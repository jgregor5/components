package commander;

import java.util.Set;

/**
 *
 * @author julian
 */
public interface IComponent extends ICommander, IEventSource {
    
    String getName();
    Set<String> getCommands();
}
