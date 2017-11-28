package cmd;

import java.lang.reflect.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author loveboat
 */
public class UserCommandInvoker implements commandinterface.Command {
    private commandinterface.Command userCommand;
    private final cmd.UserCommandReceiver commandReceiver;
    private final userinterface.UserInterface UI;
    private final ExecutorService executor;

    public UserCommandInvoker(userinterface.UserInterface ui, client.Client client) {
        this.UI = ui;
        // Allow four threads to be ran concurrently
        this.executor = Executors.newFixedThreadPool(4);
        this.commandReceiver = new UserCommandReceiver(ui, client);
    }

    @Override
    public void execute(final String command) {
        if (!command.equals("")) {
            executor.submit(() -> {
                String secureCommand = "cmd." + command + "Command";
                try {
                    // Create a parameter array for the constructor of the object
                    Class[] parameters = new Class[1];
                    parameters[0] = cmd.UserCommandReceiver.class;

                    // Get the Class corresponding to the command
                    Class newObject = Class.forName(secureCommand);

                    // Get the class's constructor pattern
                    Constructor<commandinterface.Command> objCtor =
                            newObject.getDeclaredConstructor(parameters);

                    // Create a new instance of the Class
                    userCommand = objCtor.newInstance(this.commandReceiver);

                    // Execute the command
                    userCommand.execute(secureCommand);

                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    UI.update("Invalid command.");
                } catch(InstantiationException | IllegalAccessException |
                        InvocationTargetException ex) {
                    UI.update("Error instantiating command class: " + ex.toString());
                }
            });
        }
    }
}
