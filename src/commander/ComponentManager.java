package commander;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class ComponentManager implements IEventListener, IComponent {
    
    private final static Logger LOGGER = Logger.getLogger(ComponentManager.class.getName());
    
    private static ComponentManager service;
    private ServiceLoader<IComponent> loader;
    private Map<String, IComponent> commands;
    private Set<IEventListener> listeners;

    private ComponentManager() {        
        this.loader = ServiceLoader.load(IComponent.class);
        this.listeners = new CopyOnWriteArraySet<>();
        init();
    }
    
    public static synchronized ComponentManager getInstance() {
        if (service == null) {
            service = new ComponentManager();
        }
        return service;
    }
    
    private void init() {
        
        configureLogging();
        
        this.commands = new HashMap<>();
        Iterator<IComponent> components = this.loader.iterator();
        while (components.hasNext()) {
            IComponent component = components.next();
            for (String command: component.getCommands()) {
                if (this.commands.put(command, component) != null) {
                    throw new RuntimeException("command already registered: " + command);
                }
            }
            
            component.registerListener(this);
            
            Package packinfo = component.getClass().getPackage();
            String implVersion = packinfo == null? "?" : packinfo.getImplementationVersion();
            if (implVersion == null) {
                implVersion = "?.?";
            }
            
            LOGGER.log(Level.CONFIG, "registered {0} from {1} version {2}", 
                    new Object[]{component.getCommands(), component.getName(), implVersion});     
        }
    }
    
    @Override
    public synchronized void handleEvent(JSONObject event) {
        // events from components to a (network server?)
        LOGGER.log(Level.INFO, "event:{0}", event.toString(4));  
        for (IEventListener listener: listeners) {
            listener.handleEvent(event);
        }
    }
    
    @Override
    public void registerListener(IEventListener listener) {
        // may be used by a network server
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<String> getCommands() {
        return this.commands.keySet();
    }

    @Override
    public JSONObject execute(JSONObject command) {
        
        if (!command.has("command")) {
            throw new RuntimeException("command not found in " + command);
        }
        
        String commandStr = command.getString("command");
        ICommander commander = this.commands.get(commandStr);
        if (commander == null) {
            throw new RuntimeException("commander not found for " + command);
        }
        else {
            // TODO synchronized here?
            return commander.execute(command);
        }
    }
    
    private static void configureLogging() {
        
        File loggingConfigFile = new File(System.getProperty("user.dir"), "logging.properties");
        if (loggingConfigFile.exists()) {
            try {
                LogManager.getLogManager().readConfiguration(new FileInputStream(loggingConfigFile));
                LOGGER.log(Level.CONFIG, "configured {0}", loggingConfigFile.getAbsolutePath());

            } catch (IOException | SecurityException t) {
                LOGGER.log(Level.SEVERE, "reading logging configuration", t);
            }
        }
    }

    public static void main(String[] args) {
        // for testing purposes
        getInstance();
    }

}
