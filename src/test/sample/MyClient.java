package test.sample;

import client.CommanderClient;
import commander.ComponentManager;
import commander.IEventListener;
import commander.IManager;
import commander.ManagerFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class MyClient implements IEventListener {
    
    private static final Logger LOGGER = Logger.getLogger(MyClient.class.getName());
       
    @Override
    public void handleEvent(JSONObject event) {
        LOGGER.log(Level.INFO, "client received:{0}", event.toString(4));
    }
    
    public static void main(String[] args) throws IOException {
        withManager();
    }
    
    public static void withManager() {
        
        try (IManager mgr = ManagerFactory.getManager("localhost")) {
            
            JSONObject response;

            response = mgr.execute(new JSONObject().put("command", "info.components"));
            LOGGER.log(Level.INFO, "response:{0}", response.toString(4));

            response = mgr.execute(new JSONObject().put("command", "hello").put("name", "Julian"));  
            LOGGER.log(Level.INFO, "response:{0}", response.toString(4));

            try { Thread.sleep(2000); } catch (InterruptedException ex) {}     

            response = mgr.execute(new JSONObject().put("command", "hello").put("name", "Marta"));  
            LOGGER.log(Level.INFO, "response:{0}", response.toString(4));

            response = mgr.execute(new JSONObject().put("command", "tomates"));  
            LOGGER.log(Level.INFO, "response:{0}", response.toString(4));

            LOGGER.log(Level.INFO, "sleeping...");
            try { Thread.sleep(10000); } catch (InterruptedException ex) {}
            LOGGER.log(Level.INFO, "wake up!");
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public static void withClient() {
        
        MyClient client = new MyClient();
        CommanderClient commander = new CommanderClient("localhost");
        commander.listen(ComponentManager.LISTEN_PORT);
        commander.registerListener(client);
        
        JSONObject response;        
        commander.connect(ComponentManager.COMMAND_PORT);
        
        response = commander.execute(new JSONObject().put("command", "info.components"));
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        response = commander.execute(new JSONObject().put("command", "hello").put("name", "Julian"));  
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        try { Thread.sleep(2000); } catch (InterruptedException ex) {}     
        
        response = commander.execute(new JSONObject().put("command", "hello").put("name", "Marta"));  
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        response = commander.execute(new JSONObject().put("command", "tomates"));  
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        LOGGER.log(Level.INFO, "sleeping...");
        try { Thread.sleep(10000); } catch (InterruptedException ex) {}
        LOGGER.log(Level.INFO, "wake up!");
        
        commander.disconnect();
        commander.unlisten();
    }
}
