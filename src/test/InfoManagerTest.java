
package test;

import commander.ComponentManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class InfoManagerTest {
    
    private static final Logger LOGGER = Logger.getLogger(InfoManagerTest.class.getName());
    
    private static final String HOST = null;
    //private static final String HOST = "146.255.96.104";
    
    public static void main(String[] args) {
        
        try (ComponentManager mgr = ComponentManager.getInstance()) {
            //try (IManager mgr = ManagerFactory.getManager(HOST)) {
        
            JSONObject response = mgr.execute(
                    new JSONObject().put("command", "info.components"));  
            LOGGER.log(Level.INFO, "response:{0}", response.toString(4));
        
            Thread.sleep(5000);
            LOGGER.log(Level.INFO, "done!");
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
