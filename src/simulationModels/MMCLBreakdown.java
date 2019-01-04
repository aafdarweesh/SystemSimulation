package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;
import randomGens.LogNormalGenerator;
import randomGens.WeibullGenerator;

public class MMCLBreakdown extends Simulation {

	int maxLength;

	public MMCLBreakdown(int numberOfServers, int queueLength) {
		super(numberOfServers);

		// initialize the servers
		for (int i = 0; i < numberOfServers; i++) {
			servers.add(new Server());
		}
		this.maxLength = queueLength;
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
					servers.get(i).repair();
				}
				// the current server should have breakdown
				if (servers.get(i).isBrokeDown(this.clock) == false
						&& breakdownList.get(i).get(breakdownCounterList.get(i)) <= this.clock) {
					if (servers.get(i).isEmptyStatus() == false) {
						droppedJobs.add(servers.get(i).getJobBeingServed());// drop the job to the dropped list
					}
					servers.get(i).breakDown(breakdownList.get(i).get(breakdownCounterList.get(i)),
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
				if (queue.size() >= maxLength) {
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
	
	public void startSimulation(double meanInterArrivalTime, double meanServiceTime, double meanTimeBetweenFailures,
			double meanTimeToRepair, int numberOfJobs) {
		reset();
		ExponentialGenerator interArrivalTimeGenerator = new ExponentialGenerator(meanInterArrivalTime);
		ExponentialGenerator sericeTimeGenerator = new ExponentialGenerator(meanServiceTime);
		WeibullGenerator timeBetweenFailuresGenerator = new WeibullGenerator(meanTimeBetweenFailures);
		LogNormalGenerator timeToRepairGenerator = new LogNormalGenerator(meanTimeToRepair);
		this.clock = 0;
		double previousClock = 0;
		int nextServerID;
		int nextServerID_repair;
		int[] serverStatus; // holds index of first empty server and the number of empty servers

		int jobCount = 0;

		// System.out.println("Start Simulation Function !!!");
		
		Job nextJob = new Job(0.0, sericeTimeGenerator.generate());
		double nextJobArrivalTime = 0;
		double nextBreakDown = clock + timeBetweenFailuresGenerator.generate();
		jobCount++;
		
		double nextServiceEnd;
		double nextRepairEnd;

		while (servedJobs.size() + droppedJobs.size() < numberOfJobs) {
			
			/**
			 * Need to know what is the next event and what time it is.
			 */
			// System.out.println("Iteration!");

			nextServerID = getNextServer_modified(); // the id of the next server going to finish
			if(nextServerID == -1)
				nextServiceEnd = Double.POSITIVE_INFINITY;
			else
				nextServiceEnd = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();
			
			if (jobCount <= numberOfJobs)
				nextJobArrivalTime = nextJob.getArrivalTime(); // The time of the next job arrival
			else
				nextJobArrivalTime = Double.POSITIVE_INFINITY;
			
			nextServerID_repair = getNextRepair();
			if(nextServerID_repair == -1)
				nextRepairEnd = Double.POSITIVE_INFINITY;
			else
				nextRepairEnd = servers.get(nextServerID_repair).getRepairedTime();
			
			boolean arrivalCheck = (nextJobArrivalTime < nextServiceEnd) && (nextJobArrivalTime < nextRepairEnd) &&
					(nextJobArrivalTime < nextBreakDown) && (nextJobArrivalTime < Double.POSITIVE_INFINITY) || 
					(nextJobArrivalTime == clock);
			
			boolean serviceCheck = (nextServiceEnd < nextRepairEnd) && (nextServiceEnd < nextBreakDown) 
					&& (nextServiceEnd < Double.POSITIVE_INFINITY) || 
					(nextServiceEnd == clock);
			
			boolean repairCheck = (nextRepairEnd < nextBreakDown) && (nextRepairEnd < Double.POSITIVE_INFINITY) || 
					(nextRepairEnd == clock);
			
			boolean breakDownCheck = (nextBreakDown < Double.POSITIVE_INFINITY) || (nextBreakDown == clock);
			
			

			// Check the status of all servers
			serverStatus = checkServers();

			// in case all servers are empty and there is a job going to arrive
			// in case the following server is not empty (as in case of all empty servers
			// this condition will be satisfied)
			// or there is more than one job with the same arrival time, so compare it with
			// the clock time
			if (arrivalCheck) {

				previousClock = clock;
				this.clock = nextJobArrivalTime; // Change the time
				updateStateAndServerTimes_unreliable(clock, previousClock);
				
				// Check that the length of the queue is not exceeded
				if (queue.size() + (numberOfServers - serverStatus[1]) >= maxLength) {
					droppedJobs.add(nextJob); // add the new job to the dropped list
					//System.out.println("Job (dropped): " + Integer.toString(currentJobID));
				} else {
					queue.add(nextJob); // add the new arrived job to the queue
					//System.out.println("Job (queue): " + Integer.toString(currentJobID));
				}
				
				nextJob = new Job(clock + interArrivalTimeGenerator.generate(), sericeTimeGenerator.generate());
				jobCount++;
				// System.out.println("Arrival");

			} else if (serviceCheck) {

				previousClock = clock;
				this.clock = servers.get(nextServerID).getJobBeingServed().getServiceEndTime();
				updateStateAndServerTimes_unreliable(clock, previousClock);

				servedJobs.add(servers.get(nextServerID).getJobBeingServed());

				servers.get(nextServerID).finishJob();
				// System.out.println("Departure");
			} else if (repairCheck) {
				
				previousClock = clock;
				this.clock = servers.get(nextServerID_repair).getRepairedTime();
				updateStateAndServerTimes_unreliable(clock, previousClock);
						
				servers.get(nextServerID_repair).repair();
				
			} else if (breakDownCheck) {
				previousClock = clock;
				this.clock = nextBreakDown;
				updateStateAndServerTimes_unreliable(clock, previousClock);
				
				int breakDownServer = chooseBreakDownServer();
				if(breakDownServer!=-1) {
					if(!servers.get(breakDownServer).isEmptyStatus())
						droppedJobs.add(servers.get(breakDownServer).getJobBeingServed());
					servers.get(breakDownServer).breakDown(nextBreakDown, timeToRepairGenerator.generate());
				}
				
				nextBreakDown = clock + timeBetweenFailuresGenerator.generate();
					
			} else {
				System.out.println("This should never happen!");
			}

			// Push the jobs waiting in the queue to the servers if they are Idle
			int i = 0;
			while (queue.size() > 0 && i < servers.size()) {
				// If the server is empty and there is a job, add the job to the server
				if (servers.get(i).isEmptyStatus() == true && !servers.get(i).isBrokeDown(clock)) {
					servers.get(i).addJob(queue.get(0), this.clock); // current system time
					queue.remove(0);
				}
				// System.out.println("Push from the queue");
				i++;
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
