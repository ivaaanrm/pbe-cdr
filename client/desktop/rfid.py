###########################################################################
###########################################################################
#### This example shows connecting to the PN532 with UART.             ####
#### We are using the adafruit library.                                ####
#### This program will read an rfid card and show it's uid on console. ####
###########################################################################
###########################################################################


import serial
from adafruit_pn532.uart import PN532_UART
from time import sleep

class Rfid: 
    def __init__(self):
        # UART connection:
        self.uart = serial.Serial("/dev/ttyS0", baudrate=115200, timeout=0.5)
        self.pn532 = PN532_UART(self.uart, debug=False)
        # Configure PN532 to communicate with MiFare cards
        self.pn532.SAM_configuration()

    
    def read_uid(self):
        while True:
            # Check if a card is available to read
            uid = self.pn532.read_passive_target(timeout=0.5)
            # Try again until card is available.
            if uid:
                return uid.hex().upper()



if __name__ == "__main__":
    # Init Rfid
    rf = Rfid()
    print("Waiting for RFID/NFC card...")
    # Infinnte loop until a key is pressed
    try:
        while True:
            # Read the UID
            uid = rf.read_uid()
            # Print the UID number on console
            print("Found card with UID:", uid)
            # Wait one second to make the next reading
            sleep(1)
    except KeyboardInterrupt:
        pass 
