
# Helidon Quickstart MP Example

This example implements a simple Hello World REST service using MicroProfile.

## Build and run

With JDK8+
```bash
mvn package
java -jar target/spay-app.jar
```

## Exercise the application

```
curl -i -X POST http://localhost:8080/account/ \
     -H "Content-Type: application/json" \
     -d '{
           "userName": "John Doe"
         }'

curl -i -X POST http://localhost:8080/account/1/card \
     -H "Content-Type: application/json"

curl -i -X GET http://localhost:8080/account/1033\
     -H "Content-Type: application/json"

curl -i -X POST http://localhost:8080/payment/purchase \
     -H "Content-Type: application/json" \
     -d '{
           "card": {
             "cardNumber": "9533636653254488"
           },
           "itemName": "Sample Item",
           "amount": 100
         }'

curl -i -X GET http://localhost:8080/payment/history/9533636653254488\
     -H "Content-Type: application/json"
```

```
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list

sudo apt-get update
sudo apt-get install k6

k6 version
k6 run --vus 1 --iterations 1 
k6 run functional-test.js

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
