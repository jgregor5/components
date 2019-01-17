package implement;

import commander.IComponent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class StorageComponent implements IComponent {
    
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
            return new JSONObject().
                    put("success", false).
                    put("error", "ilegal key format: " + key);
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
        
        try (BufferedReader br = new BufferedReader(new FileReader(getStorageFile(key)))) {
            
            return new JSONObject().
                    put("success", true).
                    put("data", new JSONObject(br.readLine()));
            
        } catch (FileNotFoundException ex) {
            return new JSONObject().
                    put("success", true).
                    put("data", new JSONObject());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
    
}
