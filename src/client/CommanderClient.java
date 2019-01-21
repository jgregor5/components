package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;
import client.LoggingClient.IStreamListener;
import commander.IEventListener;
import commander.IManager;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class CommanderClient implements IManager, IStreamListener {
    
    private static final Logger LOGGER = Logger.getLogger(CommanderClient.class.getName());
    
    private static final int SO_TIMEOUT_MILLIS = 5000;

    private final String host;
    
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private LoggingClient logging;
    private Set<IEventListener> listeners;
    
    public CommanderClient(String host) { 
        this.host = host;
        this.listeners = new HashSet<>();
        configureLogging();
    }

    // API
    
    @Override
    public JSONObject execute(JSONObject command) {
        
        try {
            LOGGER.log(Level.FINER, "sending {0}", command);
            this.pw.println(command);
            String response = this.br.readLine();
            LOGGER.log(Level.FINER, "received {0}", response);
            return new JSONObject(response);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }
    
    public void listen(int port) {
        this.logging = new LoggingClient(this.host, port, this);
        new Thread(logging, "commander-client").start();
    }
    
    public void unlisten() {
        this.logging.shutdown();
    }
    
    public void connect(int port) {
        
        try {
            this.socket = new Socket(this.host, port);
            this.socket.setSoTimeout(SO_TIMEOUT_MILLIS);
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));            
            this.pw = new PrintWriter(socket.getOutputStream(), true); // autoflush
            
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }
    
    public void disconnect() {
        
        try {
            this.br.close();
            this.pw.close();
            this.socket.close();
            
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }
    
    @Override
    public void close() {
        
        try {
            unlisten();
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
        
        try {
            disconnect();
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }
    
    // EVENT SOURCE
    
    @Override
    public void registerListener(IEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(IEventListener listener) {
        this.listeners.remove(listener);
    }
    
    // STREAM LISTENER
    
    @Override
    public void startOfStream() {      
        LOGGER.log(Level.INFO, "stream start");
    }

    @Override
    public void process(String line) {
        JSONObject json = new JSONObject(line);
        if (json.has("keepalive")) {
            LOGGER.log(Level.FINEST, "keepalive {0}", json.getString("keepalive"));
        }
        else {
            LOGGER.log(Level.FINER, "logging:{0}", json.toString(4)); 
            for (IEventListener listener: this.listeners) {
                try {
                    listener.handleEvent(json);
                } catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "handing event", t);
                }            
            }               
        }        
    }

    @Override
    public void endOfStream() {   
        LOGGER.log(Level.INFO, "stream end");
    }    

    // LOGGING
    
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
    
}
