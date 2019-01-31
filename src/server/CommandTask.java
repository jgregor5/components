package server;

import commander.ComponentManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author julian
 */
public class CommandTask implements Callable<Void> {
    
    private static final Logger LOGGER = Logger.getLogger(CommandTask.class.getName());

    private final Socket client;

    public CommandTask(Socket client) {
        this.client = client;
    }

    @Override
    public Void call() throws Exception {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        PrintWriter pw = new PrintWriter(this.client.getOutputStream(), true); // autoflush

        try {
            String line;
            while ((line = br.readLine()) != null) {                
                try {
                    JSONObject command = new JSONObject(line);
                    LOGGER.log(Level.INFO, "command:{0}", command.toString(4));
                    JSONObject result = ComponentManager.getInstance().execute(command);
                    LOGGER.log(Level.INFO, "response:{0}", result.toString(4));
                    pw.println(result);
                    
                } catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "executing " + line, t);
                    JSONObject error = new JSONObject().
                            put("success", false).
                            put("error", t.getMessage());
                    pw.println(error);
                    break;
                }
            }

        } finally {
            LOGGER.log(Level.INFO, "end of command {0}", this.client.getRemoteSocketAddress());
            
            br.close();
            pw.close();
            this.client.close();
        }

        return null;
    }

}
