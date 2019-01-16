package commander;

/**
 *
 * @author julian
 */
public interface IComponent extends ICommander {
    
    String getName();
    String[] getCommands();
}
