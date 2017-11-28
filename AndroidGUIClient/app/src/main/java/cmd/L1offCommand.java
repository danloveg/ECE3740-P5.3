package cmd;

/**
 *
 * @author loveboat
 */
public class L1offCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L1offCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L1off");
    }
}
