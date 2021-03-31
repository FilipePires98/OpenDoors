#!/bin/bash

cp 24properties OpenDoors_DataProcessor/24properties
cd OpenDoors_DataProcessor
mvn package
cd ..

cp 24properties OpenDoors_Persistence/24properties
cd OpenDoors_Persistence
mvn package
cd ..

cp 24properties OpenDoors_DeviceController/24properties
cd OpenDoors_DeviceController
mvn package
cd ..

cp 24properties VirtualLight/24properties
cd VirtualLight
mvn package
cd ..

cp 24properties AirConditioner/24properties
cd AirConditioner
mvn package
cd ..


while IFS= read -r var
do
  kafka="$(grep -oP 'kafka-broker>>\K.*')"
done < "24properties"

while IFS= read -r var
do
  mosquitto="$(grep -oP 'mosquitto-broker>>\K.*')"
done < "24properties"


sed -i "s/^bootstrap.servers=PLAINTEXT:\/\/.*/bootstrap.servers=PLAINTEXT:\/\/$kafka/" ExternalComponents/Kafka_Proxy/kafka-rest.properties
sed -i "s/^bootstrap.servers=.*/bootstrap.servers=$kafka/" ExternalComponents/Mqtt_Connector/connect.properties
sed -i "s/^connect.mqtt.hosts=tcp:\/\/.*/connect.mqtt.hosts=tcp:\/\/$mosquitto/" ExternalComponents/Mqtt_Connector/mqtt-source-confluent.properties

rm ./ExternalComponents/Kafka_Proxy/confluent.zip
zip -r ./ExternalComponents/Kafka_Proxy/confluent.zip ./ExternalComponents/Kafka_Proxy/confluent-5.0.1/

rm ./ExternalComponents/Mqtt_Connector/kafka.zip
zip -r ./ExternalComponents/Mqtt_Connector/kafka.zip ./ExternalComponents/Mqtt_Connector/kafka_2.11-2.1.0/
