package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;

public class MMC extends Simulation{

	public MMC(double meanInterArrivalTime, double meanSerivceTime, double numberOfServers, double numberOfJobs) {
		super(meanInterArrivalTime, meanSerivceTime, numberOfServers, numberOfJobs);
		for(int i=0;i<numberOfServers; i++) {
			servers.add(new Server());
		}
	}

	
	public void showResult() {
		System.out.println("Showing the results : " + servedJobs.size() + "\n");
		for (int i = 0; i < servedJobs.size(); ++i) {
			System.out.println("Job ID : " + Integer.toString(servedJobs.get(i).getId()) + ",The waiting time is : " + Double.toString(servedJobs.get(i).getTimeInQueue()) + 
					" arrival: " + servedJobs.get(i).getArrivalTime() + " service start, end: " + servedJobs.get(i).getServiceStartTime() + 
					", " + servedJobs.get(i).getServiceEndTime());
		}
		
	}


	@Override
	public void startSimulation() {
		this.clock = 0;
		int event;
		int[] serverStatus; //holds index of first empty server and the number of empty servers
		NextEvent nextEvent = new NextEvent();
		ExponentialGenerator exponentialGenerator = new ExponentialGenerator(meanSerivceTime);
		
		
		while(!isEndSimulation()) {
			/**
			 * Need to know what is the next event and what time it is.
			 */
			
			event = nextEvent.getNextEvent();
			clock = nextEvent.nextEventclock;
			
			
			if(event == -1) {
				Job temp = new Job(clock, exponentialGenerator.generate());
				queue.add(temp);
				serverStatus = checkServers();
				if(serverStatus[0]!=-1) {
					temp.setServiceStartTime(clock);
					servers.get(serverStatus[0]).addJob(temp, clock);
					queue.remove(0);
				}
				if(serverStatus[1]==numberOfServers) {
					nextEvent.nextServiceEnd = temp.getServiceEndTime();
					nextEvent.serverIndex = serverStatus[0];
				}
			}
			else {
				
				servedJobs.add(servers.get(event).getJobBeingServed());
				servers.get(event).finishJob();
				serverStatus = checkServers();
				if(queue.size()>0) {
					int min = 0;
					for(int i=0; i<queue.size(); i++) {
						if(queue.get(i).getArrivalTime()<queue.get(min).getArrivalTime())
							min = i;
					}
					System.out.println(queue.get(min).getId());
					queue.get(min).setServiceStartTime(clock);
					servers.get(event).addJob(queue.get(min), clock);
					queue.remove(min);
				}
			}
		}
	}
	
	@Override
	public boolean isEndSimulation() {
		if(servedJobs.size() == numberOfJobs)
			return true;
		return false;
	}
	
	private class NextEvent{
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
			
			if(nextServiceEnd==-1 || nextArrivalTime < nextServiceEnd) {
				nextEventclock = nextArrivalTime;
				nextArrivalTime = nextEventclock + exponentialGenerator.generate();
				nextEvent = -1;
			}
			else {
				nextEventclock = nextServiceEnd;
				nextEvent = serverIndex;
				nextServiceEnd = -1;
				int i = 0;
				for(Server server: servers) {
					if(!server.isEmptyStatus() && i!=nextEvent) {
						double currentServerServiceEnd = server.getJobBeingServed().getServiceEndTime();
						if(nextServiceEnd==-1 || currentServerServiceEnd < nextServiceEnd) {
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
