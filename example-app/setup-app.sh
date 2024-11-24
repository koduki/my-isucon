64 bytes from vm-app001.asia-northeast1-a.c.ren-studio-ai.internal (10.146.0.2): icmp_seq=4 ttl=64 time=0.034 ms


[postgres@vm-app002 ~]$ psql
psql (14.13)
Type "help" for help.

postgres=# ALTER USER postgres WITH PASSWORD 'password';
ALTER ROLE

sudo dnf update -y
sudo dnf clean all
sudo dnf install -y java-1.8.0-openjdk-devel maven
sudo dnf install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-8-x86_64/pgdg-redhat-repo-latest.noarch.rpm
sudo dnf install -y postgresql14 postgresql14-server

sudo /usr/pgsql-14/bin/postgresql-14-setup initdb
sudo systemctl start postgresql-14
sudo systemctl status postgresql-14

sudo -i -u postgres
psql
postgres=# ALTER USER postgres WITH PASSWORD 'password';
exit

sudo vi /var/lib/pgsql/14/data/postgresql.conf
listen_addresses = '*'


sudo systemctl restart postgresql-14
sudo systemctl status postgresql-14


psql -h localhost -U postgres -d postgres

export JAVA_X_SQL_DATASOURCE_PAYMENTDS_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
export OTEL_RESOURCE_ATTRIBUTES=service.name=myservice,service.version=1.0.0
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOGS_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_COMPRESSION=gzip
export OTEL_EXPORTER_OTLP_ENDPOINT=http://10.146.0.6:14318

export OTEL_EXPORTER_OTLP_HEADERS="uptrace-dsn=http://project2_secret_token@localhost:14318?grpc=14317"
export OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE=DELTA
export OTEL_EXPORTER_OTLP_METRICS_DEFAULT_HISTOGRAM_AGGREGATION=BASE2_EXPONENTIAL_BUCKET_HISTOGRAM

## install docker

sudo dnf -y install dnf-plugins-core
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install docker-ce docker-ce-cli containerd.io
sudo systemctl start docker
sudo systemctl enable docker

sudo docker ps
sudo docker compose up

# Load Tester

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

# Monitoring

curl -O https://gist.githubusercontent.com/kurokobo/25e41503eb060fee8d8bec1dd859eff3/raw/0d7cd29472f0eaa26ce424071456ad84b24fb318/installer.sh
bash ./installer.sh
source ~/.bashrc
docker-compose version