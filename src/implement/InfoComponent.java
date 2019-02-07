
package implement;

import commander.ComponentManager;
import commander.IComponent;
import commander.IEventListener;
import commander.IEventSource;
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
public class InfoComponent implements IComponent, IEventSource {
    
    private static final Logger LOGGER = Logger.getLogger(InfoComponent.class.getName());

    private final Set<IEventListener> listeners;
    
    public InfoComponent() {
        this.listeners = new HashSet<>();
    }
    
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String[] getCommands() {
        return new String[]{
            "info.components", "info.sendevent"
        };
    }

    @Override
    public JSONObject execute(JSONObject command) {       
        
        JSONObject result = new JSONObject();
        
        switch (command.getString("command")) {
            
            case "info.components": 
                result.put("sucess", true).
                put("items", ComponentManager.getInstance().getComponentsInfo());
                break;
                
            case "info.sendevent":                
                JSONObject event = new JSONObject();
                event.put("source", command.getString("source"));
                event.put("type", command.getString("type"));                
                Set<String> ignoreSet = new HashSet<>(Arrays.asList(
                        new String[]{"command", "source", "type"}));                
                
                for (String name: JSONObject.getNames(command)) {
                    if (!ignoreSet.contains(name)) {
                        event.put(name, command.get(name));
                    }
                }
                sendEvent(event);
                result.put("success", true);
                break;
        }
        
        return result;        
    }

    private void sendEvent(JSONObject jo) {        
        for (IEventListener listener: this.listeners) {
            listener.handleEvent(jo);
        }
    }
    
    @Override
    public void registerListener(IEventListener el) {
        this.listeners.add(el);
        LOGGER.log(Level.CONFIG, "registered {0}", el.getClass().getName());
    }

    @Override
    public void unregisterListener(IEventListener el) {
        this.listeners.remove(el);
        LOGGER.log(Level.CONFIG, "unregistered {0}", el.getClass().getName());
    }   
    
}
