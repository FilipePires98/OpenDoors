FROM amd64/ubuntu
COPY kafka.zip /IES24/kafka.zip
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "unzip"]
RUN ["apt-get", "install", "-y", "default-jre"]
RUN ["unzip", "/IES24/kafka.zip", "-d", "/IES24/"]

ENTRYPOINT ["/IES24/kafka_2.11-2.1.0/bin/connect-standalone.sh", "/IES24/kafka_2.11-2.1.0/connect.properties", "/IES24/kafka_2.11-2.1.0/mqtt-source-confluent.properties"]
