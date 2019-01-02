package application;

import generator.PacketGenerator;
import simulationModels.MMC;
import simulationModels.MMCL;

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

		/*
		 * MMC trial = new MMC(1, 1, 3, 100);
		 * 
		 * trial.startSimulation(); trial.showResult();
		 */
		PacketGenerator pG = new PacketGenerator();
		// pG.DisplayListOfJobs();
		pG.GeneratePackets(100, 3, 2);
		
		MMC trial = new MMC(3, 100);
		trial.startSimulation(pG.RetrieveListOfJobs());
		trial.showResult();
		
		
		MMCL trial2 = new MMCL(3, 100, 2);
		trial2.startSimulation(pG.RetrieveListOfJobs());
		trial2.showResult();
		
		
		pG.DisplayListOfJobs();
		
	}

}
