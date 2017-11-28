package cmd;

/**
 *
 * @author loveboat
 */
public class gpb3Command implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public gpb3Command (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("gpb3");
    }
}
