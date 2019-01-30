
package test.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class LoggingTest {
    
    private final static Logger LOGGER = Logger.getLogger(LoggingTest.class.getName());    
    
    public LoggingTest() {     
        configureLogging();
    }
    
    public void test() {  
        
        LOGGER.log(Level.INFO, "starting test");
        
        Enumeration<String> names = LogManager.getLogManager().getLoggerNames();        
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            System.out.println(name + " is " + LogManager.getLogManager().getLogger(name));
        }

        Random r = new Random();
        
        for (int i=0; i<1000; i++) {            
            LOGGER.log(Level.FINEST, "date is {0}", new Date());
            try {
                Thread.sleep(r.nextInt(5000));
            } catch (InterruptedException ex) {
                LOGGER.severe("interrupted!");
            }
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
        
        new LoggingTest().test();
    }
}
