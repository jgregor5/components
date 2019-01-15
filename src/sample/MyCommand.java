package sample;

import commander.ComponentManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class MyCommand {
    
    private static final Logger LOGGER = Logger.getLogger(MyCommand.class.getName());
 
    public static void main(String[] args) {
        
        ComponentManager service = ComponentManager.getInstance();
        
        JSONObject command = new JSONObject().put("command", "hello");
        JSONObject result = service.execute(command);
        LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
                
        command = new JSONObject().put("command", "bye");
        result = service.execute(command);
        LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
        
        command = new JSONObject().put("command", "whoami");
        result = service.execute(command);
        LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
    }
}
