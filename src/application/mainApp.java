package application;

import generator.PacketGenerator;
import generator.ServerBreakdownGenerator;
import queues_CI.MMC_CI;
import queues_analytical.M_M_c;
import queues_analytical.M_M_c_L;
import simulationModels.MMC;
import simulationModels.MMCBreakdown;
import simulationModels.MMCL;
import simulationModels.MMCLBreakdown;

public class mainApp {

	public static void main(String[] args) {

		/*
		 * System.out.println("Now testing Expo:"); TestGenerator.test(new
		 * ExponentialGenerator(2.0));
		 * 
		 * System.out.println("Now testing Lognormal:"); TestGenerator.test(new
		 * LogNormalGenerator(2.0, 1.0));
		 * 
		 * System.out.println("Now testing weibull:"); TestGenerator.test(new
		 * WeibullGenerator(2.0, 1.0, 0.0));
		 */

		/*
		 * RandomGenerator randomGenerator = new ExponentialGenerator(2); for(int i=0;
		 * i<60; i++) System.out.println(randomGenerator.generate());
		 */

		
		 
		PacketGenerator pG = new PacketGenerator();
		// pG.DisplayListOfJobs();
		pG.GeneratePackets(1000000, 3, 2);
		
		//ServerBreakdownGenerator sBG = new ServerBreakdownGenerator(3, 60, 10);//each 1 hour there is a failure, and repair time is 10mins
		//sBG.GenerateBreakdownAndRepair();
		
		System.out.println("\n\nMMC\n\n");
		
		MMC trial = new MMC(3);
		trial.startSimulation(pG.RetrieveListOfJobs());
		// or we can directly say trial.startSimulation(3, 2, 1000000); (overloaded)
		M_M_c trialAnalytical = new M_M_c(1/3.0, 1/2.0, 3);
		trial.calculateMetrics(trialAnalytical);
		
		
		System.out.println("\n\nMMCL\n\n");
		
		MMCL trial2 = new MMCL(3, 4);
		M_M_c_L trial2Analytical = new M_M_c_L(1/3.0, 1/2.0, 3, 4);
		trial2.startSimulation(pG.RetrieveListOfJobs());
		// or we can directly say trial2.startSimulation(3, 2, 1000000); (overloaded)
		trial2.calculateMetrics(trial2Analytical);
		
		/*
		System.out.println("\n\nMMC Confidence Interval Expirement\n\n"); //Confidence Interval Experiment: stops too early!!!
		MMC_CI trial5 = new MMC_CI(3, 2, 3);
		trial5.startSimulation();
		trial5.calculateMetrics(trialAnalytical);*/
		
		
		
		
		
		/*System.out.println("\n\nMMC with breakdown\n\n");
		
		MMCBreakdown trial3 = new MMCBreakdown(3, 100);
		trial3.startSimulation(pG.RetrieveListOfJobs(), sBG.RetrieveBreakdowns(), sBG.RetrieveRepair(), sBG.breakdownCounter());
		trial3.showResult();
		
		System.out.println("\n\nMMCL with breakdown\n\n");
		
		MMCLBreakdown trial4 = new MMCLBreakdown(3, 100, 2);
		trial4.startSimulation(pG.RetrieveListOfJobs(), sBG.RetrieveBreakdowns(), sBG.RetrieveRepair(), sBG.breakdownCounter());
		trial4.showResult();
		
		
		pG.DisplayListOfJobs();*/
		
	}

}
