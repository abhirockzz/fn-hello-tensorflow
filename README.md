# Serverless image classification with functions & TensorFlow

Demonstrates a serverless function capable of simple image classification using TensorFlow

Throw random images at your function and expect it to (hopefully) return something sane !
Just like this image (of what looks like a pizza) when passed on to our classification function returns -  `This is a 'pizza' Accuracy - 96%`

![pizza? sure ?](https://cdn-images-1.medium.com/max/2000/1*Vrayow-T48w8ARLjHkPqdA.png)

## Pre-requisites

- Get latest FN CLI - `curl -LSs https://raw.githubusercontent.com/fnproject/cli/master/install | sh`
- Configure your environment i.e. setup Fn context as well as OCI configuration for Oracle Functions

This is what a context file looks like:

    api-url: https://functions.us-phoenix-1.oraclecloud.com oracle.compartment-id: <OCI_compartment_OCID>
    oracle.profile: <profile_name_in_OCI_config>
    provider: oracle
    registry: <OCI_docker_registry>

.. and here is an example configuration file:

    [ORACLE_FUNCTIONS_USER] 
    user=ocid1.user.oc1..exampleuniqueID 
    fingerprint=72:00:22:7f:d3:8b:47:a4:58:05:b8:95:84:31:dd:0e 
    key_file=/.oci/admin_key.pem 
    tenancy=ocid1.tenancy.oc1..exampleuniqueID 
    pass_phrase=s3cr3t 
    region=us-phoenix-1

- Switch to the correct context according to your Functions development environment: `fn use context <context_name>`

## Create the Application

- `git clone https://github.com/abhirockzz/fn-hello-tensorflow`
- `cd fn-hello-tensorflow` 
- `fn create app <app_name> --annotation oracle.com/oci/subnetIds='["<subnet_ocid>"]'`

## Deploy the function

If you want to use TensorFlow version 1.12.0 (for Java SDK and corresponding native libraries), use the following command: `fn -v deploy --app fn-tensorflow-app`

You can also choose a specific version. Ensure that you specify it in `pom.xml` file before you build the function. For example, if you want to use version 1.11.0:
`fn -v deploy --app fn-tensorflow-app --build-arg TENSORFLOW_VERSION=1.11.0`

When the deployment completes successfully, your function is ready to use. Use the `fn ls apps` command to list down the applications currently deployed. `fn-tensorflow-app` should be listed

## Test

All you need to do is pass the image to the function while invoking it:

`cat <path to image> | fn invoke fn-tensorflow-app classify`

For the fun part, check out the `Time to Classify Images!` section in the corresponding blog post - https://medium.com/@abhishek1987/serverless-image-classification-with-oracle-functions-and-tensorflow-849395786110

