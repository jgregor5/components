
package test.listener;

import commander.IEventListener;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class AListener implements IEventListener {
    
    private String name;
    
    public AListener(String name) {
        this.name = name;
    }

    @Override
    public void handleEvent(JSONObject event) {
        System.out.println(name + " received " + event.toString(4));
    }
    
}
