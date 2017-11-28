package commandinterface;

/**
 * @author loveboat
 */

public interface Command {
    /**
     * Execute the command.
     * @param cmd The command to execute.
     */
    void execute(String cmd);
}
