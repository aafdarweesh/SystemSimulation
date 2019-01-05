package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import draw.StdDraw;
import queues_analytical.M_M_c;
import queues_analytical.M_M_c_L;
import randomGens.ExponentialGenerator;
import randomGens.LogNormalGenerator;
import randomGens.TestGenerator;
import randomGens.WeibullGenerator;
import simulationModels.MMC;
import simulationModels.MMCBreakdown;
import simulationModels.MMCL;
import simulationModels.MMCLBreakdown;

public class mainApp {

	public static void main(String[] args) {

		int c, maxLength, numberOfJobs, temp;
		double miat, mst, mtbf, mttr, tempDouble;
		boolean errorFlag=false;
		queues_analytical.Queue theoriticalModel;
		int choice, exit=0;
		Scanner in = new Scanner(System.in);
		System.out.println("\n--------------------------------------------------------------");
		System.out.println("\nMain Menu:");
		System.out.println("1->M/M/C \n2->M/M/C/L \n3->M/M/C (w/ breakdowns) \n4->M/M/C/L (w/breakdowns) \n5->Test Input Generators \nelse->exit");
		System.out.print("Your choice: ");
		try {
		choice = in.nextInt();
		in.nextLine();
		} catch (InputMismatchException e) {
			in.nextLine();
			System.out.println("Invalid input; please enter integers only for the menu options!!");
			choice = 6;
			errorFlag = true;
		}
		while(exit != 1)
		{
			try {
			switch(choice)
			{
				case 1:
					
					System.out.println("Enter number of Servers: ");
					c = in.nextInt();
					in.nextLine();
					if(c<1)
						throw new Exception("Error: The number of servers has to be 1 or more");
					
					MMC mmc = new MMC(c);
					
					System.out.println("Enter mean inter-arrival TIME (minutes): ");
					miat = in.nextDouble();
					in.nextLine();
					if(miat <= 0)
						throw new Exception("Error: Mean inter-arrival time has to be positive");
					
					System.out.println("Enter mean service TIME (minutes): ");
					mst = in.nextDouble();
					in.nextLine();
					if(mst <= 0)
						throw new Exception("Error: Mean service time has to be positive");
					
					System.out.println("Enter total number of jobs to simulate: ");
					numberOfJobs = in.nextInt();
					in.nextLine();
					if(numberOfJobs < 1)
						throw new Exception("Error: The number of jobs has to be 1 or more");
					
					mmc.startSimulation(miat, mst, numberOfJobs);
					
					theoriticalModel = new M_M_c(1/(double)miat, 1/(double)mst, c);
					mmc.calculateMetrics(theoriticalModel);
					
					break;
					
				case 2:
					
					System.out.println("Enter number of Servers: ");
					c = in.nextInt();
					in.nextLine();
					if(c<1)
						throw new Exception("Error: The number of servers has to be 1 or more");
					
					System.out.println("Enter L (maximum number of jobs in the system at a time): ");
					maxLength = in.nextInt();
					in.nextLine();
					if(maxLength<1)
						throw new Exception("Error: L has to be 1 or more");
					
					MMCL mmcl = new MMCL(c, maxLength);
					
					System.out.println("Enter mean inter-arrival TIME (minutes): ");
					miat = in.nextDouble();
					in.nextLine();
					if(miat <= 0)
						throw new Exception("Error: Mean inter-arrival time has to be positive");
					
					System.out.println("Enter mean service TIME (minutes): ");
					mst = in.nextDouble();
					in.nextLine();
					if(mst <= 0)
						throw new Exception("Error: Mean service time has to be positive");
					
					System.out.println("Enter total number of jobs to simulate: ");
					numberOfJobs = in.nextInt();
					in.nextLine();
					if(numberOfJobs < 1)
						throw new Exception("Error: The number of jobs has to be 1 or more");
					
					mmcl.startSimulation(miat, mst, numberOfJobs);
					
					theoriticalModel = new M_M_c_L(1/(double)miat, 1/(double)mst, c, maxLength);
					mmcl.calculateMetrics(theoriticalModel);
					
					break;
					
				case 3:
					
					System.out.println("Enter number of Servers: ");
					c = in.nextInt();
					in.nextLine();
					if(c<1)
						throw new Exception("Error: The number of servers has to be 1 or more");
					
					MMCBreakdown mmcBreakdown = new MMCBreakdown(c);
					
					System.out.println("Enter mean inter-arrival TIME (minutes): ");
					miat = in.nextDouble();
					in.nextLine();
					if(miat <= 0)
						throw new Exception("Error: Mean inter-arrival time has to be positive");
					
					System.out.println("Enter mean service TIME (minutes): ");
					mst = in.nextDouble();
					in.nextLine();
					if(mst <= 0)
						throw new Exception("Error: Mean service time has to be positive");
					
					System.out.println("Enter mean TIME between failures (minutes): ");
					mtbf = in.nextDouble();
					in.nextLine();
					if(mtbf <= 0)
						throw new Exception("Error: Mean time between failures has to be positive");
					
					System.out.println("Enter mean TIME to repair (minutes): ");
					mttr = in.nextDouble();
					in.nextLine();
					if(mttr <= 0)
						throw new Exception("Error: Mean time to repair has to be positive");
					
					System.out.println("1->One Repairman, 2->Multiple Repairmen: ");
					temp = in.nextInt();
					in.nextLine();
					if(temp != 1 && temp != 2)
						throw new Exception("Invalid choice!!");
					if(temp == 2)
						mmcBreakdown.setMultipleRepairMen(true);
					else 
						mmcBreakdown.setMultipleRepairMen(false);
					
					System.out.println("Enter total number of jobs to simulate: ");
					numberOfJobs = in.nextInt();
					in.nextLine();
					if(numberOfJobs < 1)
						throw new Exception("Error: The number of jobs has to be 1 or more");
					
					mmcBreakdown.startSimulation(miat, mst, mtbf, mttr, numberOfJobs);
					
					mmcBreakdown.calculateMetrics_unreliable();
					
					break;
					
				case 4:
					
					System.out.println("Enter number of Servers: ");
					c = in.nextInt();
					in.nextLine();
					if(c<1)
						throw new Exception("Error: The number of servers has to be 1 or more");
					
					System.out.println("Enter L (maximum number of jobs in the system at a time): ");
					maxLength = in.nextInt();
					in.nextLine();
					if(maxLength<1)
						throw new Exception("Error: L has to be 1 or more");
					
					MMCLBreakdown mmclBreakdown = new MMCLBreakdown(c, maxLength);
					
					System.out.println("Enter mean inter-arrival TIME (minutes): ");
					miat = in.nextDouble();
					in.nextLine();
					if(miat <= 0)
						throw new Exception("Error: Mean inter-arrival time has to be positive");
					
					System.out.println("Enter mean service TIME (minutes): ");
					mst = in.nextDouble();
					in.nextLine();
					if(mst <= 0)
						throw new Exception("Error: Mean service time has to be positive");
					
					System.out.println("Enter mean TIME between failures (minutes): ");
					mtbf = in.nextDouble();
					in.nextLine();
					if(mtbf <= 0)
						throw new Exception("Error: Mean time between failures has to be positive");
					
					System.out.println("Enter mean TIME to repair (minutes): ");
					mttr = in.nextDouble();
					in.nextLine();
					if(mttr <= 0)
						throw new Exception("Error: Mean time to repair has to be positive");
					
					System.out.println("1->One Repairman, 2->Multiple Repairmen: ");
					temp = in.nextInt();
					in.nextLine();
					if(temp != 1 && temp != 2)
						throw new Exception("Invalid choice!!");
					if(temp == 2)
						mmclBreakdown.setMultipleRepairMen(true);
					else 
						mmclBreakdown.setMultipleRepairMen(false);
					
					System.out.println("Enter total number of jobs to simulate: ");
					numberOfJobs = in.nextInt();
					in.nextLine();
					if(numberOfJobs < 1)
						throw new Exception("Error: The number of jobs has to be 1 or more");
					
					mmclBreakdown.startSimulation(miat, mst, mtbf, mttr, numberOfJobs);
					
					mmclBreakdown.calculateMetrics_unreliable();
					
					break;
					
				case 5:
					
					System.out.println("1->Exponential, 2->LogNormal, 3->Weibull: ");
					temp = in.nextInt();
					in.nextLine();
					if(temp != 1 && temp != 2 && temp != 3)
						throw new Exception("Invalid choice!!");
					
					System.out.println("Enter the mean to be tested: ");
					tempDouble = in.nextDouble();
					in.nextLine();
					if(tempDouble <= 0)
						throw new Exception("Error: Only positive means are allowed in this test");
					
					switch (temp) {
					case 1:
						TestGenerator.test(new ExponentialGenerator(tempDouble));
						break;
						
					case 2:
						TestGenerator.test(new LogNormalGenerator(tempDouble));
						break;

					default:
						TestGenerator.test(new WeibullGenerator(tempDouble));
						break;
					}
					break;	
						
				default:
					if(errorFlag)
						errorFlag = false;
					else
						exit = 1;
					break;
				}
			} catch (InputMismatchException e) {
				in.nextLine();
				System.out.println("Invalid input");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			if(exit != 1)
			{
				System.out.println("\n--------------------------------------------------------------");
				System.out.println("\nMain Menu:");
				System.out.println("1->M/M/C \n2->M/M/C/L \n3->M/M/C (w/ breakdowns) \n4->M/M/C/L (w/breakdowns) \n5->Test Input Generators \nelse->exit");
				System.out.print("Your choice: ");
				try {
				choice = in.nextInt();
				in.nextLine();
				} catch (InputMismatchException e) {
					in.nextLine();
					System.out.println("Invalid input; please enter integers only for the menu options!!");
					choice = 6;
					errorFlag = true;
				}
			}
		}
		in.close();
		StdDraw.closeFrame();
		System.out.println("Program Terminated");
		System.exit(0);
		
	}

}
