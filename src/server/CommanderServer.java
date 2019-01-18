package server;

import commander.ComponentManager;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import commander.IEventSource;
import commander.IEventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class CommanderServer implements Runnable, IEventListener, IEventSource, AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(CommanderServer.class.getName());
    
    private Set<IEventListener> listeners;
    private SocketHandler commandSH, loggingSH;
    
    public CommanderServer() {
        this.listeners = new HashSet<>();
        this.commandSH = null;
        this.loggingSH = null;
    }
    
    @Override
    public void run() {              
        
        ComponentManager.getInstance().registerListener(this);
        
        startCommandThread();
        startLoggingThread();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "shutdown hook");
                close();
            }
        });        
    }
    
    private void startCommandThread() {
        
        this.commandSH = new SocketHandler(
                9000, (Socket client) -> new CommandTask(client));
        Thread thread = new Thread(this.commandSH, "command-thread");
        thread.start();
    }
    
    private void startLoggingThread() {
        
        this.loggingSH = new SocketHandler(
                9001, (Socket client) -> new LoggingTask(client, this));
        Thread thread = new Thread(this.loggingSH, "logging-thread");
        thread.start();
    }
    
    @Override
    public void close() {
        this.commandSH.close();
        this.loggingSH.close();
    }
    
    @Override
    public void handleEvent(JSONObject event) { 
        for (IEventListener listener: listeners) {
            listener.handleEvent(event);
        }
    }
    
    @Override
    public void registerListener(IEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }

    // MAIN
    
    public static void main(String[] args) {
        
        Thread thread = new Thread(new CommanderServer(), "commander-server");
        thread.start();
    }

}
