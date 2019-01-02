package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;

public class MMCL extends Simulation{
	
	protected int queueLength = 0;

	public MMCL(double numberOfServers, double numberOfJobs, int queueLength) {
		super(numberOfServers, numberOfJobs);
		
		//initialize the servers
		for (int i = 0; i < numberOfServers; i++) {
			servers.add(new Server());
		}
		//change the queue length
		this.queueLength = queueLength;
		
	}

	public void showResult() {
		System.out.println("Showing the results : " + servedJobs.size() + "\n");
		for (int i = 0; i < servedJobs.size(); ++i) {
			System.out.println("Job ID : " + Integer.toString(servedJobs.get(i).getId()) + ",The waiting time is : "
					+ Double.toString(servedJobs.get(i).getTimeInQueue()) + " arrival: "
					+ servedJobs.get(i).getArrivalTime() + " service start, end: "
					+ servedJobs.get(i).getServiceStartTime() + ", " + servedJobs.get(i).getServiceEndTime());
		}

	}

	@Override
	public void startSimulation(ArrayList<Job> listOfJobs) {
		this.clock = 0;
		int nextServerID;
		int[] serverStatus; // holds index of first empty server and the number of empty servers
		NextEvent nextEvent = new NextEvent();

		int currentJobID = 0;
		double nextJobArrivalTime = 0;
		
		//System.out.println("Start Simulation Function !!!");

		while (!isEndSimulation()) {
			/**
			 * Need to know what is the next event and what time it is.
			 */
			//System.out.println("Iteration!");

			nextServerID = nextEvent.getNextEvent(); // the id of the next server going to finish
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

				this.clock = nextJobArrivalTime; // Change the time

				queue.add(listOfJobs.get(currentJobID)); // add the new arrived job to the queue

				currentJobID++;
				//System.out.println("Arrival");

			} else if ((servers.get(nextServerID).isEmptyStatus() == false
					&& nextJobArrivalTime > servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (servers.get(nextServerID).getJobBeingServed().getServiceEndTime() == this.clock)) {

				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				//System.out.println("Departure");
			}

			// Push the jobs waiting in the queue to the servers if they are Idel
			int i = 0;
			while (queue.size() > 0 && i < servers.size()) {
				// If the server is empty and there is a job, add the job to the server
				if (servers.get(i).isEmptyStatus() == true) {
					servers.get(i).addJob(queue.get(0), this.clock); // current system time
					queue.remove(0);
				}
				//System.out.println("Push from the queue");
				i++;
			}
		}
	}

	@Override
	public boolean isEndSimulation() {
		if (servedJobs.size() == numberOfJobs)
			return true;
		return false;
	}

	class NextEvent {

		public int getNextEvent() {

			int nextEvent = 0;
			double minimumTime = Double.POSITIVE_INFINITY;
			int i = 0;
			while (i < servers.size()) {
				if (servers.get(i).isEmptyStatus() == false
						&& servers.get(i).getJobBeingServed().getServiceEndTime() < minimumTime) {
					nextEvent = i;
				}
				i++;
			}

			return nextEvent;
			
			
			
		}
	}

	
	
}
