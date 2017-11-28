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
public interface MessageHandler {
    public abstract void handleClientMessage(
            clientconnection.ClientConnection connection,
            String message);
    
    public abstract void handleClientException(String exceptionalEvent);
}
