package cmd;

/**
 *
 * @author loveboat
 */
public class L3onCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L3onCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L3on");
    }
}
