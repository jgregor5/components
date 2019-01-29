package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import commander.IEventSource;
import commander.IEventListener;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class LoggingTask implements Callable<Void>, IEventListener {
    
    private static final Logger LOGGER = Logger.getLogger(LoggingTask.class.getName());

    private static final long POLL_TIMEOUT = 5000;
    
    private static final int EOT = 3;
    private static final int ACK = 6;
    
    private final Socket client;
    private final IEventSource source;
    private BlockingQueue<JSONObject> queue;
    private boolean enabled;

    public LoggingTask(Socket client, IEventSource source) {
        this.client = client;
        this.source = source;
        this.queue = new ArrayBlockingQueue<>(1024);
        this.enabled = true;
    }

    @Override
    public Void call() throws Exception {    
        
        InputStream is = this.client.getInputStream();
        PrintWriter pw = new PrintWriter(this.client.getOutputStream(), true); // autoflush 
        
        try {
            // CommandServer will be listening
            this.source.registerListener(this);        

            while (this.enabled) {
                JSONObject json = this.queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
                if (json != null) {
                    pw.println(json);
                }
                else {
                    pw.println(new JSONObject().put("keepalive", new Date()));
                }
                
                // TODO: add here a timeout?
                int ack = is.read();
                if (ack == EOT) {
                    break;
                }
                else if (ack != ACK) {
                    throw new RuntimeException("unexpected ack " + ack);
                }                    
            }        

            this.source.unregisterListener(this);    
            
        } finally {
            LOGGER.log(Level.INFO, "end of logging {0}", this.client.getRemoteSocketAddress());
            
            is.close();
            pw.close();
            this.client.close();
        }
                
        return null;
    }
    
    @Override
    public void handleEvent(JSONObject event) {  
        // receives events from CommanderServer and sends them to the queue
        this.queue.add(event);
    }
    
}
