package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;

public class MMCLBreakdown extends Simulation {

	int queueLength;

	public MMCLBreakdown(int numberOfServers, int queueLength) {
		super(numberOfServers);

		// initialize the servers
		for (int i = 0; i < numberOfServers; i++) {
			servers.add(new Server());
		}
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

		System.out.println("List of dropped jobs :" + droppedJobs.size() + "\n");
		for (int i = 0; i < droppedJobs.size(); ++i) {
			System.out.println("Job ID : " + Integer.toString(droppedJobs.get(i).getId()) + ",The waiting time is : "
					+ Double.toString(droppedJobs.get(i).getTimeInQueue()) + " arrival: "
					+ droppedJobs.get(i).getArrivalTime() + " service start, end: "
					+ droppedJobs.get(i).getServiceStartTime() + ", " + droppedJobs.get(i).getServiceEndTime());
		}
	}

	@Override
	public void startSimulation(ArrayList<Job> listOfJobs, ArrayList<ArrayList<Double>> breakdownList,
			ArrayList<ArrayList<Double>> repairList, ArrayList<Integer> breakdownCounterList) {
		this.clock = 0;
		int nextServerID;
		int[] serverStatus; // holds index of first empty server and the number of empty servers
		NextEvent nextEvent = new NextEvent();

		int currentJobID = 0;
		double nextJobArrivalTime = 0;

		double previousClock = 0;

		// System.out.println("Start Simulation Function !!!");

		while (servedJobs.size() + droppedJobs.size() < listOfJobs.size()) {
			/**
			 * Need to know what is the next event and what time it is.
			 */
			// System.out.println("Iteration!");

			nextServerID = nextEvent.getNextEvent(); // the id of the next server going to finish
			if (currentJobID < listOfJobs.size())
				nextJobArrivalTime = listOfJobs.get(currentJobID).getArrivalTime(); // The time of the next job arrival
			else
				nextJobArrivalTime = Double.POSITIVE_INFINITY;

			// breakdown and repair loop (i is the server ID)
			for (int i = 0; i < servers.size(); ++i) {
				// The server is down and should have repair or it is empty
				if (servers.get(i).isBrokeDown(this.clock) == false && servers.get(i).isEmptyStatus() == true) {
					// either has a repair or null
					servers.get(i).Repair();
				}
				// the current server should have breakdown
				if (servers.get(i).isBrokeDown(this.clock) == false
						&& breakdownList.get(i).get(breakdownCounterList.get(i)) <= this.clock) {
					if (servers.get(i).isEmptyStatus() == false) {
						droppedJobs.add(servers.get(i).getJobBeingServed());// drop the job to the dropped list
					}
					servers.get(i).BreakDown(breakdownList.get(i).get(breakdownCounterList.get(i)),
							repairList.get(i).get(breakdownCounterList.get(i)));
					breakdownCounterList.set(i, breakdownCounterList.get(i) + 1);// get the following breakdown for that
																					// server
				}
			}

			// Check the status of all servers
			serverStatus = checkServers();

			// in case all servers are empty and there is a job going to arrive
			// in case the following server is not empty (as in case of all empty servers
			// this condition will be satisfied)
			// or there is more than one job with the same arrival time, so compare it with
			// the clock time
			if ((nextJobArrivalTime < Double.POSITIVE_INFINITY && serverStatus[1] == servers.size())
					|| (servers.get(nextServerID).isBrokeDown(this.clock) == false
							&& servers.get(nextServerID).isEmptyStatus() == false
							&& nextJobArrivalTime <= servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
					|| (nextJobArrivalTime == this.clock)) {

				this.clock = nextJobArrivalTime; // Change the time

				// Check that the length of the queue is not exceeded
				if (queue.size() >= queueLength) {
					droppedJobs.add(listOfJobs.get(currentJobID)); // add the new job to the dropped list
					// System.out.println("Job (dropped): " + Integer.toString(currentJobID));
				} else {
					queue.add(listOfJobs.get(currentJobID)); // add the new arrived job to the queue
					// System.out.println("Job (queue): " + Integer.toString(currentJobID));
				}
				currentJobID++;
				System.out.println("Arrival");

			}
			// Look to get the next job after checking the queue, and current idel servers
			if (servers.get(nextServerID).isBrokeDown(this.clock) == false
					&& ((servers.get(nextServerID).isEmptyStatus() == false
							&& nextJobArrivalTime > servers.get(nextServerID).getJobBeingServed().getServiceEndTime())
							|| (servers.get(nextServerID).isEmptyStatus() == false && servers.get(nextServerID)
									.getJobBeingServed().getServiceEndTime() == this.clock))) {

				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				System.out.println("Departure");
			}
			// Push the jobs waiting in the queue to the servers if they are Idel
			{
				int i = 0;
				while (queue.size() > 0 && i < servers.size()) {
					// If the server is empty and there is a job, add the job to the server
					// if the server is not down as well
					if (servers.get(i).isBrokeDown(this.clock) == false && servers.get(i).isEmptyStatus() == true) {
						servers.get(i).addJob(queue.get(0), this.clock); // current system time
						System.out.println("Job (toServer): " + Integer.toString(queue.get(0).getId()));
						queue.remove(0);
					}
					System.out.println("Push from the queue");
					i++;
				}
			}

			// as for server breakdown there might be a problem (considering all possible
			// cases "there might be infinite loop for time changing"), so implementing a
			// counter to keep track of infinite loops and continue with the following
			// events
			if (this.clock == previousClock) {
				this.clock++;
			} else {
				previousClock = this.clock;
			}

			
		}
	}

	/*
	public boolean isEndSimulation() {
		if (servedJobs.size() + droppedJobs.size() == numberOfJobs)
			return true;
		return false;
	}*/

	class NextEvent {

		public int getNextEvent() {

			int nextEvent = 0;
			double minimumTime = Double.POSITIVE_INFINITY;
			int i = 0;
			while (i < servers.size()) {
				if (servers.get(i).isBrokeDown(clock) == false && servers.get(i).isEmptyStatus() == false
						&& servers.get(i).getJobBeingServed().getServiceEndTime() < minimumTime) {
					nextEvent = i;
					minimumTime = servers.get(i).getJobBeingServed().getServiceEndTime();
				}
				i++;
			}

			return nextEvent;

		}

	}

	@Override
	public void startSimulation(ArrayList<Job> listOfJobs) {
		// TODO Auto-generated method stub

	}
	
	public double getNumberOfJobsSoFar() {
		return servedJobs.size() + droppedJobs.size();
	}

}
