package server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author julian
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger LOGGER = Logger.getLogger(MyThreadPoolExecutor.class.getName());

    private int count;
    private String name;

    public MyThreadPoolExecutor(String name, int nThreads) {

        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new MyThreadFactory(name));

        this.name = name;
        this.count = 0;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        LOGGER.log(Level.FINER, name + " finished, count is {0}", --this.count);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        LOGGER.log(Level.FINER, name + " to start, count is {0}", ++this.count);
    }

    static class MyThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    LOGGER.log(Level.SEVERE, "uncaught exception", e);
                }
            });

            return t;
        }
    }
    
    public static void main(String[] args) {
        
        ExecutorService pool=Executors.newSingleThreadExecutor(new MyThreadFactory("test"));
        pool.execute(new Runnable() {
            @Override
            public void run() {
                if (true)
                    throw new RuntimeException("catch me!");
                else
                    System.out.println("done!");
            }
        });
        
        try { Thread.sleep(5000); } catch (Throwable t) {}
        System.out.println("exit!");
        pool.shutdownNow();
        System.out.println("shutdown!");
    }
}
