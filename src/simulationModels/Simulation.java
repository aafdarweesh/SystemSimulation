package simulationModels;

import java.util.ArrayList;

import components.Job;
import components.Server;

public abstract class Simulation {

	protected double numberOfServers;
	protected double numberOfJobs;
	protected ArrayList<Job> queue;
	protected ArrayList<Server> servers;
	protected ArrayList<Job> servedJobs;
	protected ArrayList<Job> droppedJobs;
	protected double clock;

	public Simulation(double numberOfServers, double numberOfJobs) {
		
		this.numberOfServers = numberOfServers;
		this.numberOfJobs = numberOfJobs;
		this.queue = new ArrayList<>();
		this.servers = new ArrayList<>();
		this.servedJobs = new ArrayList<>();
		this.droppedJobs = new ArrayList<>();
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

	public abstract void startSimulation(ArrayList<Job> listOfJobs);

	public abstract boolean isEndSimulation();
}
