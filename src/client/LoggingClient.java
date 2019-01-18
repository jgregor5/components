package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class LoggingClient implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(LoggingClient.class.getName());
    
    private static final int SO_TIMEOUT_MILLIS = 5000;
    
    private static final int EOT = 3;
    private static final int ACK = 6;
    
    private Socket socket;
    
    private final String host;
    private final int port;
    private final IStreamListener listener;
    private boolean enabled;
    
    public LoggingClient(String host, int port, IStreamListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
        this.enabled = true;
    }
    
    public interface IStreamListener {
        void startOfStream();
        void process(String line);
        void endOfStream();
    }
    
    public void shutdown() {
        this.enabled = false;
        //this.socket.shutdownInput();         
    }
        
    @Override
    public void run() {
        
        OutputStream os = null;
        BufferedReader br = null;
        
        try {
            this.socket = new Socket(this.host, this.port);
            this.socket.setSoTimeout(SO_TIMEOUT_MILLIS);
            
            os = this.socket.getOutputStream();
            br = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); 
            
            this.listener.startOfStream();
            
            String line;
            while (this.enabled) {                
                try {
                    line = br.readLine();
                    if (line != null) {
                        this.listener.process(line);
                        os.write(this.enabled? ACK : EOT);
                    }
                    
                } catch (SocketTimeoutException ex) {
                    LOGGER.log(Level.FINEST, "socket timeout");
                }
            }

            this.listener.endOfStream();
            
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
            
        } finally {                
            LOGGER.log(Level.INFO, "closing client socket");     
            
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {}
            
            try {
                if (this.socket != null)
                    this.socket.close();
            } catch (IOException ex) {}
        }
    }
}
