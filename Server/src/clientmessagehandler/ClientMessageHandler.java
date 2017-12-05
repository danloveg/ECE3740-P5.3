/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientmessagehandler;

import clientconnection.ClientConnection;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ferens
 */
public class ClientMessageHandler implements MessageHandler {
    private static final char TERMINATOR = 0xFFFD;
    private final server.Server myServer;
    private String theCommand = "";

    public ClientMessageHandler(server.Server myServer) {
        this.myServer = myServer;
    }


    @Override
    public void handleClientMessage(clientconnection.ClientConnection myClientConnection, String msg) {
        if (msg.charAt(0) != TERMINATOR) {
            theCommand += msg;
        } else {
            handleCompleteClientMessage(myClientConnection, theCommand);
            theCommand = "";
        }
    }


    @Override
    public void handleClientException(String theExceptionalEvent) {
        myServer.sendMessageToUI(theExceptionalEvent);
    }


    public void handleCompleteClientMessage(clientconnection.ClientConnection myClientConnection, String theCommand) {
        double randomNum;
        switch (theCommand) {
            case "d":
                myServer.sendMessageToUI("Disconnect command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"Disconnect Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.clientDisconnect();
                myServer.sendMessageToUI("\tDisconnect successful. ");
                break;
            case "q":
                myServer.sendMessageToUI("Quit command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"Quit Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.clientQuit();
                myServer.sendMessageToUI("\tQuit successful. ");
                break;
            case "t":
                myServer.sendMessageToUI("Get Time command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                Calendar cal = Calendar.getInstance();
                sendMessageToClient(myClientConnection,"The time is: ");
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sendMessageToClient(myClientConnection, sdf.format(cal.getTime()));
                myServer.sendMessageToUI("\tClient given time: " + sdf.format(cal.getTime()));
                break;
            case "L1on":
                myServer.sendMessageToUI("L1on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L1on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL1on successful. ");
                break;
            case "L2on":
                myServer.sendMessageToUI("L2on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L2on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL2on successful. ");
                break;
            case "L3on":
                myServer.sendMessageToUI("L3on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L3on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL3on successful. ");
                break;
            case "L4on":
                myServer.sendMessageToUI("L4on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L4on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL4on successful. ");
                break;
            case "L1off":
                myServer.sendMessageToUI("L1off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L1off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL1off successful. ");
                break;
            case "L2off":
                myServer.sendMessageToUI("L2off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L2off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL2off successful. ");
                break;
            case "L3off":
                myServer.sendMessageToUI("L3off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L3off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL3off successful. ");
                break;
            case "L4off":
                myServer.sendMessageToUI("L4off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                sendMessageToClient(myClientConnection,"L4off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myServer.sendMessageToUI("\tL4off successful. ");
                break;
            case "gpb1":
                myServer.sendMessageToUI("gpb1 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    sendMessageToClient(myClientConnection,"PB1Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    sendMessageToClient(myClientConnection,"PB1Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myServer.sendMessageToUI("\tgpb1 successful. ");
                break;
            case "gpb2":
                myServer.sendMessageToUI("gpb2 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    sendMessageToClient(myClientConnection,"PB2Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    sendMessageToClient(myClientConnection,"PB2Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myServer.sendMessageToUI("\tgpb2 successful. ");
                break;
            case "gpb3":
                myServer.sendMessageToUI("gpb3 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    sendMessageToClient(myClientConnection,"PB3Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    sendMessageToClient(myClientConnection,"PB3Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myServer.sendMessageToUI("\tgpb3 successful. ");
                break;
        }
    }


    private void sendMessageToClient(clientconnection.ClientConnection myClientConnection, String message) {
        myClientConnection.sendStringMessageToClient(message);
        myClientConnection.sendMessageToClient((byte) 0xFF);
    }

}
