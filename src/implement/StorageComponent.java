package implement;

import commander.ComponentManager;
import commander.IComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author julian
 */
public class StorageComponent implements IComponent {
    
    private static final Logger LOGGER = Logger.getLogger(StorageComponent.class.getName());
    
    public StorageComponent() {
    }

    @Override
    public String getName() {
        return "storage";
    }

    @Override
    public String[] getCommands() {
        return new String[]{
            "storage.load", "storage.save", "storage.delete", "storage.loadorsave"
        };
    }
    
    @Override
    public JSONObject execute(JSONObject command) {
        
        String cmd = command.getString("command");
        String key = command.getString("key");
        if (!key.matches("^[-_.A-Za-z0-9]+$")) {
            return new JSONObject().put("success", false).put("error", "ilegal key format");
        }
        
        switch (cmd) {
            case "storage.save":
                return exsave(key, command.getJSONObject("data"));
            case "storage.load":
                return exload(key);
            case "storage.delete":
                return exdelete(key);
            case "storage.loadorsave":                
                return exloadorsave(key, command.getJSONObject("data"));                
            default: 
                throw new UnsupportedOperationException("unsupported command " + cmd);
        }
    }    
    
    private File getStorageFolder() {
         return new File(System.getProperty("user.dir"), "storage");
    }
    
    private File getStorageFile(String key) {
                
        File folder = getStorageFolder();
        if (!folder.exists()) {
             folder.mkdir();
        }
        
        return new File(folder, key);
    }
    
    private JSONObject exsave(String key, JSONObject data) {
        
        try (FileWriter fw = new FileWriter(getStorageFile(key))) {
            fw.write(data.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return new JSONObject().put("success", true);
    }
    
    private JSONObject exloadorsave(String key, JSONObject data) {
        
        File file = getStorageFile(key);
        if (file.exists()) {
            return exload(key);
        }
        else {
            JSONObject result = exsave(key, data);
            result.put("data", data);
            return result;
        }
    }
    
    private JSONObject exload(String key) {
        
        try {
            JSONTokener tokener = new JSONTokener(new FileInputStream(getStorageFile(key)));
            return new JSONObject().put("success", true).put("data", new JSONObject(tokener));

        } catch (FileNotFoundException ex) {
            return new JSONObject().put("success", true).put("data", new JSONObject());
        }
    }
        
    private JSONObject exdelete(String key) {
        
        File file = getStorageFile(key);
        
        if (!file.exists() || file.delete()) {
            return new JSONObject().put("success", true);
        }
        else {
            return new JSONObject().
                    put("success", false).
                    put("error", "could not delete " + file.getAbsolutePath());
        }
    }
    
    public static void main(String[] args) {
        
        increment();
        loadanddelete();
    }
    
    public static void loadanddelete() {
        
        ComponentManager manager = ComponentManager.getInstance();
        
        JSONObject command = new JSONObject().
                put("command", "storage.load").
                put("key", "test.counter");
        
        JSONObject result = manager.execute(command);
        LOGGER.log(Level.INFO, "load result:{0}", result.toString(4));
        
        command = new JSONObject().
                put("command", "storage.delete").
                put("key", "test.counter");
        
        result = manager.execute(command);
        LOGGER.log(Level.INFO, "delete result:{0}", result.toString(4));
        
    }
    
    public static void increment() {
        
        ComponentManager manager = ComponentManager.getInstance();
        
        JSONObject initialData = new JSONObject().put("counter", 0);
        
        JSONObject command = new JSONObject().
                put("command", "storage.loadorsave").
                put("key", "test.counter").
                put("data", initialData);
        
        JSONObject result = manager.execute(command);
        LOGGER.log(Level.INFO, "loadorsave result:{0}", result.toString(4));
        
        if (result.getBoolean("success")) {
            JSONObject data = result.getJSONObject("data");
            data.put("counter", data.getInt("counter") + 1);
            
            command = new JSONObject().
                    put("command", "storage.save").
                    put("key", "test.counter").
                    put("data", data);
            
            manager.execute(command);
            LOGGER.log(Level.INFO, "save result:{0}", result.toString(4));
        }
    }
}
