package commander;

import org.json.JSONObject;

/**
 *
 * @author julian
 */
public interface ICommander {
        
    JSONObject execute(JSONObject command);
}
