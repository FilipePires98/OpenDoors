import paho.mqtt.publish as publish
import smbus
import time
 
MQTT_SERVER = "deti-engsoft-02.ua.pt"
MQTT_PATH = "24sensors-data"

while True:
	bus = smbus.SMBus(1)
	bus.write_byte_data(0x39, 0x00 | 0x80, 0x03)
	bus.write_byte_data(0x39, 0x01 | 0x80, 0x02)
	time.sleep(0.5)
	data = bus.read_i2c_block_data(0x39, 0x0C | 0x80, 2)
	data1 = bus.read_i2c_block_data(0x39, 0x0E | 0x80, 2)
	ch0 = data[1] * 256 + data[0]
	ch1 = data1[1] * 256 + data1[0]
	
	res='{"data_type":1, "data":{"store":1, "light":{"infrared":[%d],"visible":[%d]}}}' % (ch1,(ch0-ch1))

	publish.single(MQTT_PATH, res, hostname=MQTT_SERVER)
	
	time.sleep(3)
