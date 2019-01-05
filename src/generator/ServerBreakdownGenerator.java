package generator;

import java.util.ArrayList;

import randomGens.ExponentialGenerator;

public class ServerBreakdownGenerator {

	ArrayList<ArrayList<Double>> breakdownList;
	ArrayList<ArrayList<Double>> repairList;
	
	ExponentialGenerator exponentialGeneratorBreakdown;
	ExponentialGenerator exponentialGeneratorRepair;
	int numberOfServer;
	
	double meanBreakdown;

	public ServerBreakdownGenerator(int numberOfServers, double meanBreakdown, double meanRepair) {

		exponentialGeneratorBreakdown = new ExponentialGenerator(meanBreakdown);
		exponentialGeneratorRepair = new ExponentialGenerator(meanRepair);
		this.numberOfServer = numberOfServers;

		this.breakdownList = new ArrayList<ArrayList<Double>>();
		this.repairList = new ArrayList<ArrayList<Double>>();
		
		this.meanBreakdown = meanBreakdown;

	}

	//Generate breakdowns and repairs for 5 iterations
	public void GenerateBreakdownAndRepair() {
		for (int i = 0; i < numberOfServer; ++i) {
			breakdownList.add(new ArrayList<Double>(100));
			repairList.add(new ArrayList<Double>(100));
			for (int j = 0; j < 100; ++j) {
				if(j != 0) {
					breakdownList.get(i).add(this.breakdownList.get(i).get(j-1) + exponentialGeneratorBreakdown.generate());
					repairList.get(i).add(exponentialGeneratorRepair.generate());
				}else {
					breakdownList.get(i).add(exponentialGeneratorBreakdown.generate());
					repairList.get(i).add(exponentialGeneratorRepair.generate());
				}
			}
		}
	}
	
	//Make a copy of the breakdown list and return it
	public ArrayList<ArrayList<Double>> RetrieveBreakdowns(){
		ArrayList<ArrayList<Double>> breakdownCopy = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < numberOfServer; ++i) {
			breakdownCopy.add(new ArrayList<Double>(100));
			//repairList.add(new ArrayList<Double>());
			for (int j = 0; j < 100; ++j) {
				breakdownCopy.get(i).add(breakdownList.get(i).get(j));
				//repairList.get(i).add(exponentialGeneratorRepair.generate());
			}
		}
		return breakdownCopy;
	}
	
	//Make a copy of the repair list and return it
	public ArrayList<ArrayList<Double>> RetrieveRepair(){
		ArrayList<ArrayList<Double>> repairCopy = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < numberOfServer; ++i) {
			repairCopy.add(new ArrayList<Double>(100));
			for (int j = 0; j < 100; ++j) {
				repairCopy.get(i).add(repairList.get(i).get(j));
			}
		}
		return repairCopy;
	}
	
	public ArrayList<Integer> breakdownCounter() {
		ArrayList<Integer> breakdownCounter = new ArrayList<Integer>(); // to count the number of breakdowns as they are independent
		
		for (int i = 0; i < numberOfServer; ++i) {
			breakdownCounter.add(0);
		}
		return breakdownCounter;
	}
	
}
