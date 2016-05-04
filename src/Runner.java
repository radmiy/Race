import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import by.gsu.epamlab.Car;

public class Runner {
	public static void main(String[] args) {
		final int NUMBER_OF_CARS = 5;
		Random random = new Random();
		
		//Thread forming cars and one race
		CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER_OF_CARS + 1);
		List<Car> cars = new ArrayList<Car>();
		for(int i = 0; i < NUMBER_OF_CARS; i++) {
			cars.add(new Car("car" + (i + 1), 100 - random.nextInt(20), cyclicBarrier));
		}
		
		Thread raceThread = new Thread(new Runnable() {
			private long time = 5000;
			@Override
			public void run() {
				try {
					cyclicBarrier.await();
					//Wait 5 seconds
					Thread.sleep(time);
					
					//Interrupt random cars
					int interruptNumber = random.nextInt(NUMBER_OF_CARS);
					interruptNumber = interruptNumber <= 0 ? 1 : interruptNumber;
					for(int num = 0; num < interruptNumber; num++) {
						int interruptThread = random.nextInt(NUMBER_OF_CARS);
						(cars.get(interruptThread)).interrupt();
					}
					
					//Waiting for the race end
					while(!Car.stopThreads){
						Thread.sleep(10);
					}
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
		
		//Start threads
		for(Car car : cars) {
			Thread carThread = new Thread(car);
			carThread.start();
		}
		
		//Start race and wait while the thread is finish
		try {
			raceThread.start();
			raceThread.join();
			
			//On result print the winner
			for(Car car : cars) {
				String result = car.getWinner();
				if(result != null) {
					System.out.println(result);
				}
			}
			System.out.println("The race is over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}
}
