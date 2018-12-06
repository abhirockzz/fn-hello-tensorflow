FROM fnproject/fn-java-fdk-build:jdk9-1.0.75 as build-stage
WORKDIR /function
ENV MAVEN_OPTS -Dhttp.proxyHost= -Dhttp.proxyPort= -Dhttps.proxyHost= -Dhttps.proxyPort= -Dhttp.nonProxyHosts= -Dmaven.repo.local=/usr/share/maven/ref/repository
ADD pom.xml /function/pom.xml
RUN ["mvn", "package", "dependency:copy-dependencies", "-DincludeScope=runtime", "-DskipTests=true", "-Dmdep.prependGroupId=true", "-DoutputDirectory=target", "--fail-never"]

ARG TENSORFLOW_VERSION=1.12.0
RUN echo "using tensorflow version " $TENSORFLOW_VERSION

RUN curl -LJO https://storage.googleapis.com/tensorflow/libtensorflow/libtensorflow-$TENSORFLOW_VERSION.jar
RUN curl -LJO https://storage.googleapis.com/tensorflow/libtensorflow/libtensorflow_jni-cpu-linux-x86_64-$TENSORFLOW_VERSION.tar.gz
RUN tar -xvzf libtensorflow_jni-cpu-linux-x86_64-$TENSORFLOW_VERSION.tar.gz


ADD src /function/src
RUN ["mvn", "package"]

FROM fnproject/fn-java-fdk:jdk9-1.0.75
ARG TENSORFLOW_VERSION=1.12.0
WORKDIR /function
COPY --from=build-stage /function/libtensorflow_jni.so /function/runtime/lib
COPY --from=build-stage /function/libtensorflow_framework.so /function/runtime/lib
COPY --from=build-stage /function/libtensorflow-$TENSORFLOW_VERSION.jar /function/app/
COPY --from=build-stage /function/target/*.jar /function/app/
CMD ["com.example.fn.LabelImageFunction::classify"]
