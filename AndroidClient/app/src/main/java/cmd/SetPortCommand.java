package cmd;

/**
 * Created by loveboat on 24/11/17.
 */

public class SetPortCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public SetPortCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String port) {
        receiver.setClientPort(port);
    }
}
