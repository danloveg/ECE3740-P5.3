# ECE3740-P5.3: Android Client Communications with Java Proxy Server to MX7CK Server

## Overview

There are three parts to this repository:
1. An Android mobile application
2. A Java Proxy server
3. A TCP/IP server made for a Microchip MX7CK development board

The Android application acts as the client. With it, you can set the IP and port of the server to connect to. Once connected to the MX7CK board, the LEDs can be toggled and the push button states and the temperatue can be monitored.
The Java proxy server can be run on any machine that can be connected to the MX7CK board through ethernet. When it runs, it fields all communication directly to the MX7CK board connected to the machine. In this way, it is a proxy to the MX7CK server.
The TCP/IP server for the MX7CK is the endpoint the client connects to. It receives data from the proxy server and sends responses to the proxy server.

## Running the Code

To run everything together, first open **Android Studio** and upload the client code in the **AndroidClient folder** to an Android device. The app uploaded is the client. Then, you will want to connect the MX7CK board to a computer via USB and Ethernet. Open **MPLABX** to upload the TCP/IP Server code in the **MX7CKServer folder** to the board. Finally, in class we used **Netbeans** to run the Proxy Server code in the **Server folder**.

You will want to update the port and IP address in the Android app to the port of the Proxy server and the local IP of the machine running the proxy server.
