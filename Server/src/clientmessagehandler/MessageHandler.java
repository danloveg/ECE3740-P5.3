package clientmessagehandler;

import java.io.IOException;

/**
 *
 * @author loveboat
 */
public interface MessageHandler {
    public abstract void handleClientMessage(
            clientconnection.ClientConnection connection,
            String message) throws IOException;
    
    public abstract void handleClientException(String exceptionalEvent);
}
