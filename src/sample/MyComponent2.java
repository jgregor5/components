package sample;

import commander.ComponentManager;
import commander.IComponent;
import commander.IEventListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class MyComponent2 implements IComponent {
    
    private static final Logger LOGGER = Logger.getLogger(MyComponent2.class.getName());
    
    private Set<IEventListener> listeners;
    
    public MyComponent2() {
        this.listeners = new HashSet<>();
    }

    @Override
    public String getName() {
        return "my second component";
    }

    @Override
    public Set<String> getCommands() {
        return new HashSet<>(Arrays.asList(new String[]{"bye"}));
    }

    @Override
    public JSONObject execute(JSONObject command) {
        if ("bye".equals(command.getString("command"))) {
            return new JSONObject().put("response", "bye for now");
        }
        
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void registerListener(IEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }
    
    public static void main(String[] args) {
        
        ComponentManager manager = ComponentManager.getInstance();        
        LOGGER.log(Level.INFO, "version is {0}", manager.getClass().getPackage().getImplementationVersion());
        
        JSONObject command = new JSONObject().put("command", "bye");
        JSONObject result = manager.execute(command);
        LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
    }    
    
}
