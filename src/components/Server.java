package components;

public class Server {
	private static double nextID = 0;
	private double id;
	private int timeLastBreakDown = -1;
	private int timeToRepair = -1;
	private boolean emptyStatus; //True is empty, false is not empty 
	private Job jobBeingServed;
	
	
	public Server()
	{
		this.id = nextID++;
		emptyStatus = true;
		jobBeingServed = null;
	} 
	
	//Add new job to the server
	public void addJob(Job job, double clock) {
		job.setServiceStartTime(clock);
		this.jobBeingServed = job;
		this.emptyStatus = false;
	}
	
	public Job finishJob() {
		Job finished = null;
		this.emptyStatus = true;
		finished = this.jobBeingServed;
		this.jobBeingServed = null;
		return finished;
	}
	
	//BreakDown status
	public boolean isBrokeDown(int currentTime) {
		return currentTime >= (this.timeLastBreakDown + this.timeToRepair) ? false : true;
	}
	
	//Repaired
	public void Repair() {
		this.timeLastBreakDown = -1;
		this.timeToRepair = -1;
		this.emptyStatus = true;
		this.jobBeingServed = null; //drop the job (garbage collector will delete it)
	}
	//Breakdown
	public void BreakDown(int breakdownTime, int repairTime) {
		this.timeLastBreakDown = breakdownTime;
		this.timeToRepair = repairTime;
		this.emptyStatus = false;
		this.jobBeingServed = null; //drop the job (garbage collector will delete it)
	}

	public double getId() {
		return id;
	}

	public void setId(double id) {
		this.id = id;
	}

	public int getTimeLastBreakDown() {
		return timeLastBreakDown;
	}

	public void setTimeLastBreakDown(int timeLastBreakDown) {
		this.timeLastBreakDown = timeLastBreakDown;
	}

	public int getTimeToRepair() {
		return timeToRepair;
	}

	public void setTimeToRepair(int timeToRepair) {
		this.timeToRepair = timeToRepair;
	}

	public boolean isEmptyStatus() {
		return emptyStatus;
	}

	public void setEmptyStatus(boolean emptyStatus) {
		this.emptyStatus = emptyStatus;
	}

	public Job getJobBeingServed() {
		return jobBeingServed;
	}

	public void setJobBeingServed(Job jobBeingServed) {
		this.jobBeingServed = jobBeingServed;
	}
	
	
}
