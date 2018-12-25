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
	} 
	
	//Add new job to the server
	public void addJobTotheServer(Job job) {
		this.jobBeingServed = job;
		this.emptyStatus = false;
	}
	
	//BreakDown status
	public boolean isBreakDown(int currentTime) {
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
}
