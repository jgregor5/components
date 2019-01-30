package test.logging;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 *
 * @author julian
 */
public class MyLogHandler extends Handler {

    private static MyLogHandler instance;
    
    private final String prefix;
    private Formatter formatter;
    private Level level;
    private Set<ILogListener> listeners;
    
    public static MyLogHandler getInstance() {
        return instance;
    }
    
    public MyLogHandler() {
        instance = this;        
        this.prefix = this.getClass().getName();
        this.listeners = new HashSet<>();
        initProperties();
    }
    
    public interface ILogListener {
        void process(String message);
    }
    
    public void registerListener(ILogListener listener) {
        this.listeners.add(listener);
    }
    
    public void unregisterListener(ILogListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void publish(LogRecord record) {
        if (this.isLoggable(record)) {
            String message = getFormatter().format(record);
            for (ILogListener listener: this.listeners) {
                listener.process(message);
            }
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public Formatter getFormatter() {
        return this.formatter;
    }
    
    @Override
    public Level getLevel() {
        return this.level;
    }

    private void initProperties() {

        LogManager manager = LogManager.getLogManager();

        final String formatterName = manager.getProperty(prefix + ".formatter");
        if (formatterName != null) {
            try {
                formatter = (Formatter) getCustomizeInstance(formatterName);
            } catch (Exception e) {
                formatter = null;
            }
        }
        
        String levelName = manager.getProperty(prefix + ".level");
        if (levelName != null) {
            try {
                level = Level.parse(levelName);
            } catch (Exception e) {
                level = Level.ALL;
            }
        } else {
            level = Level.ALL;
        }        
    }

    // get a instance from given class name, using context classloader
    private Object getCustomizeInstance(final String className) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Class<?> c = loader.loadClass(className);
        return c.newInstance();
    }

}
