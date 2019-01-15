package server;

import commander.ComponentManager;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import commander.IEventSource;
import commander.IEventListener;

/**
 *
 * @author julian
 */
public class CommanderServer implements Runnable, IEventListener, IEventSource {
    
    private Set<IEventListener> listeners;
    
    public CommanderServer() {
        this.listeners = new HashSet<>();
    }
    
    @Override
    public void run() {              
        ComponentManager.getInstance().registerListener(this);
        startCommandThread();
        startLoggingThread();        
    }
    
    private void startCommandThread() {
        
        SocketHandler runnable = new SocketHandler(
                9000, (Socket client) -> new CommandTask(client));
        Thread thread = new Thread(runnable, "command-thread");
        thread.start();
    }
    
    private void startLoggingThread() {
        
        SocketHandler runnable = new SocketHandler(
                9001, (Socket client) -> new LoggingTask(client, this));
        Thread thread = new Thread(runnable, "logging-thread");
        thread.start();
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
