package commander;

import client.CommanderClient;

/**
 *
 * @author julian
 */
public class ManagerFactory {
    
    private ManagerFactory() {}
    
    public static IManager getManager(String host) {
        
        if (host == null) {
            return ComponentManager.getInstance();
        }
        else {
            CommanderClient cc = new CommanderClient(host);
            cc.connect(ComponentManager.COMMAND_PORT);
            cc.listen(ComponentManager.LISTEN_PORT);
            return cc;
        }
    }
}
