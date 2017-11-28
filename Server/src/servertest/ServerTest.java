package servertest;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ferens, loveboat
 */
public class ServerTest {

    public static void main(String[] args) {

        standardiouserinterface.StandardIO myUI = new standardiouserinterface.StandardIO();
        InetAddress proxyClientAddress = null;

        try {
            proxyClientAddress = InetAddress.getByName("192.168.1.214");
        } catch (UnknownHostException e) {
            myUI.update("Could not create IP address.");
        }

        server.Server myServer = new server.Server(8765, 1, myUI, proxyClientAddress, 7777);
        usercommandhandler.UserCommandHandler myUserCommandHandler = new usercommandhandler.UserCommandHandler(myUI, myServer);
        myUI.setCommand(myUserCommandHandler);
        Thread myUIthread = new Thread(myUI);
        myUIthread.start();     
        myUI.update("1:\tQuit\n2:\tlisten\n3:\tSet Port\n4:\tGet Port\n5:\tStop listening\n6:\tStart Server Socket\n");
    }
}
