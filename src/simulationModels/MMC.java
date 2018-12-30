package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;

public class MMC extends Simulation {

	public MMC(double meanInterArrivalTime, double meanSerivceTime, double numberOfServers, double numberOfJobs) {
		super(meanInterArrivalTime, meanSerivceTime, numberOfServers, numberOfJobs);
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

	}

	@Override
	public void startSimulation() {
		this.clock = 0;
		int event;
		int[] serverStatus; // holds index of first empty server and the number of empty servers
		NextEvent nextEvent = new NextEvent();
		ExponentialGenerator exponentialGenerator = new ExponentialGenerator(meanSerivceTime);

		while (!isEndSimulation()) {
			/**
			 * Need to know what is the next event and what time it is.
			 */

			event = nextEvent.getNextEvent(); // -1 for new job, others for server finished a job
			clock = nextEvent.nextEventclock; // current clock is the next Event clock

			if (event == -1) { // in case of new job to the system
				Job temp = new Job(clock, exponentialGenerator.generate());
				queue.add(temp);
				serverStatus = checkServers();
				if (serverStatus[0] != -1) {
					temp.setServiceStartTime(clock);
					servers.get(serverStatus[0]).addJob(temp, clock);
					queue.remove(queue.indexOf(temp));

				}

				if (serverStatus[1] == numberOfServers) {
					nextEvent.nextServiceEnd = temp.getServiceEndTime();
					nextEvent.serverIndex = serverStatus[0];
				}
			} else { // in case a server finished a job

				servedJobs.add(servers.get(event).getJobBeingServed()); // add the job that was served to served jobs
				servers.get(event).finishJob();
				
			}
			serverStatus = checkServers();
			/*for (int i = 0; i < servers.size(); ++i) {
				if (servers.get(i).isEmptyStatus() == false
						&& servers.get(i).getJobBeingServed().getServiceEndTime() == clock) {
					servedJobs.add(servers.get(i).getJobBeingServed()); // add the job that was served to served
																		// jobs
					servers.get(i).finishJob();
					System.out.println("Finished the server : " + servers.get(i).getId());
				}
			}*/
			while (queue.size() > 0 && serverStatus[0] != -1 && queue.get(0).getArrivalTime() <= clock) {
				System.out.println("Job Id : " + queue.get(0).getId() + ", server id " + serverStatus[0]);
				System.out.println("Arrival time : " + queue.get(0).getArrivalTime() + ", clock time" + clock);
				queue.get(0).setServiceStartTime(clock);
				servers.get(serverStatus[0]).addJob(queue.get(0), clock);
				
				if (queue.get(0).getServiceEndTime() < nextEvent.nextEventclock) {
					System.out.println("Very special case");
					nextEvent.nextServiceEnd = queue.get(0).getServiceEndTime();
					nextEvent.serverIndex = serverStatus[0];
				}
				
				queue.remove(0);
				
				serverStatus = checkServers();
			}
		}
	}

	@Override
	public boolean isEndSimulation() {
		if (servedJobs.size() == numberOfJobs)
			return true;
		return false;
	}

	private class NextEvent {
		double nextEventclock;
		double nextArrivalTime;
		double nextServiceEnd;
		int serverIndex;
		ExponentialGenerator exponentialGenerator;

		public NextEvent() {
			exponentialGenerator = new ExponentialGenerator(meanInterArrivalTime);
			nextArrivalTime = 0;
			nextServiceEnd = -1;
			serverIndex = 56564646;
		}

		public int getNextEvent() {

			int nextEvent;

			if (nextServiceEnd == -1 || nextArrivalTime < nextServiceEnd) {
				nextEventclock = nextArrivalTime;
				nextArrivalTime = nextEventclock + exponentialGenerator.generate();
				nextEvent = -1;
			} else {
				nextEventclock = nextServiceEnd;
				nextEvent = serverIndex;
				nextServiceEnd = -1;
				int i = 0;
				for (Server server : servers) {
					if (!server.isEmptyStatus() && i != nextEvent) {
						double currentServerServiceEnd = server.getJobBeingServed().getServiceEndTime();
						if (nextServiceEnd == -1 || currentServerServiceEnd < nextServiceEnd) {
							nextServiceEnd = currentServerServiceEnd;
							serverIndex = i;
						}
					}
					i++;
				}
			}

			return nextEvent;
		}
	}

}
