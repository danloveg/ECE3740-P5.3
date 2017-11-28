package cmd;

/**
 *
 * @author loveboat
 */
public class gpb2Command implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public gpb2Command (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("gpb2");
    }
}
