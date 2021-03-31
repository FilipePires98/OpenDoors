FROM amd64/ubuntu

COPY target/OpenDoors_DataProcessor-1.0-SNAPSHOT.jar /IES24/OpenDoors_DataProcessor-1.0-SNAPSHOT.jar
COPY 24properties /IES24/24properties

RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "openjdk-8-jre"]

ENTRYPOINT ["java", "-jar", "/IES24/OpenDoors_DataProcessor-1.0-SNAPSHOT.jar"]

