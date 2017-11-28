package cmd;

/**
 *
 * @author loveboat
 */
public class L2onCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public L2onCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("L2on");
    }
}
