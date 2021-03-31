#!/usr/bin/env python

import paho.mqtt.publish as publish
import RPi.GPIO as GPIO, time, os      
import time

DEBUG = 1
GPIO.setmode(GPIO.BCM)

MQTT_SERVER = "deti-engsoft-02.ua.pt"
MQTT_PATH = "24sensors-data"


def getInput (RCpin):
        time.sleep(0.1)
        GPIO.setup(RCpin, GPIO.IN)
	return GPIO.input(RCpin)


prev=0
while True:                                     
        entered=getInput(17) #entering in pin 17
	if entered==0 and prev==1:
        	res= '{"data_type":3, "data":{"store":1, "moviment":1}}'
	        publish.single(MQTT_PATH, res, hostname=MQTT_SERVER)
	prev=entered
	time.sleep(2)
