package simulationModels;

public abstract class Simulation {

	protected double meanInterArrivalTime;
	protected double meanSerivceTime;
	protected double numberOfServers;
	protected double numberOfJobs;
	
	public Simulation(double meanInterArrivalTime, double meanSerivceTime, double numberOfServers,
			double numberOfJobs) {
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.meanSerivceTime = meanSerivceTime;
		this.numberOfServers = numberOfServers;
		this.numberOfJobs = numberOfJobs;
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
	
	public abstract void startSimulation();
}
