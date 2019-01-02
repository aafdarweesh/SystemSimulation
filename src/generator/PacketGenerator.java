package generator;

import java.util.ArrayList;

import components.Job;
import randomGens.ExponentialGenerator;

public class PacketGenerator {

	ArrayList<Job> listOfJobs;

	public PacketGenerator() {
		this.listOfJobs = new ArrayList<Job>();
	}

	/**
	 * @param numberOfPackets
	 * @param meanInterArrivalTime
	 * @param meanSerivceTime
	 * @return List of Jobs generated
	 */
	public ArrayList<Job> GeneratePackets(int numberOfPackets, int meanInterArrivalTime, int meanSerivceTime) {
		ExponentialGenerator exponentialGeneratorServiceTime = new ExponentialGenerator(meanSerivceTime);
		ExponentialGenerator exponentialGeneratorArrivalTime = new ExponentialGenerator(meanInterArrivalTime);

		double timeCounter = 0;
		Job j0 = new Job(timeCounter, exponentialGeneratorServiceTime.generate());

		this.listOfJobs.add(j0);

		// generate the inter-arrival time for the packets, following exponential
		// distribution
		for (int i = 1; i < numberOfPackets; ++i) {

			double inter_arrivalTime = exponentialGeneratorArrivalTime.generate();
			timeCounter += inter_arrivalTime;

			// generate a new job with exponential inter-arrival time, and service time
			Job j = new Job(timeCounter, exponentialGeneratorServiceTime.generate());

			// add the job to the list
			this.listOfJobs.add(j);
		}

		return this.listOfJobs;
	}

	public void DisplayListOfJobs() {
		System.out.println("List of Jobs generated : " + this.listOfJobs.size());
		for (int i = 0; i < this.listOfJobs.size(); ++i) {
			System.out.println("Job ID : " + this.listOfJobs.get(i).getId() + ", Arrival time : "
					+ this.listOfJobs.get(i).getArrivalTime() + ", Service time : "
					+ this.listOfJobs.get(i).getServiceTime());
		}
		
		System.out.println("END");
	}

}
