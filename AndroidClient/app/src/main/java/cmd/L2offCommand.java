package cmd;

/**
 *
 * @author loveboat
 */
public class L2offCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L2offCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L2off");
    }
}
