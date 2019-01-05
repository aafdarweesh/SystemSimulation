package simulationModels;

import java.util.ArrayList;
import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;

public class MMC extends Simulation {

	public MMC(int numberOfServers) {
		super(numberOfServers);
		for (int i = 0; i < numberOfServers; i++) {
			servers.add(new Server());
		}
	}

	public void showLogs() {
		System.out.println("Showing the results : " + servedJobs.size() + "\n");
		for (int i = 0; i < servedJobs.size(); ++i) {
			System.out.println("Job ID : " + Integer.toString(servedJobs.get(i).getId()) + ",The waiting time is : "
					+ Double.toString(servedJobs.get(i).getTimeInQueue()) + " arrival: "
					+ servedJobs.get(i).getArrivalTime() + " service start, end: "
					+ servedJobs.get(i).getServiceStartTime() + ", " + servedJobs.get(i).getServiceEndTime());
		}
	}

	
	public void startSimulation(ArrayList<Job> listOfJobs) {
		reset();
		this.clock = 0;
		double previousClock = 0;
		int nextServerID;
		int[] serverStatus; // holds index of first empty server and the number of empty servers

		int currentJobID = 0;
		double nextJobArrivalTime = 0;

		

		while (servedJobs.size() < listOfJobs.size()) {
			
			/**
			 * Need to know what is the next event and what time it is.
			 */
			

			nextServerID = getNextServer(); // the id of the next server going to finish
			if (currentJobID < listOfJobs.size())
				nextJobArrivalTime = listOfJobs.get(currentJobID).getArrivalTime(); // The time of the next job arrival
			else
				nextJobArrivalTime = Double.POSITIVE_INFINITY;

			// Check the status of all servers
			serverStatus = checkServers();

			// in case all servers are empty and there is a job going to arrive
			// in case the following server is not empty (as in case of all empty servers
			// this condition will be satisfied)
			// or there is more than one job with the same arrival time, so compare it with
			// the clock time
			if ((nextJobArrivalTime < Double.POSITIVE_INFINITY && serverStatus[1] == servers.size())
					|| (servers.get(nextServerID).isEmptyStatus() == false
							&& nextJobArrivalTime <= servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (nextJobArrivalTime == this.clock)) {

				previousClock = clock;
				this.clock = nextJobArrivalTime; // Change the time
				updateStateAndServerTimes(clock, previousClock);
				
				queue.add(listOfJobs.get(currentJobID)); // add the new arrived job to the queue

				currentJobID++;
				

			} else // Look to get the next job after checking the queue, and current idel servers
			if ((servers.get(nextServerID).isEmptyStatus() == false
					&& nextJobArrivalTime > servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (servers.get(nextServerID).getJobBeingServed().getServiceEndTime() == this.clock)) {

				previousClock = clock;
				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();
				updateStateAndServerTimes(clock, previousClock);

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				
			}

			// Push the jobs waiting in the queue to the servers if they are Idle
			int i = 0;
			while (queue.size() > 0 && i < servers.size()) {
				// If the server is empty and there is a job, add the job to the server
				if (servers.get(i).isEmptyStatus() == true) {
					servers.get(i).addJob(queue.get(0), this.clock); // current system time
					queue.remove(0);
				}
				
				i++;
			}
			
			
                                         
		}
	}
	
	public void startSimulation(double meanInterArrivalTime, double meanServiceTime, int numberOfJobs) {
		reset();
		ExponentialGenerator interArrivalTimeGenerator = new ExponentialGenerator(meanInterArrivalTime);
		ExponentialGenerator sericeTimeGenerator = new ExponentialGenerator(meanServiceTime);
		this.clock = 0;
		double previousClock = 0;
		int nextServerID;
		int[] serverStatus; // holds index of first empty server and the number of empty servers

		int jobCount = 0;

		
		
		Job nextJob = new Job(0.0, sericeTimeGenerator.generate());
		double nextJobArrivalTime = 0;
		jobCount++;

		while (servedJobs.size() < numberOfJobs) {
			
			/**
			 * Need to know what is the next event and what time it is.
			 */
			

			nextServerID = getNextServer(); // the id of the next server going to finish
			if (jobCount <= numberOfJobs)
				nextJobArrivalTime = nextJob.getArrivalTime(); // The time of the next job arrival
			else
				nextJobArrivalTime = Double.POSITIVE_INFINITY;

			// Check the status of all servers
			serverStatus = checkServers();

			// in case all servers are empty and there is a job going to arrive
			// in case the following server is not empty (as in case of all empty servers
			// this condition will be satisfied)
			// or there is more than one job with the same arrival time, so compare it with
			// the clock time
			if ((nextJobArrivalTime < Double.POSITIVE_INFINITY && serverStatus[1] == servers.size())
					|| (servers.get(nextServerID).isEmptyStatus() == false
							&& nextJobArrivalTime <= servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (nextJobArrivalTime == this.clock)) {

				previousClock = clock;
				this.clock = nextJobArrivalTime; // Change the time
				updateStateAndServerTimes(clock, previousClock);
				
				queue.add(nextJob); // add the new arrived job to the queue
				nextJob = new Job(clock + interArrivalTimeGenerator.generate(), sericeTimeGenerator.generate());
				jobCount++;
				

			} else // Look to get the next job after checking the queue, and current idle servers
			if ((servers.get(nextServerID).isEmptyStatus() == false
					&& nextJobArrivalTime > servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (servers.get(nextServerID).getJobBeingServed().getServiceEndTime() == this.clock)) {

				previousClock = clock;
				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();
				updateStateAndServerTimes(clock, previousClock);

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				
			}

			// Push the jobs waiting in the queue to the servers if they are Idle
			int i = 0;
			while (queue.size() > 0 && i < servers.size()) {
				// If the server is empty and there is a job, add the job to the server
				if (servers.get(i).isEmptyStatus() == true) {
					servers.get(i).addJob(queue.get(0), this.clock); // current system time
					queue.remove(0);
				}
				
				i++;
			}
			
                                         
		}
	}



	public int getNextServer() {

		int nextServer = 0;
		double minimumTime = Double.POSITIVE_INFINITY;
		int i = 0;
		while (i < servers.size()) {
			if (servers.get(i).isEmptyStatus() == false
					&& servers.get(i).getJobBeingServed().getServiceEndTime() < minimumTime) {
				nextServer = i;
				minimumTime = servers.get(i).getJobBeingServed().getServiceEndTime();
			}
			i++;
		}

		return nextServer;

	}
		
	public double getNumberOfJobsSoFar() {
		return servedJobs.size();
	}
	
	
	public void calculateMetrics(queues_analytical.Queue theoritical) {
		System.out.println("---------------- Simulation Results ----------------");
		System.out.println("Total Running Time: " + clock);
		super.calculateMetrics(theoritical);
	}
	
}
