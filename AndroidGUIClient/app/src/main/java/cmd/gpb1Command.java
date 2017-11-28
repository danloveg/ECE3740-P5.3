package cmd;

/**
 *
 * @author loveboat
 */
public class gpb1Command implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public gpb1Command (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("gpb1");
    }
}
