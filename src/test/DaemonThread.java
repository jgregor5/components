
package test;

/**
 * @author ashraf
 * 
 */
public class DaemonThread extends Thread {

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("Daemon thread is running");
				Thread.sleep(1000);
			}

		} catch (InterruptedException ie) {
			ie.printStackTrace();

		} finally {
			System.out.println("Daemon Thread exiting"); // never called
		}
	}

}
