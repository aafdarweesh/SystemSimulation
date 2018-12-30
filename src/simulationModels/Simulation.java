package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;

public abstract class Simulation {

	protected double meanInterArrivalTime;
	protected double meanSerivceTime;
	protected double numberOfServers;
	protected double numberOfJobs;
	protected ArrayList<Job> queue;
	protected ArrayList<Server> servers;
	protected ArrayList<Job> servedJobs;
	protected double clock;

	public Simulation(double meanInterArrivalTime, double meanSerivceTime, double numberOfServers, double numberOfJobs) {
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanSerivceTime = meanSerivceTime;
		this.numberOfServers = numberOfServers;
		this.numberOfJobs = numberOfJobs;
		this.queue = new ArrayList<>();
		this.servers = new ArrayList<>();
		this.servedJobs = new ArrayList<>();
	}

	public double getMeanInterArrivalTime() {
		return meanInterArrivalTime;
	}

	public void setMeanInterArrivalTime(double meanInterArrivalTime) {
		this.meanInterArrivalTime = meanInterArrivalTime;
	}

	public double getMeanSerivceTime() {
		return meanSerivceTime;
	}

	public void setMeanSerivceTime(double meanSerivceTime) {
		this.meanSerivceTime = meanSerivceTime;
	}

	public double getNumberOfServers() {
		return numberOfServers;
	}

	public void setNumberOfServers(double numberOfServers) {
		this.numberOfServers = numberOfServers;
	}

	public double getNumberOfJobs() {
		return numberOfJobs;
	}

	public void setNumberOfJobs(double numberOfJobs) {
		this.numberOfJobs = numberOfJobs;
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

	public abstract boolean isEndSimulation();
}
