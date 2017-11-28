package cmd;

/**
 * @author loveboat
 */
public class SetIPCommand implements commandinterface.Command {
    private final cmd.UserCommandReceiver receiver;

    public SetIPCommand (cmd.UserCommandReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String IP) {
        receiver.setClientIP(IP);
    }
}
