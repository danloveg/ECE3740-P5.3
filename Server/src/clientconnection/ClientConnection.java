/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ferens
 */
public class ClientConnection implements Runnable {

    InputStream input;
    OutputStream output;
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    clientmessagehandler.MessageHandler myClientCommandHandler;
    server.Server myServer;
    boolean stopThisThread = false;

    InetAddress proxyAddress = null;
    int proxyPortNumber;
    boolean isProxyConnection = false;
    client.ProxyClient myProxyClient = null;
    userinterface.UserInterface serverMessageInterceptor;


    /**
     * Create a normal client connection to the server.
     * @param clientSocket
     * @param myClientCommandHandler
     * @param myServer
     */
    public ClientConnection(Socket clientSocket,
            clientmessagehandler.MessageHandler myClientCommandHandler,
            server.Server myServer) {

        this.clientSocket = clientSocket;
        this.myClientCommandHandler = myClientCommandHandler;
        this.myServer = myServer;

        try {
            input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            myServer.sendMessageToUI("Cannot create IO streams; exiting program.");
            System.exit(1);
        }
    }


    /**
     * Create a connection to another server, with a specific address and port
     * number.
     * @param clientSocket The socket the client is connected to
     * @param myServer The server the client is currently connected to
     * @param proxyAddress The address to use for a new client
     * @param proxyPortNumber The port number of the server to route communications to
     */
    public ClientConnection(Socket clientSocket,
            server.Server myServer,
            InetAddress proxyAddress,
            int proxyPortNumber) {

        this.clientSocket = clientSocket;
        this.myServer = myServer;
        this.proxyAddress = proxyAddress;
        this.proxyPortNumber = proxyPortNumber;

        try {
            input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            myServer.sendMessageToUI("Cannot create IO streams; exiting program.");
            System.exit(1);
        }

        this.serverMessageInterceptor = new MessageInterceptor(this.output);
        this.myProxyClient = new client.ProxyClient(proxyPortNumber, serverMessageInterceptor, proxyAddress);
        this.myClientCommandHandler = new clientmessagehandler.ProxyClientMessageHandler(this.myServer, this.myProxyClient);

        this.isProxyConnection = true;
    }


    @Override
    public void run() {
        byte msg;
        String theClientMessage;

        while (stopThisThread == false) {
            try {
                msg = (byte) input.read();
                theClientMessage = byteToString(msg);
                myClientCommandHandler.handleClientMessage(this, theClientMessage);
            } catch (IOException e) {
                myClientCommandHandler.handleClientException("IOException: "
                        + e.toString()
                        + ". Stopping thread and disconnecting client: "
                        + clientSocket.getRemoteSocketAddress());
                disconnectClient();
                stopThisThread = true;
            }
        }
    }


    private String byteToString(byte theByte) {
        byte[] theByteArray = new byte[1];
        String theString = null;
        theByteArray[0] = theByte;
        try {
            theString = new String(theByteArray, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            myServer.sendMessageToUI("Cannot convert from UTF-8 to String; exiting program.");
            System.exit(1);
        } finally {
            return theString;
        }
    }


    public void sendMessageToClient(byte msg) {
        try {
            output.write(msg);
            output.flush();
        } catch (IOException e) {
            myServer.sendMessageToUI("cannot send to socket; exiting program.");
            System.exit(1);
        } finally {
        }
    }


    public void sendStringMessageToClient(String theMessage) {
        for (int i = 0; i < theMessage.length(); i++) {
            byte msg = (byte) theMessage.charAt(i);
            sendMessageToClient(msg);
        }
    }

    public void clientQuit() {
        disconnectClient();
    }

    public void clientDisconnect() {
        disconnectClient();
    }

    public void disconnectClient() {
        try {
            stopThisThread = true;
            clientSocket.close();
            clientSocket = null;
            input = null;
            output = null;
        } catch (IOException e) {
            myServer.sendMessageToUI("cannot close client socket; exiting program.");
            System.exit(1);
        } finally {
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }


    /**
     * Set the address to use and the port number of the server to proxy to.
     * @param address The address to use to connect to the server.
     * @param portNumber The port to use to connect to the server.
     */
    public void setProxy(InetAddress address, int portNumber) {
        if (this.isProxyConnection) {
            this.proxyAddress = address;
            this.proxyPortNumber = portNumber;
            this.serverMessageInterceptor = new MessageInterceptor(this.output);
        } else {
            myServer.sendMessageToUI("Trying to set proxy info for a normal connection.");
        }
    }


    public boolean getProxy() {
        return this.isProxyConnection;
    }


    private class MessageInterceptor implements userinterface.UserInterface {
        PrintWriter output;

        public MessageInterceptor(OutputStream output) {
            this.output = new PrintWriter(output, true);
        }

        @Override
        public void update(String message) {
            output.print(message);
        }
    }
}
