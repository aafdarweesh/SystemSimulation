import java.util.Scanner;

public class RandomVariates {
	
	private double mean;
	private double variance;
	Scanner c =new Scanner(System.in);
	
	RandomVariates(){
		mean = 1.0;
		variance = 1.0;
	}
	
	public double uniform() {
		double r;
		do {
			r = Math.random();
		}while(r == 0.0 || r == 1.0);
		return r;
	}
	
	public double exponential(double mean)
	{
		this.mean = mean;
		this.variance = mean*mean;
		return -Math.log(uniform())*this.mean;
	}
	
	public double logNormal(double mean, double variance)
	{
		this.mean = mean;
		this.variance = variance;
		double z =   Math.sqrt((-2*Math.log(Math.random()))) * Math.cos(2 * Math.PI * Math.random());
		return Math.pow(Math.E, mean + Math.sqrt(variance)*z);
	}
	


	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}
	
	
	

}
