package queues_CI;

import java.util.ArrayList;
import java.util.HashMap;

import components.Job;
import components.Server;

public abstract class Simulation_CI {

	protected double meanInterArrivalTime;
	protected double meanServiceTime;
	protected int numberOfServers;
	protected ArrayList<Job> queue;
	protected ArrayList<Server> servers;
	protected ArrayList<Job> servedJobs;
	protected ArrayList<Job> droppedJobs;
	protected double clock;
	protected HashMap<Integer, Double> stateTimes;
	protected double[] serverTimes;
	protected ArrayList<Double> meanQueueLengthRecords;

	public Simulation_CI(double meanInterArrivalTime, double meanServiceTime, int numberOfServers) {
		
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanServiceTime = meanServiceTime;
		this.numberOfServers = numberOfServers;
		this.queue = new ArrayList<>();
		this.servers = new ArrayList<>();
		this.servedJobs = new ArrayList<>();
		this.droppedJobs = new ArrayList<>();
		this.stateTimes = new HashMap<>();
		this.serverTimes = new double[numberOfServers];
		this.meanQueueLengthRecords = new ArrayList<>();
	}

	public ArrayList<Job> getDroppedJobs() {
		return droppedJobs;
	}

	public void setDroppedJobs(ArrayList<Job> droppedJobs) {
		this.droppedJobs = droppedJobs;
	}

	public double getClock() {
		return clock;
	}

	public void setClock(double clock) {
		this.clock = clock;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	public void setNumberOfServers(int numberOfServers) {
		this.numberOfServers = numberOfServers;
	}

	public ArrayList<Job> getQueue() {
		return queue;
	}

	public void setQueue(ArrayList<Job> queue) {
		this.queue = queue;
	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}

	public ArrayList<Job> getServedJobs() {
		return servedJobs;
	}

	public void setServedJobs(ArrayList<Job> servedJobs) {
		this.servedJobs = servedJobs;
	}

	public int[] checkServers() {
		int emptyServerIndex = -1;
		int numberOfEmpty = 0;
		for (int i = 0; i < servers.size(); i++) {
			if (servers.get(i).isEmptyStatus()) {
				emptyServerIndex = i;
				numberOfEmpty ++;
			}
		}
		return new int[] {emptyServerIndex, numberOfEmpty};
	}

	public abstract void startSimulation();
	
	public void startSimulation(ArrayList<Job> listOfJobs, ArrayList<ArrayList<Double>> breakdownList,
			ArrayList<ArrayList<Double>> repairList, ArrayList<Integer> breakdownCounterList) {
		
	}

	public abstract boolean isEndSimulation();
	
	public void reset() {
		servedJobs.clear();
		queue.clear();
		droppedJobs.clear();
		stateTimes.clear();
		for(int i=0; i<serverTimes.length; i++) {
			serverTimes[i] = 0.0;
		}
	}
	
	public int getNumberOfJobsInSystem() {
		int jobsBeingServed = 0;
		for(int i=0; i<servers.size(); i++) {
			if(!servers.get(i).isEmptyStatus())
				jobsBeingServed++;
		}
		return jobsBeingServed + queue.size();
	}
	
	public void updateStateAndServerTimes(double clock, double previousClock) {
		int state = getNumberOfJobsInSystem();
		if(stateTimes.containsKey(state))
			stateTimes.put(state, stateTimes.get(state) + clock - previousClock);
		else
			stateTimes.put(state, clock - previousClock);
		
		for (int j = 0; j < servers.size(); j++) {
			if(!servers.get(j).isEmptyStatus()) {
				serverTimes[j] += clock - previousClock;
			}
		}
		
	}
	
	public abstract double getNumberOfJobsSoFar();
	
	public double getMeanQueueLength() {
		if (clock>0) {
			double meanQueueLength = 0;
			for (int i = 0; i < getNumberOfJobsSoFar()+1; i++) {
				if(stateTimes.containsKey(i))
					meanQueueLength += i*stateTimes.get(i)/clock;
				//else we add zero
			}
			return meanQueueLength;
		}
		else
			return 0;
	}
	
public void calculateMetrics(queues_analytical.Queue theoritical) {
		
		theoritical.calculateAll();
		double totalWaitingTime = 0;
		double avgWaitingTime;
		double totalWaitingTimeCustom = 0;
		int numberOfWaitingJobs = 0;
		double avgWaitingTimeCustom;
		
		System.out.println("Number of Served Jobs " + servedJobs.size());
		
		for(Job job: servedJobs) {
			totalWaitingTime += job.getTimeInQueue();
			if(job.getTimeInQueue() > 0) {
				numberOfWaitingJobs++;
				totalWaitingTimeCustom += job.getTimeInQueue();
			}
		}
		
		avgWaitingTime = totalWaitingTime/getNumberOfJobsSoFar();
		System.out.print("Average Waiting Time: " + avgWaitingTime);
		System.out.println(String.format(" (%.4f%%  of theortical value)", (100*(avgWaitingTime/theoritical.getE_w()))));
		
		avgWaitingTimeCustom = totalWaitingTimeCustom/numberOfWaitingJobs; //might be NaN (division by zero)
		System.out.println("Average Waiting Time for those Who Wait: " + avgWaitingTimeCustom);
		
		System.out.println("State Probabilities: ");
		HashMap<Integer, Double> stateProbabilties = new HashMap<>();
		for (int state : stateTimes.keySet()) {
			stateProbabilties.put(state, stateTimes.get(state)/clock);
		}
		double probabilityAllBusy = 0;
		for (int state : stateProbabilties.keySet()) {
			System.out.print("\tp("+state+") = " + stateProbabilties.get(state));
			System.out.println(String.format(" (%.4f%%  of theortical value)",
					(100*(stateProbabilties.get(state)/theoritical.P_i(state)))));
			if(state>=servers.size())
				probabilityAllBusy += stateProbabilties.get(state);
		}
		System.out.println("The rest are zeros.");
		System.out.print("Probability That All Servers are Busy: "  + probabilityAllBusy);
		System.out.println(String.format(" (%.4f%%  of theortical value)",
				(100*(probabilityAllBusy/theoritical.getP_busy()))));
		
		
		double p0=0.0;
		if(stateProbabilties.containsKey(0))
			p0 = stateProbabilties.get(0);
		double utilization = 1 - p0;
		System.out.println("Utilization for the Whole System: " + utilization);
		
		double averageServerUtilization;
		double tempSum = 0;
		
		for (double workingTime : serverTimes) 
			tempSum += workingTime;
		
		averageServerUtilization = tempSum / (numberOfServers*clock);
		System.out.print("Average Server Utilization: " + averageServerUtilization);
		System.out.println(String.format(" (%.4f%%  of theortical value)",
				(100*(averageServerUtilization/theoritical.getU()))));
	
		
		double meanQueueLength = 0;
		for (int i = 0; i < getNumberOfJobsSoFar()+1; i++) {
			if(stateProbabilties.containsKey(i))
				meanQueueLength += i*stateProbabilties.get(i);
			//else we add zero
		}
		System.out.print("Mean Queue Length: " + meanQueueLength);
		System.out.println(String.format(" (%.4f%%  of theortical value)",
				(100*(meanQueueLength/theoritical.getE_n()))));
		
		
		double throughPut = servedJobs.size() / clock;
		System.out.print("Throughput: " + throughPut);
		System.out.println(String.format(" (%.4f%%  of theortical value)",
				(100*(throughPut/theoritical.getThroughPut()))));
		
		double responseTime = meanQueueLength / throughPut;
		System.out.print("Resonse Time: " + responseTime);
		System.out.println(String.format(" (%.4f%%  of theortical value)",
				(100*(responseTime/theoritical.getE_t()))));
		
		System.out.println("\n---------------- Theoritical Results ----------------\n");
		theoritical.viewPerformance();
	}

}
