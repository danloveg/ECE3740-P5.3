package server;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private final clientmessagehandler.ClientMessageHandler myClientCommandHandler;
    public userinterface.UserInterface myUI;
    private int portNumber = 8765, backlog = 500;
    private boolean doListen = false;

    private boolean isProxyServer = false;
    private InetAddress proxyAddress = null;
    private int proxyPortNumber;


    /**
     * Create a new normal Server instance, so that clients can connect to this
     * server.
     * @param portNumber The port number to create the server on.
     * @param backlog The number of users that are allowed to connect to server.
     * @param myUI The user interface to be associated with this Server.
     */
    public Server(int portNumber, int backlog, userinterface.UserInterface myUI) {
        this.portNumber = portNumber;
        this.backlog = backlog;
        this.myUI = myUI;
        this.myClientCommandHandler = new clientmessagehandler.ClientMessageHandler(this);
    }


    /**
     * Create a new proxy Server instance, so that clients can connect to an
     * external server (routed through this server).
     * @param portNumber The port number for this server.
     * @param backlog The number of users that are allowed to connect to server.
     * @param myUI The user interface to be associated with this Server.
     * @param proxyAddress The address to use to connect to proxy server.
     * @param proxyPortNumber The port number of the external server.
     */
    public Server(int portNumber, int backlog, userinterface.UserInterface myUI, InetAddress proxyAddress, int proxyPortNumber) {
        this.portNumber = portNumber;
        this.backlog = backlog;
        this.myUI = myUI;
        this.proxyAddress = proxyAddress;
        this.proxyPortNumber = proxyPortNumber;
        this.isProxyServer = true;
        this.myClientCommandHandler = new clientmessagehandler.ClientMessageHandler(this);
    }


    public synchronized void setDoListen(boolean doListen){
        this.doListen = doListen;
    }
    public void startServer() {
        if (serverSocket != null) {
            stopServer();
        } else {
            try {
                serverSocket = new ServerSocket(portNumber, backlog);
            } catch (IOException e) {
                sendMessageToUI("Cannot create ServerSocket, because " + e +". Exiting program.");
                System.exit(1);
            } finally {
            }
        }

    }

    public void stopServer() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                sendMessageToUI("Cannot close ServerSocket, because " + e +". Exiting program.");
                System.exit(1);
            } finally {
            }

        }
    }

    public void listen() {
        try {
            setDoListen(true);
            serverSocket.setSoTimeout(500);
            Thread myListenerThread = new Thread(this);
            myListenerThread.start();     
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopListening() {
        setDoListen(false);
    }


    /**
     * Accept Clients connecting, and 
     */
    @Override
    public void run() {
        while (true) {
            if (doListen == true) {
                try {
                    clientSocket = serverSocket.accept();
                    connectClient();
                } catch (SocketException | SocketTimeoutException e) {
                    //check doListen.
                    if (doListen == false) {
                        myUI.update(e.toString());
                    }
                } catch (IOException e) {
                    myUI.update(e.toString());
                }finally {
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {}
            }
        }
    }


    /**
     * Create a normal or proxy connection to this server.
     */
    public void connectClient() {
        clientconnection.ClientConnection myCC;

        if (true == isProxyServer) {
            // Connect the client to an external server
            myCC = new clientconnection.ClientConnection(clientSocket, this, proxyAddress, proxyPortNumber);
            myCC.connectProxyClient();

        } else {
            // Otherwise, connect client to this server
            myCC = new clientconnection.ClientConnection(clientSocket, myClientCommandHandler, this);
        }

        // Create a new Thread and run it
        Thread myCCthread = new Thread(myCC);
        myCCthread.start();

        // Notify the server user that a client has connected.
        sendMessageToUI("Client connected:\n\tRemote Socket Address = " +
                clientSocket.getRemoteSocketAddress() +
                "\n\tLocal Socket Address = " +
                clientSocket.getLocalSocketAddress());
    }


    /**
     * Set the server's port.
     * @param portNumber The port to create the server socket on.
     */
    public void setPort(int portNumber) {
        this.portNumber = portNumber;
    }


    /**
     * Get the port that the server is using.
     * @return The port number currently being used for the server socket.
     */
    public int getPort() {
        return this.portNumber;
    }


    /**
     * Set the address to use and the port number of the server to proxy to.
     * @param address The address to use to connect to the server.
     * @param portNumber The port to use to connect to the server.
     */
    public void setProxy(InetAddress address, int portNumber) {
        if (true == isProxyServer) {
            this.proxyAddress = address;
            this.proxyPortNumber = portNumber;
        } else {
            myUI.update("Trying to set proxy info for non-proxy server.");
        }
    }


    /**
     * Determine if this server is being used as a proxy.
     * @return true if server is a proxy, false is not.
     */
    public boolean getProxyStatus() {
        return this.isProxyServer;
    }


    /**
     * Display a message to the server user.
     * @param theString The message to display.
     */
    public void sendMessageToUI(String theString) {
        myUI.update(theString);
    }
}