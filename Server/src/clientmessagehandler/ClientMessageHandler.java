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

    server.Server myServer;
    String theCommand = "";

    public ClientMessageHandler(server.Server myServer) {
        this.myServer = myServer;
    }


    @Override
    public void handleClientMessage(clientconnection.ClientConnection myClientConnection, String msg) {
        //if (!msg.equals("255")) {
        if (msg.charAt(0)!=0xFFFD) { //0xFFFD = UTF-8 encoding of 0xFF
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
                myClientConnection.sendStringMessageToClient("Disconnect Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myClientConnection.clientDisconnect();
                myServer.sendMessageToUI("\tDisconnect successful. ");
                break;
            case "q":
                myServer.sendMessageToUI("Quit command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("Quit Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myClientConnection.clientQuit();
                myServer.sendMessageToUI("\tQuit successful. ");
                break;
            case "t":
                myServer.sendMessageToUI("Get Time command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                Calendar cal = Calendar.getInstance();
                myClientConnection.sendStringMessageToClient("The time is: ");
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                for (int i = 0; i < sdf.format(cal.getTime()).length(); i++) {
                    byte msg;
                    msg = (byte) sdf.format(cal.getTime()).charAt(i);
                    myClientConnection.sendMessageToClient(msg);
                }
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tClient given time: " + sdf.format(cal.getTime()));
                break;
            case "L1on":
                myServer.sendMessageToUI("L1on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L1on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL1on successful. ");
                break;
            case "L2on":
                myServer.sendMessageToUI("L2on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L2on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL2on successful. ");
                break;
            case "L3on":
                myServer.sendMessageToUI("L3on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L3on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL3on successful. ");
                break;
            case "L4on":
                myServer.sendMessageToUI("L4on command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L4on Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL4on successful. ");
                break;
            case "L1off":
                myServer.sendMessageToUI("L1off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L1off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL1off successful. ");
                break;
            case "L2off":
                myServer.sendMessageToUI("L2off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L2off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL2off successful. ");
                break;
            case "L3off":
                myServer.sendMessageToUI("L3off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L3off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL3off successful. ");
                break;
            case "L4off":
                myServer.sendMessageToUI("L4off command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendStringMessageToClient("L4off Ack: " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tL4off successful. ");
                break;
            case "gpb1":
                myServer.sendMessageToUI("gpb1 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    myClientConnection.sendStringMessageToClient("PB1Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    myClientConnection.sendStringMessageToClient("PB1Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tgpb1 successful. ");
                break;
            case "gpb2":
                myServer.sendMessageToUI("gpb2 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    myClientConnection.sendStringMessageToClient("PB2Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    myClientConnection.sendStringMessageToClient("PB2Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tgpb2 successful. ");
                break;
            case "gpb3":
                myServer.sendMessageToUI("gpb3 command received from client " + myClientConnection.getClientSocket().getRemoteSocketAddress());
                randomNum = Math.random();
                if (randomNum >= 0.5) {
                    myClientConnection.sendStringMessageToClient("PB3Down" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                } else {
                    myClientConnection.sendStringMessageToClient("PB3Up" + myClientConnection.getClientSocket().getRemoteSocketAddress());
                }
                myClientConnection.sendMessageToClient((byte) 0xFF);
                myServer.sendMessageToUI("\tgpb3 successful. ");
                break;
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

}
