package simulationModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import components.Job;
import components.Server;

public abstract class Simulation {

	protected int numberOfServers;
	protected ArrayList<Job> queue;
	protected ArrayList<Server> servers;
	protected ArrayList<Job> servedJobs;
	protected ArrayList<Job> droppedJobs;
	protected double clock;
	protected HashMap<Integer, Double> stateTimes;
	protected double[] serverTimes;
	protected double[] serverDownTimes;
	protected boolean multipleRepairMen;

	public Simulation(int numberOfServers) {
		
		this.numberOfServers = numberOfServers;
		this.queue = new ArrayList<>();
		this.servers = new ArrayList<>();
		this.servedJobs = new ArrayList<>();
		this.droppedJobs = new ArrayList<>();
		this.stateTimes = new HashMap<>();
		this.serverTimes = new double[numberOfServers];
		this.serverDownTimes = new double[numberOfServers];
		this.multipleRepairMen = false; //not used in all simulation types (only the ones with breakdowns)
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

	public void reset() {
		servedJobs.clear();
		queue.clear();
		droppedJobs.clear();
		stateTimes.clear();
		for(int i=0; i<serverTimes.length; i++) {
			serverTimes[i] = 0.0;
		}
		for(int i=0; i<serverTimes.length; i++) {
			serverDownTimes[i] = 0.0;
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
	
	public void updateStateAndServerTimes_unreliable(double clock, double previousClock) {
		updateStateAndServerTimes(clock, previousClock);
		
		for (int j = 0; j < servers.size(); j++) {
			if(servers.get(j).isBrokeDown(previousClock)) {
				serverDownTimes[j] += clock - previousClock;
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

	public void calculateMetrics_unreliable() {
		System.out.println("---------------- Simulation Results ----------------\n");
		System.out.println("Total Running Time: " + clock);
		int total =  droppedJobs.size() + servedJobs.size();
		System.out.println("Total Number of Jobs Encountered: " + total);
		System.out.println("Number of Dropped Jobs: " + droppedJobs.size());
		System.out.println("Dropping Probability: " + droppedJobs.size() / (double)total);
		
		double totalDownTimeforAll = 0;
		double avgDownTime;
		System.out.println("Down Times For Each Server: ");
		for (int i = 0; i < serverDownTimes.length; i++) {
			System.out.println("\tServer " + i + ": " + serverDownTimes[i]);
			totalDownTimeforAll += serverDownTimes[i];
		}
		avgDownTime = totalDownTimeforAll/servers.size();
		System.out.println("Average Down Time For a Server: " + avgDownTime);
		System.out.println("Probability that a Server is Down: " + avgDownTime/clock);
		
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
		System.out.println("Average Waiting Time: " + avgWaitingTime);
		
		avgWaitingTimeCustom = totalWaitingTimeCustom/numberOfWaitingJobs; //might be NaN (division by zero)
		System.out.println("Average Waiting Time for those Who Wait: " + avgWaitingTimeCustom);
		
		System.out.println("State Probabilities: ");
		HashMap<Integer, Double> stateProbabilties = new HashMap<>();
		for (int state : stateTimes.keySet()) {
			stateProbabilties.put(state, stateTimes.get(state)/clock);
		}
		double probabilityAllBusy = 0;
		for (int state : stateProbabilties.keySet()) {
			System.out.println("\tp("+state+") = " + stateProbabilties.get(state));
			if(state>=servers.size())
				probabilityAllBusy += stateProbabilties.get(state);
		}
		System.out.println("The rest are zeros.");
		System.out.println("Probability That All Servers are Busy: "  + probabilityAllBusy);
		
		
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
		System.out.println("Average Server Utilization: " + averageServerUtilization);
	
		
		double meanQueueLength = 0;
		for (int i = 0; i < getNumberOfJobsSoFar()+1; i++) {
			if(stateProbabilties.containsKey(i))
				meanQueueLength += i*stateProbabilties.get(i);
			//else we add zero
		}
		System.out.println("Mean Queue Length: " + meanQueueLength);
		
		double throughPut = servedJobs.size() / clock;
		System.out.println("Throughput: " + throughPut);
		
		double responseTime = meanQueueLength / throughPut;
		System.out.println("Resonse Time: " + responseTime);
		
	}
	
	public int getNextServer_modified() {

		int nextServer = -1;
		double minimumTime = Double.POSITIVE_INFINITY;
		int i = 0;
		while (i < servers.size()) {
			if (servers.get(i).isBrokeDown(clock) == false && servers.get(i).isEmptyStatus() == false
					&& servers.get(i).getJobBeingServed().getServiceEndTime() < minimumTime) {
				nextServer = i;
				minimumTime = servers.get(i).getJobBeingServed().getServiceEndTime();
			}
			i++;
		}

		return nextServer;

	}
	
	public int getNextRepair() {

		int nextServer = -1;
		double minimumTime = Double.POSITIVE_INFINITY;
		int i = 0;
		while (i < servers.size()) {
			if (servers.get(i).isBrokeDown(clock) == true &&
					servers.get(i).getRepairedTime() < minimumTime) {
				nextServer = i;
				minimumTime = servers.get(i).getRepairedTime();
			}
			i++;
		}

		return nextServer;

	}
	
	public int chooseBreakDownServer() {
		ArrayList<Integer> functionalServers = new ArrayList<Integer>();
		int i = 0;
		while (i < servers.size()) {
			if (!servers.get(i).isBrokeDown(clock)) {
				functionalServers.add(i);
			}
			i++;
		}
		if(functionalServers.isEmpty())
			return -1; 
		else {
			int rnd = new Random().nextInt(functionalServers.size());
		    return functionalServers.get(rnd);
		}
	}
	
	public boolean isMultipleRepairMen() {
		return multipleRepairMen;
	}

	public void setMultipleRepairMen(boolean multipleRepairMen) {
		this.multipleRepairMen = multipleRepairMen;
	}
	
	public double getRepairManFreeTime() {
		if(isMultipleRepairMen())
			return 0; //there is a repair man available all the time
		double freeTime = 0; //now
		int i = 0;
		while (i < servers.size()) {
			if (servers.get(i).isBrokeDown(clock)) {
				if(servers.get(i).getTimeToRepair() > freeTime)
					freeTime = servers.get(i).getTimeToRepair();
			}
			i++;
		}
		
		return freeTime;
	}

}
