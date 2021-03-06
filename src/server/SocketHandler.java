package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class SocketHandler implements Runnable, AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(SocketHandler.class.getName());
    
    private static final int POOL_SIZE = 50;
    private static final int SO_TIMEOUT_MILLIS = 5000;

    private final int port;
    private final ExecutorService pool;
    private final ICallableCreator creator;
    private boolean enabled;
    private ServerSocket server;
        
    public SocketHandler(int port, ICallableCreator creator) {
        this.port = port;
        this.pool = new MyThreadPoolExecutor("port" + port, POOL_SIZE);
        this.server = null;
        this.enabled = true;
        this.creator = creator;
    }

    @Override
    public void run() {

        try {
            this.server = new ServerSocket(this.port);
            this.server.setSoTimeout(SO_TIMEOUT_MILLIS);

            while (this.enabled) {
                try {
                    Socket client = this.server.accept();
                    LOGGER.log(Level.INFO, "new client on {0} from {1}", 
                            new Object[]{this.port, client.getRemoteSocketAddress()});
                    Callable<Void> task = this.creator.create(client);
                    this.pool.submit(task);
                } catch (SocketTimeoutException ex) {
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "server at {0}", this.port);
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void close() {
        this.enabled = false;
        try {
            this.pool.shutdownNow();
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, null, t); 
        }
    }
    
    public interface ICallableCreator {    
        Callable<Void> create(Socket client);
    }
}
