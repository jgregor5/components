package commander;

/**
 *
 * @author julian
 */
public interface IEventSource {
    
    public void registerListener(IEventListener listener);
    public void unregisterListener(IEventListener listener);
}
