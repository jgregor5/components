
package implement;

import commander.ComponentManager;
import commander.IComponent;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class InfoComponent implements IComponent {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String[] getCommands() {
        return new String[]{
            "info.components"
        };
    }

    @Override
    public JSONObject execute(JSONObject command) {        
        return new JSONObject().
            put("sucess", true).
            put("items", ComponentManager.getInstance().getComponentsInfo());
    }
    
}
