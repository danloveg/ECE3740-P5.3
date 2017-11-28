package server;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private InputStream input;
    private OutputStream output;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private clientmessagehandler.ClientMessageHandler myClientCommandHandler;
    private userinterface.UserInterface myUI;
    private int portNumber = 8765, backlog = 500;
    private boolean doListen = false;

    private boolean isProxyServer = false;
    private InetAddress proxyAddress = null;
    private int proxyPortNumber;
    private boolean isProxySet;


    /**
     * Create a new Server instance, which may or may not be a proxy server. If
     * the server is to be a proxy, the proxy port and address must be set
     * before a Client is allowed to connect; connections will be rejected if
     * there is no server to proxy to.
     * @param portNumber The port number to create the server on.
     * @param backlog The number of users that are allowed to connect to server.
     * @param myUI The user interface to be associated with this Server.
     * @param proxy true if this is to be a proxy server, false if not.
     */
    public Server(int portNumber, int backlog, userinterface.UserInterface myUI,
            boolean proxy) {
        this.portNumber = portNumber;
        this.backlog = backlog;
        this.myUI = myUI;
        this.isProxyServer = proxy;
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
                    if (isProxyServer == false) {
                        connectClientNormal();
                    } else {
                        connectClientProxy();
                    }
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
    
    public void connectClientNormal() {
        // Connect the client to this server
        clientconnection.ClientConnection myCC =
                new clientconnection.ClientConnection(clientSocket, myClientCommandHandler, this, false);
        Thread myCCthread = new Thread(myCC);
        myCCthread.start();
        sendMessageToUI("Client connected:\n\tRemote Socket Address = " +
                clientSocket.getRemoteSocketAddress() +
                "\n\tLocal Socket Address = " +
                clientSocket.getLocalSocketAddress());
    }
    
    public void connectClientProxy() {
        // Create a proxy client connection
        clientconnection.ClientConnection proxyConnection =
                new clientconnection.ClientConnection(clientSocket, myClientCommandHandler, this, true);

        // Set the proxy
        proxyConnection.setProxy(proxyAddress, proxyPortNumber);

        // Create a new Thread and run it
        Thread myCCthread = new Thread(proxyConnection);
        myCCthread.start();
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
        this.proxyAddress = address;
        this.proxyPortNumber = portNumber;
    }


    /**
     * Determine if this server is being used as a proxy.
     * @return true if server is a proxy, false is not.
     */
    public boolean getProxy() {
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