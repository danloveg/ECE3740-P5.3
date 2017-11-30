package usercommandhandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ferens, loveboat
 */
public class UserCommandHandler {
    private userinterface.UserInterface myUI;
    private server.Server myServer;
    private ExecutorService threadPool;


    /**
     * Create a new instance of a server user command handler.
     * @param myUI The user interface connected to the server
     * @param myServer The server instance itself
     */
    public UserCommandHandler(userinterface.UserInterface myUI, server.Server myServer) {
        this.myUI = myUI;
        this.myServer = myServer;
        threadPool = Executors.newFixedThreadPool(4);
    }


    /**
     * Handle the server user's command. All commands are processed in a thread
     * in this object's thread pool to reduce lag in the server user's UI.
     * @param theCommand The command that the server user issued
     */
    public void handleUserCommand(String theCommand) {
        threadPool.submit(() -> {
            String[] commandTokens = theCommand.split("\\s+");
            switch (commandTokens[0]) {
                case "1": //QUIT
                    if (commandTokens.length == 1) {
                        myServer.stopServer();
                        myUI.update("Quiting program by User command.");
                        System.exit(-1);
                    } else {
                        myUI.update("Quit does not accept any parameters.");
                    }
                    break;
                case "2": //LISTEN
                    if (commandTokens.length == 1) {
                        myServer.listen();
                        myUI.update("Server is now listening, ...");
                    } else {
                        myUI.update("Listen does not accept any parameters.");
                    }
                    break;
                case "3": //SET PORT
                    if (commandTokens.length == 2) {
                        setServerPort(commandTokens[1]);
                    } else {
                        myUI.update("Set port accepts 1 and only 1 parameter.");
                    }
                    break;
                case "4": //GET PORT
                    if (commandTokens.length == 1) {
                        myUI.update("The port number is: " +String.valueOf(myServer.getPort()));
                    break;
                    } else {
                        myUI.update("Get port does not accept any parameters.");
                    }
                case "5": //Stop Listening
                    if (commandTokens.length == 1) {
                        myServer.stopListening();
                        myUI.update("Server is not listening, ...");
                    } else {
                        myUI.update("Stop listening does not accept any parameters.");
                    }
                    break;
                case "6": //START SERVER SOCKET
                    if (commandTokens.length == 1) {
                        myServer.startServer();
                        myUI.update("Server Socket has been created.");
                    } else {
                        myUI.update("Create socket does not accept any parameters.");
                    }
                    break;
                case "":
                    break;
                default:
                    myUI.update("Command \"" + theCommand + "\" not recognized.");
                    break;
            }
        });
    }
    
    
    /**
     * Try to set the server's port to the string value that the user inputted.
     * Port numbers must be integers between 1023 and 65536. If the number is
     * not between these, or a NumberFormatException occurs, the server's port
     * number is not updated.
     * @param newPort The port the user entered.
     */
    void setServerPort(String newPort) {
        try {
            int portNumber = Integer.parseInt(newPort);
            if (portNumber < 1024) {
                myUI.update("Port numbers 0-1023 are reserved.");
            } else if (portNumber > 65535) {
                myUI.update("Port numbers greater than 65535 are not allowed.");
            } else if (portNumber >= 1024 && portNumber <= 65535) {
                myServer.setPort(portNumber);
                myUI.update("Server port set to " + portNumber);
            }
        } catch (NumberFormatException e) {
            myUI.update(newPort + " is not a number.");
        }
    }
}
