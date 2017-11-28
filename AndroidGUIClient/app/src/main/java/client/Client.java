package client;

import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import servermessagehandler.ServerMessageHandler;
import java.util.concurrent.TimeoutException;

/**
 * Client to connect to server via TCP/IP socket.
 *
 * @author Daniel Lovegrove
 */
public class Client implements Runnable {
    private static final int TIMEOUT_MILLIS = 2000;

    private int portNumber;
    private Socket clientSocket = null;
    private InetAddress ipAddress = null;
    private final userinterface.Userinterface UI;
    private servermessagehandler.ServerMessageHandler commandHandler;
    private boolean connected = false;
    private boolean disconnectWaiting = false;

    /**
     * Creates an instance with an associated port number and user interface
     * @param portNumber The port number
     * @param ui The user interface
     */
    public Client(int portNumber, userinterface.Userinterface ui, InetAddress ipAddress) {
        this.portNumber = portNumber;
        this.UI = ui;
        this.ipAddress = ipAddress;
    }


    // Connect to the server at the address and port number
    public void connectToServer() throws IOException {
        if (false == getConnected()) {
            // Create a socket
            clientSocket = new Socket();

            // Try to connect to the server
            clientSocket.connect(new InetSocketAddress(ipAddress, portNumber), TIMEOUT_MILLIS);

            // Create a new command handler
            this.commandHandler = new ServerMessageHandler(clientSocket);

            // Mark Client as "connected"
            setConnected(true);
            clientConnected();
        }
    }


    /**
     * Disconnect from the server. Closes the client connection to the server
     * and closes the Server Command Handler
     * @throws IOException
     */
    public void disconnectFromServer() throws IOException {
        // Wait for the disconnection acnowledgement.
        try {
            waitForDisconnectAck();
        } catch (TimeoutException e) {
            UI.update("Server connection timed out. Closing connection immediately.");
        } finally {
            // Close the socket
            if (null != this.clientSocket) {
                clientSocket.close();
                clientSocket = null;
            }

            // Close the command handler
            if (null != this.commandHandler) {
                this.commandHandler.close();
                this.commandHandler = null;
            }

            // Mark client as "Not connected."
            setConnected(false);
            clientDisconnected();
        }
    }


    /**
     * Uses the server command handler to send a message to the server.
     * @param message The message to send.
     * @throws IOException
     */
    public void sendMessageToServer(String message) throws IOException {
        commandHandler.sendStringToServer(message);
    }


    /**
     * Reads messages from the server.
     */
    @Override
    public void run() {
        while (true == getConnected()) {
            try {
                String msg = this.commandHandler.readStringFromServer();
                UI.update(msg);

                // If we were waiting for the message to finish sending to
                // disconnect, notify anything waiting that the server has
                // acknowledged us.
                if (true == getDisconnectWaiting()) {
                    setDisconnectWaiting(false);
                    setConnected(false);
                }
            } catch (IOException e) {
                if (true == getConnected()) {
                    serverNotResponding(e);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Callback Methods
    // -------------------------------------------------------------------------

    /**
     * Notify the user that they are connected.
     */
    public void clientConnected() {
        UI.update("Client connected to server on port " + this.portNumber);
    }


    /**
     * Notify the user that they have disconnected.
     */
    public void clientDisconnected() {
        UI.update("Client disconnected from server on port " + this.portNumber);
    }


    /**
     * Wait for the server to stop sending data. Sets disconnectWaiting to true,
     * and waits until the Thread reading from the server puts it to false. If
     * it takes too long, a TimeoutException is thrown. Since we are blocking
     * here, this is necessary.
     * @throws TimeoutException
     */
    public void waitForDisconnectAck() throws TimeoutException {
        // We are now waiting for a disconnection
        setDisconnectWaiting(true);
        long startTime = System.currentTimeMillis();

        while (true == getDisconnectWaiting()) {
            if (System.currentTimeMillis() - startTime > TIMEOUT_MILLIS) {
                setDisconnectWaiting(false);
                throw new TimeoutException();
            }
        }
    }


    /**
     * Act on the server not responding.
     * @param e The exception thrown from trying to communicate with the server
     */
    public void serverNotResponding(IOException e) {
        UI.update("Could not read from server: " + e.toString());
        UI.update("Disconnecting...");

        try {
            this.disconnectFromServer();
        } catch (IOException ex) {
            // Trouble closing the socket, nullify it as a last resort
            clientSocket = null;
        }
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public void setPort(int newPort) { portNumber = newPort; }
    public int getPort()             { return portNumber; }

    public void setIpAddress(InetAddress newAddress) { ipAddress = newAddress; }
    public InetAddress getIpAddress() { return ipAddress; }

    public synchronized void setConnected(boolean connected) { this.connected = connected; }
    public synchronized boolean getConnected() { return connected; }

    public synchronized void setDisconnectWaiting(boolean waiting) { disconnectWaiting = waiting; }
    public synchronized boolean getDisconnectWaiting() { return disconnectWaiting; }
}