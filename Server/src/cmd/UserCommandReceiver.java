/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author loveboat
 */
public class UserCommandReceiver {
    private final client.Client myClient;
    private final userinterface.UserInterface UI;

    public UserCommandReceiver(userinterface.UserInterface ui, client.Client client) {
        UI = ui;
        myClient = client;
    }

    /**
     * Try to connect the client to the server if they aren't already connected.
     */
    protected void connectClientToServer() {
        if (false == myClient.getConnected()) {
            try {
                // Try to connect to server
                myClient.connectToServer();
                // Then create the Client Thread and start it
                Thread clientThread = new Thread(myClient);
                clientThread.start();
            } catch (IOException ex) {
                UI.update("Could not create a connection to server.");
            }

        } else {
            // We are already connected
            UI.update("Already connected!");
        }
    }


    /**
     * Try to disconnect the client from the server if they are connected to a
     * server.
     * @param disconnectString The disconnect code that will be sent to the
     * server
     */
    protected void disconnectClientFromServer(String disconnectString) {
        if (true == myClient.getConnected()) {
            try {
                // Send disconnect message q or d to server
                sendStringToServer(disconnectString);
                // Then disconnect the Client
                myClient.disconnectFromServer();
            } catch (IOException ex) {
                UI.update("Disconnection error: " + ex.toString());
            }

        } else if (false == disconnectString.equals("q")) {
            UI.update("No connected server.");
        }
        // If we want to quit, close the program as well
        if (disconnectString.equals("q")) {
            System.exit(0);
        }
    }


    /**
     * Use the client to send a String to a connected server.
     * @param message The message to send
     */
    protected void sendStringToServer(String message) {
        if (true == myClient.getConnected()) {
            try {
                myClient.sendMessageToServer(message);
            } catch (IOException e) {
                UI.update("Could not send message to server.");
                disconnectClientFromServer("d");
            }
        } else {
            UI.update("No connected server.");
        }
    }


    /**
     * Try to set the Client's IP to a new address. Tries to convert the string
     * to an address, if there is an UnknownHostException, the IP will not be
     * updated.
     * @param newIPAddress The string containing the IP address
     */
    protected void setClientIP(String newIPAddress) {
        // Try to create a new IP address
        try {
            InetAddress address = InetAddress.getByName(newIPAddress);
            myClient.setIpAddress(address);
            UI.update("IP set to: " + newIPAddress);
        } catch (UnknownHostException e) {
            UI.update("Could not create IP: " + newIPAddress);
        }
    }


    /**
     * Try to set the Client's port to a new number. Converts the string to a
     * number and uses that as the new port number. If there is a
     * NumberFormatException, it will not update the port.
     * @param newPortNumber The string containing the port number
     */
    protected void setClientPort(String newPortNumber) {
        try {
            int port = Integer.parseInt(newPortNumber);
            myClient.setPort(port);
            UI.update("Port number set to " + newPortNumber);
        } catch (NumberFormatException e) {
            UI.update(newPortNumber + " is not a number.");
        }
    }
}
