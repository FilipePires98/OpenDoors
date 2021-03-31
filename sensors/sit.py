#!/usr/bin/env python

import paho.mqtt.publish as publish
import RPi.GPIO as GPIO, time, os      

DEBUG = 1
GPIO.setmode(GPIO.BCM)

MQTT_SERVER = "deti-engsoft-02.ua.pt"
MQTT_PATH = "24sensors-data"


def getInput (RCpin):
        time.sleep(0.1)
        GPIO.setup(RCpin, GPIO.IN)
	return GPIO.input(RCpin)
control=1

while True:                                     
        seated=getInput(18) #seating in pin 18
	if seated!=control:
		control=seated
        	res='{"data_type":4, "data":{"store":1, "pressure":[%d]}}' % (seated)
	        publish.single(MQTT_PATH, res, hostname=MQTT_SERVER)
