package cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import commandinterface.Command;

/**
 *
 * @author loveboat
 */
public class UserCommandInvoker implements commandinterface.Command {
    private commandinterface.Command userCommand;
    private final cmd.UserCommandReceiver commandReceiver;
    private final userinterface.Userinterface UI;
    private final ExecutorService executor;

    public UserCommandInvoker(userinterface.Userinterface ui, client.Client client) {
        this.UI = ui;
        // Allow four threads to be ran concurrently
        this.executor = Executors.newFixedThreadPool(4);
        // myClient = client;
        this.commandReceiver = new cmd.UserCommandReceiver(ui, client);
    }


    /**
     * Takes a String and dynamically creates the class with name cmd.<command>Command where
     * <command> is the name of the name of the command.
     * A String parameter can be passed to the class created by passing a space delimited string
     * where the first token is the command name and the second token is the parameter to pass to
     * the class's execute method.
     * @param command The command to execute.
     */
    @Override
    public void execute(final String command) {
        if (!command.equals("")) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    // Split command into tokens (space delimited)
                    String[] commandTokens = command.split("\\s+");
                    String secureCommand = "cmd." + commandTokens[0] + "Command";

                    try {
                        // Create a parameter array for the constructor of the object
                        Class[] parameters = new Class[1];
                        parameters[0] = UserCommandReceiver.class;

                        // Get the Class corresponding to the command
                        Class newObject = Class.forName(secureCommand);

                        // Get the class's constructor pattern
                        Constructor<Command> objConstructor =
                                newObject.getDeclaredConstructor(parameters);

                        // Create a new instance of the Class
                        userCommand = objConstructor.newInstance(commandReceiver);

                        // Execute the command, pass the parameter if there is one.
                        if (commandTokens.length == 1) {
                            userCommand.execute(secureCommand);
                        } else {
                            userCommand.execute(commandTokens[1]);
                        }

                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        UI.update("Invalid command.");
                    } catch (InstantiationException | IllegalAccessException |
                            InvocationTargetException ex) {
                        UI.update("Error instantiating command class: " + ex.toString());
                    }
                }
            });
        }
    }
}
