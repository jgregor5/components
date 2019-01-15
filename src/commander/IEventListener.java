package commander;

import org.json.JSONObject;

/**
 *
 * @author julian
 */
public interface IEventListener {
    
    void handleEvent(JSONObject event);
}
