package test.sample;

import commander.ComponentManager;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import commander.IEventListener;
import commander.IComponent;
import commander.IEventSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class MyComponent implements IComponent, IEventSource {
    
    private static final Logger LOGGER = Logger.getLogger(MyComponent.class.getName());

    private final Set<IEventListener> listeners;
    
    public MyComponent() {
        this.listeners = new HashSet<>();
    }

    @Override
    public String getName() {
        return "my first component";
    }
    
    @Override
    public String[] getCommands() {
        return new String[]{"hello"};
    }

    @Override
    public void registerListener(IEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public JSONObject execute(JSONObject command) {
        
        if (command.has("command") && command.getString("command").equals("hello")) {
            
            // some example event sent
            for (IEventListener listener: this.listeners) {
                listener.handleEvent(new JSONObject().put("message", "command executed").put("command", command));
            }
            
            return new JSONObject().put("reply", "hello, there!");
        }
        
        throw new UnsupportedOperationException("Not supported");
    }
    
    public static void main(String[] args) {
        
        try (ComponentManager manager = ComponentManager.getInstance()) {
            LOGGER.log(Level.INFO, "version is {0}", 
                    manager.getClass().getPackage().getImplementationVersion());

            JSONObject command = new JSONObject().put("command", "hello");
            JSONObject result = manager.execute(command);
            LOGGER.log(Level.INFO, "result:{0}", result.toString(4));
        }
    }
}
