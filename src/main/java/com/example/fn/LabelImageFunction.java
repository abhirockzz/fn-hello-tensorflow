package com.example.fn;

import com.google.common.io.ByteStreams;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

public class LabelImageFunction {

    public String classify(byte[] image) {

        String result = null;
        List<String> labels = null;
        try {
            labels = loadLabels();
        } catch (Throwable ex) {
            String err = "Could not load labels - " + ex.getMessage();
            System.err.println(err);
            result = err;
            return result;
        }

        System.err.println("Loaded labels....");

        Session session = null;
        Graph graph = null;
        try {
            graph = new Graph();
            session = new Session(graph);
            graph.importGraphDef(loadGraphDef());

            System.err.println("Loaded graph definition....");

            float[] probabilities = null;
            byte[] bytes = null;
            try {
                //bytes = ByteStreams.toByteArray(LabelImageFunction.class.getClassLoader().getResourceAsStream(filename));
                bytes = ByteStreams.toByteArray(new ByteArrayInputStream(image));
            } catch (Throwable ex) {
                String err = "Failed to read file - " + ex.getMessage();
                System.err.println(err);
                result = err;
                return result;
            }
            System.err.println("loaded image....");

            try (Tensor<String> input = Tensors.create(bytes);
                    Tensor<Float> output
                    = session
                            .runner()
                            .feed("encoded_image_bytes", input)
                            .fetch("probabilities")
                            .run()
                            .get(0)
                            .expect(Float.class)) {
                if (probabilities == null) {
                    probabilities = new float[(int) output.shape()[0]];
                }
                output.copyTo(probabilities);
                int label = argmax(probabilities);
                /*System.err.printf(
                        "%-30s --> %-15s (%.2f%% likely)\n",
                        filename, labels.get(label), probabilities[label] * 100.0);*/
                result = "This is a '" + labels.get(label) + "' Accuracy - " + Math.round(probabilities[label] * 100.0) +"%";
                System.out.println("Processing result - " + result);
            }
        } catch (Throwable ex) {
            System.err.println(ex);
            result = "Error - " + ex.getMessage();
        }
        /*finally {
            if (graph != null)  {
                graph.close();
                System.err.println("closed graph...");
            }
        }*/
        return result;
    }

    private static byte[] loadGraphDef() throws IOException {
        try (InputStream is = LabelImageFunction.class.getClassLoader().getResourceAsStream("graph.pb")) {
            return ByteStreams.toByteArray(is);
        }
    }

    private static ArrayList<String> loadLabels() throws IOException {
        ArrayList<String> labels = new ArrayList<String>();
        String line;
        final InputStream is = LabelImageFunction.class.getClassLoader().getResourceAsStream("labels.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
        }
        return labels;
    }

    private static int argmax(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

}
