package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;
import randomGens.ExponentialGenerator;

public class MMC extends Simulation{

	public MMC(double meanInterArrivalTime, double meanSerivceTime, double numberOfServers, double numberOfJobs,
			ArrayList<Job> queue, ArrayList<Server> servers, ArrayList<Job> servedJobs) {
		super(meanInterArrivalTime, meanSerivceTime, numberOfServers, numberOfJobs, queue, servers, servedJobs);
		for(int i=0;i<numberOfServers; i++) {
			servers.add(new Server());
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
					servers.get(serverStatus[0]).addJob(temp, clock);
					queue.remove(0);
				}
				if(serverStatus[1]==numberOfServers) {
					nextEvent.nextServiceEnd = temp.getServiceTime();
				}
			}
			else {
				servedJobs.add(servers.get(event).finishJob());
				if(queue.size()>0) {
					servers.get(event).addJob(queue.get(0), clock);
					queue.remove(0);
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
			serverIndex = -1;
		}
		
		public int getNextEvent() {
		
			int nextEvent;
			
			if(nextServiceEnd==-1 || nextArrivalTime < nextServiceEnd) {
				nextEventclock = nextArrivalTime;
				nextArrivalTime = exponentialGenerator.generate();
				nextEvent = -1;
			}
			else {
				nextEventclock = nextServiceEnd;
				nextEvent = serverIndex;
				nextServiceEnd = -1;
				int i = 0;
				for(Server server: servers) {
					if(!server.isEmptyStatus() && i!=nextEvent) {
						double currentServerServiceEnd = server.getJobBeingServed().getServiceStartTime() + server.getJobBeingServed().getServiceTime();
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
