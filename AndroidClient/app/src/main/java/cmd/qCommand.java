package cmd;

/**
 *
 * @author loveboat
 */
public class qCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public qCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.disconnectClientFromServer("q");
    }
}
