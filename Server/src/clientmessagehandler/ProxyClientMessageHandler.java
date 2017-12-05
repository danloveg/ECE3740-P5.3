package clientmessagehandler;

import java.io.IOException;

/**
 *
 * @author loveboat
 */
public class ProxyClientMessageHandler implements MessageHandler {
    private static final char TERMINATOR = 0xFFFD;
    private final server.Server myServer;
    private final client.ProxyClient myClient;
    String theCommand = "";


    public ProxyClientMessageHandler(server.Server server, client.ProxyClient client) {
        this.myServer = server;
        this.myClient = client;
    }


    /**
     * Send the client's command to handleCompleteClientMessage once the full
     * command is received.
     * @param connection The client connection object
     * @param message The message sent from the client
     * @throws IOException If the client cannot send a message to the server
     */
    @Override
    public void handleClientMessage(clientconnection.ClientConnection connection, String message) throws IOException {
        if (message.charAt(0) != TERMINATOR) { //0xFFFD = UTF-8 encoding of 0xFF
            theCommand += message;
        } else {
            handleCompleteClientMessage(connection, theCommand);
            theCommand = "";
        }
    }


    /**
     * Display an exceptional event that occurred within the client connection
     * to the server user.
     * @param exceptionalEvent Information about the exceptional event
     */
    @Override
    public void handleClientException(String exceptionalEvent) {
        myServer.sendMessageToUI(exceptionalEvent);
    }


    /**
     * Act on the client's command. Always sends the message to the server
     * connected to from the ProxyClient, but intercepts disconnect commands by
     * disconnecting the client to provide transparent service.
     * @param connection The client connection object
     * @param message The client's complete message
     * @throws IOException If the client cannot send a message to the server
     */
    public void handleCompleteClientMessage(clientconnection.ClientConnection connection, String message) throws IOException {
        switch (theCommand) {
            case "d":
            case "q":
                // We need to intercept disconnect commands to disconnect client gracefully
                myServer.sendMessageToUI("Disconnect command intercepted from client " + connection.getClientSocket().getRemoteSocketAddress());

                // Disconnect from external server
                try {
                    if (myClient.getConnected()) {
                        // Send disconnect message to server
                        myClient.sendMessageToServer(theCommand);
                        // Then disconnect the Client
                        myClient.disconnectFromServer();
                    }
                } catch (IOException ex) {
                    myServer.sendMessageToUI("Problem occurred disconnecting proxy client:\n\t" + ex.toString());
                }

                // Disconnect from proxy server
                connection.sendStringMessageToClient("Disconnect Ack: " + connection.getClientSocket().getRemoteSocketAddress());
                connection.sendMessageToClient((byte) 0xFF);
                connection.clientDisconnect();
                myServer.sendMessageToUI("\tDisconnect successful. ");
                break;
            default:
                // Every other command can be redirected straight to the external server
                myClient.sendMessageToServer(theCommand);
                break;
        }
    }
}
