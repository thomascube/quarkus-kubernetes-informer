# Quarkus Farbic8 + Vert.x Webclient / Eventbus Test Case 

This project is a minimal example of a Kubernetes informer listening on ConfigMaps and 
posting changes to the Vert.x event bus. A consumer listening on these eventbus messages
will fetch again a resource from Kubernetes.

It's a showcase to demonstrate how the fabric8 Kubernetes client fails to fetch a resource
when used in an eventbus consumer function.

Another test case is to first perform an async HTTP request using the Vert.x WebClient
and a subsequent request to Kubernetes which similarly will fail with a TimeoutException.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Steps to reproduce the fetch timeout

1. Make sure you have an active login to a Kubernetes cluster in your local kubectl config.
2. Set the Kubernetes namespace with the env var `CURRENT_NAMESPACE`
3. Start the application
4. Open the route http://localhost:8080/configmap/{name} in the browser to fetch a ConfigMap with the given name
from the Kubernetes namespace. The data block should be printed to the response.
5. Open the route http://localhost:8080/configmap/chained/{name} to perform an API request before fetching a ConfigMap.
    The request to Kubernetes will block and eventually fail with a `TimeoutException`

### Test the async fetching via EventBus
5. Edit a ConfigMap in Kubernetes
6. Watch the application logs
