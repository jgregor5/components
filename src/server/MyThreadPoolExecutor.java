/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
        super(nThreads, nThreads,0L, TimeUnit.MILLISECONDS, 
                new LinkedBlockingQueue<Runnable>());
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
    
    
}
