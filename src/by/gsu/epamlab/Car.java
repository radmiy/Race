package by.gsu.epamlab;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Logger;

public class Car implements Runnable{
	public static volatile boolean stopThreads;
	private volatile boolean stopCurrentThread;
	private CyclicBarrier cyclicBarrier;
	private long friction; 
	private long distance; 
	private String name;
	private Logger log = Logger.getLogger(getClass().toString());
	private static final long MAX_DISTANCE = 10000;
	
	
    public Car(String name, long friction, CyclicBarrier cyclicBarrier) {
    	this.cyclicBarrier = cyclicBarrier;
    	this.name = name;
        this.friction = friction;
    }
    
    @Override
	public void run() {
    	try 
    	{
			cyclicBarrier.await();
			while (!stopThreads && !stopCurrentThread) {
				if(distance < MAX_DISTANCE) {
					Thread.sleep(friction);
					distance += 100;
					synchronized (log) {
						log.info(name + " " + distance);
					}
				}
				if(distance >= MAX_DISTANCE) {
					stopThreads = true;
				}
			}
			if(distance >= MAX_DISTANCE) {
				String result = getWinner();
				if(result != null) {
					synchronized (log) {
						log.info(getWinner());
					}
				}
			}
		} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
		}
	}

    public void interrupt() {
    	if(!stopCurrentThread && !stopThreads) {
    		stopCurrentThread = true;
    		synchronized (log) {
    			log.info("Disqualify the " + name + getFriction() + " at distance " + distance);
    		}
		}
    }
    
    private String getFriction() {
    	return "(" + friction + ")";
    }
    
    public String getWinner() {
    	if(distance >= MAX_DISTANCE) {
    		return "The " + name + getFriction() + " is finish first.";
    	}else
    	if(stopCurrentThread) {
    		return "Disqualify the " + name + getFriction() + " at distance " + distance;
    	}
    	return null;
    }
}
