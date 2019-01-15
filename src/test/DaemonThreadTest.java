package test;
/**
 * @author ashraf
 * 
 */
public class DaemonThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Create a new daemon thread and start it
		DaemonThread daemonThread = new DaemonThread();
		daemonThread.setDaemon(true); // change this!
		daemonThread.start();

		// Create a new user thread and start it
		Thread userThread = new Thread(new UserThread());
		userThread.start();

	}

}
