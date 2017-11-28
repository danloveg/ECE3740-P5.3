package cmd;

/**
 *
 * @author loveboat
 */
public class connectCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public connectCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.connectClientToServer();
    }
}
