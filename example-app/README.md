
# Helidon Quickstart MP Example

This example implements a simple Hello World REST service using MicroProfile.

## Build and run

With JDK8+
```bash
mvn package
java -jar target/spay-app.jar
```

## Exercise the application


export OTEL_RESOURCE_ATTRIBUTES=service.name=myservice,service.version=1.0.0
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOGS_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_COMPRESSION=gzip
export OTEL_EXPORTER_OTLP_ENDPOINT=http://host.docker.internal:14318

export OTEL_EXPORTER_OTLP_HEADERS="uptrace-dsn=http://project2_secret_token@localhost:14318?grpc=14317"
export OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=DELTA
export OTEL_EXPORTER_OTLP_METRICS_DEFAULT_HISTOGRAM_AGGREGATION=BASE2_EXPONENTIAL_BUCKET_HISTOGRAM


export OTEL_EXPORTER_OTLP_ENDPOINT="http://host.docker.internal:14318"
java -javaagent:./opentelemetry-javaagent.jar -jar target/spay-app.jar 


```
curl -i -X POST http://localhost:8080/account/ \
     -H "Content-Type: application/json" \
     -d '{
           "userName": "John Doeabc"
         }'

curl -i -X POST http://localhost:8080/account/1/card \
     -H "Content-Type: application/json"

curl -i -X GET http://localhost:8080/account/1033\
     -H "Content-Type: application/json"

curl -i -X POST http://localhost:8080/payment/purchase \
     -H "Content-Type: application/json" \
     -d '{
          "cardNumber": "9533636653254488"
           "itemName": "Sample Item",
           "amount": 100
         }'

curl -i -X GET http://localhost:8080/payment/history/9533636653254488\
     -H "Content-Type: application/json"
```



## Try health and metrics

```
curl -s -X GET http://localhost:8080/health

# Prometheus Format
curl -s -X GET http://localhost:8080/metrics
```

## Build the Docker Image

```
docker build -t helidon-quickstart-mp .
```

## Start the application with Docker

```
docker run --rm -p 8080:8080 helidon-quickstart-mp:latest
```

Exercise the application as described above

## Deploy the application to Kubernetes

```
kubectl cluster-info                         # Verify which cluster
kubectl get pods                             # Verify connectivity to cluster
kubectl create -f app.yaml               # Deploy application
kubectl get service helidon-quickstart-mp  # Verify deployed service
```
