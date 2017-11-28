package cmd;

/**
 *
 * @author loveboat
 */
public class L4offCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L4offCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L4off");
    }
}
