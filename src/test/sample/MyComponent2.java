package test.sample;

import commander.ComponentManager;
import commander.IComponent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class MyComponent2 implements IComponent {
    
    private static final Logger LOGGER = Logger.getLogger(MyComponent2.class.getName());
    
    public MyComponent2() {
    }

    @Override
    public String getName() {
        return "my second component";
    }

    @Override
    public String[] getCommands() {
        return new String[]{"bye"};
    }

    @Override
    public JSONObject execute(JSONObject command) {
        if ("bye".equals(command.getString("command"))) {
            return new JSONObject().put("response", "bye for now");
        }
        
        throw new UnsupportedOperationException("Not supported");
    }
    
    public static void main(String[] args) {
        
        try (ComponentManager manager = ComponentManager.getInstance()) {
            LOGGER.log(Level.INFO, "version is {0}", 
                    manager.getClass().getPackage().getImplementationVersion());

            JSONObject command = new JSONObject().put("command", "bye");
            JSONObject result = manager.execute(command);
            LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
        }
    }    
    
}
