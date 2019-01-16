package test;

import commander.ComponentManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class StorageTest {
    
    private static final Logger LOGGER = Logger.getLogger(StorageTest.class.getName());
    
    public static void main(String[] args) {
        
        increment();
        loadanddelete();
    }
    
    public static void loadanddelete() {
        
        ComponentManager manager = ComponentManager.getInstance();
        
        JSONObject command = new JSONObject().
                put("command", "storage.load").
                put("key", "test.counter");
        
        JSONObject result = manager.execute(command);
        LOGGER.log(Level.INFO, "load result:{0}", result.toString(4));
        
        command = new JSONObject().
                put("command", "storage.delete").
                put("key", "test.counter");
        
        result = manager.execute(command);
        LOGGER.log(Level.INFO, "delete result:{0}", result.toString(4));
        
    }
    
    public static void increment() {
        
        ComponentManager manager = ComponentManager.getInstance();
        
        JSONObject initialData = new JSONObject().put("counter", 0);
        
        JSONObject command = new JSONObject().
                put("command", "storage.loadorsave").
                put("key", "test.counter").
                put("data", initialData);
        
        JSONObject result = manager.execute(command);
        LOGGER.log(Level.INFO, "loadorsave result:{0}", result.toString(4));
        
        if (result.getBoolean("success")) {
            JSONObject data = result.getJSONObject("data");
            data.put("counter", data.getInt("counter") + 1);
            
            command = new JSONObject().
                    put("command", "storage.save").
                    put("key", "test.counter").
                    put("data", data);
            
            result = manager.execute(command);
            LOGGER.log(Level.INFO, "save result:{0}", result.toString(4));
        }
    }
}
