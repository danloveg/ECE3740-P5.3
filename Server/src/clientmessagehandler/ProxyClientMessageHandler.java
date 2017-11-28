/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientmessagehandler;

/**
 *
 * @author loveboat
 */
public class ProxyClientMessageHandler implements MessageHandler {
    server.Server myServer;
    client.ProxyClient myClient;
    String theCommand = "";
    
    public ProxyClientMessageHandler(server.Server server, client.ProxyClient client) {
        this.myServer = server;
        this.myClient = client;
    }
    
    @Override
    public void handleClientMessage(clientconnection.ClientConnection connection, String message) {
        // TODO: Implement
    }
    
    @Override
    public void handleClientException(String exceptionalEvent) {
        myServer.sendMessageToUI(exceptionalEvent);
    }
}
