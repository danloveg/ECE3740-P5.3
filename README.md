# Android Client Communications with Java Proxy Server to MX7CK Server
### ECE3740 Fall 2017 Project 5.3

## Overview

There are three parts to this repository:
1. An Android mobile application
2. A Java Proxy server
3. A TCP/IP server made for a Microchip MX7CK development board

The Android application acts as the client. With it, you can set the IP and port of the server to connect to. You connect to the proxy server so that it fields communications to the MX7CK board. Once connected to the MX7CK board through the proxy, the LEDs can be toggled and the push button states/temperatue can be monitored.

At a high level, the server on the MX7CK is the endpoint the client connects to. It receives data from the proxy server and sends its responses to the proxy server.

## Running the Code
There are three IDEs used here: Android Studio, MPLABX, and Netbeans.

### Android Client
First open **Android Studio** and upload the client code in the **AndroidClient folder** to an Android device. The app uploaded is the client. There are various buttons you can use to interact with a server.

### MX7CK C Server 
Once the Android app is verified to be working, you will want to connect the MX7CK board to a computer via USB and Ethernet. Open **MPLABX** to upload the TCP/IP Server code in the **MX7CKServer folder** to the board.

### Java Proxy Server
Once the MX7CK server is up and running (the LED on the board should be blinking), uses **Netbeans** to run the Proxy Server code in the **Server folder**. The proxy server should be running on the machine connected to the MX7CK's Ethernet cord.

To test everything together you will want to update the port and IP address in the Android app to the port of the Proxy server and the local IP of the machine running the proxy server, respectively.
