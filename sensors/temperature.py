
#!/usr/bin/env python

import paho.mqtt.publish as publish
import time

MQTT_SERVER = "deti-engsoft-02.ua.pt"
MQTT_PATH = "24sensors-data"


while True:
	f=open("/sys/bus/w1/devices/22-000000229fcd/w1_slave", "r")
	temp=int(f.read().split("t=")[1].strip())                                     
	res='{"data_type":2, "data":{"store":1, "temperature":[%d]}}' % (temp/1000)
	publish.single(MQTT_PATH, res, hostname=MQTT_SERVER)
	time.sleep(3)
