package cmd;

/**
 *
 * @author loveboat
 */
public class tCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public tCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String cmd) {
        receiver.sendStringToServer("t");
    }
}
