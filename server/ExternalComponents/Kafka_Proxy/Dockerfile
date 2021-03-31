FROM amd64/ubuntu

COPY confluent.zip /IES24/confluent.zip

RUN ["apt-get", "update"]
RUN ["apt-get", "install", "unzip"]
RUN ["apt-get", "install", "-y", "default-jre"]
RUN ["unzip", "/IES24/confluent.zip", "-d", "/IES24/"]


EXPOSE 8082

ENTRYPOINT ["/IES24/confluent-5.0.1/bin/kafka-rest-start", "/IES24/confluent-5.0.1/etc/kafka-rest/kafka-rest.properties"]
