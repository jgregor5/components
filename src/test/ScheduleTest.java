
package test;

import commander.IManager;
import commander.ManagerFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class ScheduleTest {
    
    private static final Logger LOGGER = Logger.getLogger(ScheduleTest.class.getName());
    
    public static void main(String[] args) {
        
        try (IManager mgr = ManagerFactory.getManager(null)) {

            JSONObject execute;
            
            list(mgr);
            remove(mgr, "f6aedeba-3ae4-4973-934c-e36146f29e3b");            
            execute = new JSONObject().put("command", "info.components");
            
            //add(mgr, createDate(8, 49, 45), execute);
            //add(mgr, createDate(8, 59, 0), execute);
            
            Thread.sleep(1000 * 30);
            
            list(mgr);
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private static void add(IManager mgr, Date date, JSONObject execute) {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));        
        String dateStr = dateFormat.format(date);
        
        JSONObject command = new JSONObject().
                put("command", "schedule.add").
                put("date", dateStr).
                put("execute", execute);
        JSONObject result = mgr.execute(command);
        LOGGER.info(result.toString(4));     
    }
    
    private static void remove(IManager mgr, String key) {
        
        JSONObject command = new JSONObject().put("command", "schedule.remove").put("key", key);
        JSONObject result = mgr.execute(command);
        LOGGER.info(result.toString(4));
    }
    
    private static void list(IManager mgr) {
        
        JSONObject command = new JSONObject().put("command", "schedule.list");
        JSONObject result = mgr.execute(command);
        LOGGER.info(result.toString(4));
    }
    
    private static Date createDate(int hour, int mins, int secs) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, secs);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }    
}
