
package test.listener;

import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class AListenerTest {
    
    public static void main(String[] args) {
        
        ASource source = new ASource();
        
        AListener listener1 = new AListener("listener1");
        AListener listener2 = new AListener("listener2");

        source.sendEvent(new JSONObject().put("event", "1"));
        
        source.registerListener(listener1);
        
        source.sendEvent(new JSONObject().put("event", "2"));
        
        source.registerListener(listener2);
        
        source.sendEvent(new JSONObject().put("event", "3"));
        
        source.unregisterListener(listener2);
        
        source.sendEvent(new JSONObject().put("event", "4"));
        
        source.unregisterListener(listener1);
        
        source.sendEvent(new JSONObject().put("event", "5"));
    }
}
