
package test.listener;

import commander.IEventListener;
import commander.IEventSource;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class ASource implements IEventSource {
    
    private Set<IEventListener> listeners;
    
    public ASource() {
        this.listeners = new HashSet<>();
    }

    @Override
    public void registerListener(IEventListener listener) {
        System.out.println("register: " + this.listeners.add(listener));
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        System.out.println("unregister: " + this.listeners.remove(listener));
    }
    
    public void sendEvent(JSONObject event) {
        
        for (IEventListener listener: listeners) {
            listener.handleEvent(event);
        }
    }
    
}
