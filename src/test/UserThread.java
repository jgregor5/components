package test;

/**
 * @author ashraf
 * 
 */
public class UserThread implements Runnable {

	public void run() {

		try {
			for (int i = 0; i < 5; i++) {
				System.out.println("User thread is running");
				Thread.sleep(1000);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();

		} finally {
			System.out.println("User Thread exiting");
		}
	}

}
