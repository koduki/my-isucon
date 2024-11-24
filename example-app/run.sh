export monitoring_ip=host.docker.internal

export OTEL_RESOURCE_ATTRIBUTES=service.name=myservice,service.version=1.0.0
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOGS_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_COMPRESSION=gzip
export OTEL_EXPORTER_OTLP_ENDPOINT=http://${monitoring_ip}:14318

export OTEL_EXPORTER_OTLP_HEADERS="uptrace-dsn=http://project2_secret_token@localhost:14318?grpc=14317"
export OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=DELTA
export OTEL_EXPORTER_OTLP_METRICS_DEFAULT_HISTOGRAM_AGGREGATION=BASE2_EXPONENTIAL_BUCKET_HISTOGRAM

# export JAVAX_SQL_DATASOURCE_PAYMENTDS_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
export JAVAX_SQL_DATASOURCE_PAYMENTDS_DATASOURCE_URL=jdbc:postgresql://database:5432/postgres

java -javaagent:./opentelemetry-javaagent.jar -jar target/spay-app.jar 
