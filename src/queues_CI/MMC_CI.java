package queues_CI;


import java.util.ArrayList;
import java.util.HashMap;

import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;

public class MMC_CI extends Simulation_CI {

	public MMC_CI(double meanInterArrivalTime, double meanServiceTime, int numberOfServers) {
		super(meanInterArrivalTime, meanServiceTime, numberOfServers);
		for (int i = 0; i < numberOfServers; i++) {
			servers.add(new Server());
		}
	}

	public void showResult() {
		System.out.println("Showing the results : " + servedJobs.size() + "\n");
		for (int i = 0; i < servedJobs.size(); ++i) {
			System.out.println("Job ID : " + Integer.toString(servedJobs.get(i).getId()) + ",The waiting time is : "
					+ Double.toString(servedJobs.get(i).getTimeInQueue()) + " arrival: "
					+ servedJobs.get(i).getArrivalTime() + " service start, end: "
					+ servedJobs.get(i).getServiceStartTime() + ", " + servedJobs.get(i).getServiceEndTime());
		}
		
		double time=0;
		System.out.println("States: " + stateTimes.keySet().size());
		for(int key: stateTimes.keySet()) {
			System.out.println(key + ": " + stateTimes.get(key));
			time += stateTimes.get(key); 
		}
		
		System.out.println("Last clock: " + clock + ", calculated time: " + time);
		
		System.out.println("Servers working times: ");
		for (int i = 0; i < serverTimes.length; ++i) {
			System.out.println(i + ": " + serverTimes[i]);
		}
		
		
	}

	@Override
	public void startSimulation() {
		ExponentialGenerator interArrivalTimeGenerator = new ExponentialGenerator(meanInterArrivalTime);
		ExponentialGenerator sericeTimeGenerator = new ExponentialGenerator(meanServiceTime);
		this.clock = 0;
		double previousClock = 0;
		int nextServerID;
		int[] serverStatus; // holds index of first empty server and the number of empty servers


		// System.out.println("Start Simulation Function !!!");
		
		Job nextJob = new Job(0.0, sericeTimeGenerator.generate());
		double nextJobArrivalTime = 0;

		while (!isEndSimulation()) {
			
			/**
			 * Need to know what is the next event and what time it is.
			 */
			// System.out.println("Iteration!");

			nextServerID = getNextEvent(); // the id of the next server going to finish
			nextJobArrivalTime = nextJob.getArrivalTime(); // The time of the next job arrival
			
			// Check the status of all servers
			serverStatus = checkServers();

			// in case all servers are empty and there is a job going to arrive
			// in case the following server is not empty (as in case of all empty servers
			// this condition will be satisfied)
			// or there is more than one job with the same arrival time, so compare it with
			// the clock time
			if (serverStatus[1] == servers.size()
					|| (servers.get(nextServerID).isEmptyStatus() == false
							&& nextJobArrivalTime <= servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (nextJobArrivalTime == this.clock)) {

				previousClock = clock;
				this.clock = nextJobArrivalTime; // Change the time
				updateStateAndServerTimes(clock, previousClock);
				
				queue.add(nextJob); // add the new arrived job to the queue
				nextJob = new Job(clock + interArrivalTimeGenerator.generate(), sericeTimeGenerator.generate());

				// System.out.println("Arrival");

			} else // Look to get the next job after checking the queue, and current idel servers
			if ((servers.get(nextServerID).isEmptyStatus() == false
					&& nextJobArrivalTime > servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (servers.get(nextServerID).getJobBeingServed().getServiceEndTime() == this.clock)) {

				previousClock = clock;
				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();
				updateStateAndServerTimes(clock, previousClock);

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				// System.out.println("Departure");
			}

			// Push the jobs waiting in the queue to the servers if they are Idle
			int i = 0;
			while (queue.size() > 0 && i < servers.size()) {
				// If the server is empty and there is a job, add the job to the server
				if (servers.get(i).isEmptyStatus() == true) {
					servers.get(i).addJob(queue.get(0), this.clock); // current system time
					queue.remove(0);
				}
				// System.out.println("Push from the queue");
				i++;
			}
			
			
           meanQueueLengthRecords.add(getMeanQueueLength());                              
		}
	}

	@Override
	public boolean isEndSimulation() {
		if(meanQueueLengthRecords.size() < 10 || servedJobs.size() < 10)
			return false;
		else {
			double average;
			double tempSum = 0;
			
			for(double mql: meanQueueLengthRecords) {
				tempSum = tempSum + mql;
			}
			average = tempSum/meanQueueLengthRecords.size();
			tempSum = 0;
			double sd;
			for(double mql: meanQueueLengthRecords)
				tempSum += Math.pow(mql-average, 2);
			sd = Math.sqrt(tempSum/(meanQueueLengthRecords.size() - 1));
			
			double offset = 2.576 * (sd/Math.sqrt(meanQueueLengthRecords.size()));
			if(offset <= 0.025*average)
				return true;
			else
				return false;
		}
	}

	
	public int getNextEvent() {

		int nextEvent = 0;
		double minimumTime = Double.POSITIVE_INFINITY;
		int i = 0;
		while (i < servers.size()) {
			if (servers.get(i).isEmptyStatus() == false
					&& servers.get(i).getJobBeingServed().getServiceEndTime() < minimumTime) {
				nextEvent = i;
				minimumTime = servers.get(i).getJobBeingServed().getServiceEndTime();
			}
			i++;
		}

		return nextEvent;

	}
	
	
	
	public double getNumberOfJobsSoFar() {
		return servedJobs.size();
	}
	
}

