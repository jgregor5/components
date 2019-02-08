
package implement;

import commander.IComponent;
import commander.IEventListener;
import commander.IEventSource;
import commander.IInitManager;
import commander.IManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class ScheduleComponent implements IComponent, IInitManager, IEventSource, AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(ScheduleComponent.class.getName());

    private IManager mgr;
    private final Set<IEventListener> listeners;
    private final SimpleDateFormat dateFormat;
    private final Timer timer;
    
    public ScheduleComponent() {
        
        this.timer = new Timer("schedule");
        this.listeners = new HashSet<>(); 
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
    }

    @Override
    public String getName() {
        return "schedule";
    }

    @Override
    public String[] getCommands() {
        return new String[]{
            "schedule.add", "schedule.remove", "schedule.list"
        };
    }
    
    @Override
    public void setManager(IManager mgr) {
        this.mgr = mgr;        
        init();
    }
    
    private void init() {
        
        JSONObject result = load();
        if (result.getBoolean("success")) {
            
            JSONObject data = result.getJSONObject("data");
            
            for (String key: data.keySet()) {
                JSONObject dateExecuteObj = data.getJSONObject(key);        
                String dateStr = dateExecuteObj.getString("date");
                JSONObject execute = dateExecuteObj.getJSONObject("execute");  
                
                Date date;
                try {
                    date = this.dateFormat.parse(dateStr);
                } catch (ParseException ex) {
                    throw new RuntimeException("wrong date format " + dateStr);
                }
                
                this.timer.schedule(new ExecuteTask(key, execute), date);
                LOGGER.log(Level.CONFIG, "scheduled at {0}: {1}", new Object[]{date, execute});
            }
        }        
    }
    
    @Override
    public void close() {  
        this.timer.cancel();
        LOGGER.log(Level.CONFIG, "timer cancelled");
    }

    @Override
    public JSONObject execute(JSONObject command) {
        
        String cmd = command.getString("command");        
        switch (cmd) {
            case "schedule.add":
                return add(command.getString("date"), command.getJSONObject("execute"));
            case "schedule.remove":
                return remove(command.getString("key"));
            case "schedule.list":
                return list();
            default: 
                throw new UnsupportedOperationException("unsupported command " + cmd);
        }
    }
        
    private JSONObject add(String dateStr, JSONObject execute) { 
        
        // SOME CHECKS
        
        if (!execute.has("command")) {
            throw new RuntimeException("missing command value");
        }
        
        Date date;
        try {
            date = this.dateFormat.parse(dateStr);            
        } catch (ParseException ex) {
            throw new RuntimeException("wrong date format");
        }
        
        // LOAD AND SAVE
        
        JSONObject result = load();    
        
        if (result.getBoolean("success")) {                        
            JSONObject data = result.getJSONObject("data");
            String uniqueID = UUID.randomUUID().toString();              
            JSONObject dateExecuteObj = new JSONObject().
                    put("date", dateStr).
                    put("execute", execute);            
            data.put(uniqueID, dateExecuteObj);
                    
            result = save(data);            
            if (result.getBoolean("success")) {                
                this.timer.schedule(new ExecuteTask(uniqueID, execute), date);                
                return new JSONObject().put("success", true).put("key", uniqueID);
            }
        }        
        
        return result;
    }
    
    private JSONObject remove(String key) {   
        
        JSONObject result = load();
        
        if (result.getBoolean("success")) {            
            JSONObject data = result.getJSONObject("data");
            if (!data.has(key)) {
                return new JSONObject().
                    put("success", false).
                    put("message", "unknown key " + key);
            }
            
            data.remove(key);
            result = save(data);
            
            if (result.getBoolean("success")) {
                return new JSONObject().put("success", true);
            }
        }
        
        return result;
    }

    private JSONObject list() {   
        
        JSONObject result = load();
        
        if (result.getBoolean("success")) {
            return new JSONObject().
                put("success", true).
                put("items", result.getJSONObject("data"));
        }
        
        return result;
    }
    
    // LISTENER
    
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
    
    // UTILS

    private JSONObject load() {
        
        JSONObject command = new JSONObject().
                put("command", "storage.load").
                put("key", "schedule.map");        
        return this.mgr.execute(command);
    }
    
    private JSONObject save(JSONObject data) {
        
        JSONObject command = new JSONObject().
                put("command", "storage.save").
                put("key", "schedule.map").
                put("data", data);            
        return this.mgr.execute(command);
    }
    
    // EXECUTE TASK

    private class ExecuteTask extends TimerTask {

        private final String key;
        private final JSONObject execute;
        
        public ExecuteTask(String key, JSONObject execute) {
            this.key = key;
            this.execute = execute;
        }
        
        @Override
        public void run() {
            
            try {
                JSONObject result = ScheduleComponent.this.mgr.execute(this.execute);
                
                JSONObject event = new JSONObject().
                        put("source", "schedule").
                        put("type", "execute").
                        put("result", result);
                
                sendEvent(event);                
                
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "executing " + execute.toString(), t);
                
            } finally {
                
                boolean removed = false;
                
                JSONObject result = load();
                if (result.getBoolean("success")) {
                    JSONObject data = result.getJSONObject("data");
                    if (data.has(this.key)) {
                        data.remove(this.key);
                        result = save(data);
                        if (result.getBoolean("success")) {
                            removed = true;
                        }
                    }
                }
                
                if (!removed) {
                    LOGGER.log(Level.SEVERE, "failed to remove {0}", this.key);
                }
            }
        }

    }
}
