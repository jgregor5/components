package sample;

import client.CommanderClient;
import commander.IEventListener;
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
        
        MyClient client = new MyClient();
        CommanderClient commander = new CommanderClient("localhost");
        commander.listen(9001);
        commander.registerListener(client);
        
        JSONObject response;        
        commander.connect(9000);
        
        response = commander.execute(new JSONObject().put("command", "info.components"));
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        response = commander.execute(new JSONObject().put("command", "hello"));  
        LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
        try { Thread.sleep(2000); } catch (InterruptedException ex) {}     
        
        response = commander.execute(new JSONObject().put("command", "hello"));  
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
