package cmd;

/**
 *
 * @author loveboat
 */
public class L3offCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L3offCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L3off");
    }
}
