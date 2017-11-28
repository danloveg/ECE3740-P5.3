package cmd;

/**
 *
 * @author loveboat
 */
public class L4onCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L4onCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L1off");
    }
}
