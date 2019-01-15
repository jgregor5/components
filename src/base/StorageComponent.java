/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import commander.IComponent;
import commander.IEventListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class StorageComponent implements IComponent {
    
    private static final Logger LOGGER = Logger.getLogger(StorageComponent.class.getName());

    private final Set<IEventListener> listeners;
    
    public StorageComponent() {
        this.listeners = new HashSet<>();
    }

    @Override
    public String getName() {
        return "storage";
    }

    @Override
    public Set<String> getCommands() {
        return new HashSet<>(Arrays.asList(new String[]{
            "storage.load", "storage.save", "storage.delete"
        }));
    }
    
    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }
    
    @Override
    public void registerListener(IEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public JSONObject execute(JSONObject command) {
        switch (command.getString("command")) {
            case "storage.save":
                return exsave(
                        command.getString("key"), 
                        command.getJSONObject("data"));
            case "storage.load":
                return exload(command.getString("key"));
            case "storage.delete":
                return exdelete(command.getString("key"));
            default: return null;
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
    
    private JSONObject exload(String key) {
        
        try (FileReader fr = new FileReader(getStorageFile(key))) {
            
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        return null;
    }
    
    private JSONObject exdelete(String key) {
        return null;
    }
}
