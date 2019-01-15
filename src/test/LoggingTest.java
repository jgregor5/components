
package test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class LoggingTest {
    
    private static final Logger LOGGER = Logger.getLogger(LoggingTest.class.getName());
 
    public static void main(String[] args) {
        
        LOGGER.log(Level.WARNING, "un missatge amb paràmetre {0}", "1");
        LOGGER.log(Level.WARNING, "un missatge amb paràmetres {0} i {1}", new Object[]{"1", 2});
        LOGGER.log(Level.WARNING, "un missatge amb paràmetre {0}", new RuntimeException("una excepció"));
    }
}
