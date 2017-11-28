package servermessagehandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author loveboat
 */
public class ServerMessageHandler {
    private static final char TERMINATOR = 0xFFFD; // UTF-8 encoding of 0xFF
    private BufferedReader input;
    private PrintWriter output;


    /**
     * Create an instance with an associated client, socket, and interface.
     * Throws IOException if it cannot get the client's input and output streams.
     * @param clientSocket The socket the client is connected on
     * @throws IOException
     */
    public ServerMessageHandler(Socket clientSocket) throws IOException {
        this.input = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
    }


    /**
     * Blocking method that reads an n-character String from the server.
     * @return The byte from the server.
     * @throws IOException
     */
    public String readStringFromServer() throws IOException {
        StringBuilder message = new StringBuilder();
        boolean terminatorFound = false;

        while (false == terminatorFound) {
            if (input != null && input.ready()) {
                // Get a byte from the server
                char serverByte = (char) input.read();
                // Check whether the terminator byte has come in yet
                if (serverByte == TERMINATOR) {
                    terminatorFound = true;
                } else {
                    message.append(serverByte);
                }
            }
        }

        return message.toString();
    }


    /**
     * Write a message to the connected server.
     * @param message The byte to write to the server.
     * @throws IOException
     */
    public void sendStringToServer(String message) throws IOException {
        if (output != null) {
            output.print(message);
            output.print(TERMINATOR);
            output.flush();
        }
    }


    /**
     * Close associated input and output streams to avoid memory leaks.
     * @throws IOException
     */
    public void close() throws IOException {
        if (input != null) {
            input.close();
            input = null;
        }
        if (output != null) {
            output.close();
            output = null;
        }
    }
}
