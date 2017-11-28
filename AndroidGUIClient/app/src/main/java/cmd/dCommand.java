package cmd;

/**
 *
 * @author loveboat
 */
public class dCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public dCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.disconnectClientFromServer("d");
    }
}
