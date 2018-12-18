import java.util.Scanner;


import org.apache.commons.math3.special.*;


public class RandomVariates {

	private double mean;
	private double variance;
	Scanner c = new Scanner(System.in);

	RandomVariates() {
		mean = 1.0;
		variance = 1.0;
	}

	public double uniform() {
		double r;
		do {
			r = Math.random();
		} while (r == 0.0 || r == 1.0);
		return r;
	}
	
	public double exponential(double mean)
	{
		this.mean = mean;
		this.variance = mean*mean;
		return -Math.log(uniform())*this.mean;
	}
	
	public double logNormal(double normalMean, double normalVariance)
	{
		this.mean = Math.pow(Math.E, normalMean + normalVariance/2);
		this.variance = (Math.pow(Math.E, normalVariance) - 1) * (Math.pow(Math.E, normalVariance + 2*normalMean));
		double z =   Math.sqrt((-2*Math.log(Math.random()))) * Math.cos(2 * Math.PI * Math.random());
		return Math.pow(Math.E, normalMean + Math.sqrt(normalVariance)*z);
	}

	
	public double weibull(double alpha, double beta, double nu)
	{
		//alpha -> scale
		//beta -> shape
		//nu -> location
		this.mean = nu + alpha*Gamma.gamma(1/beta + 1);
		this.variance = alpha*alpha * (Gamma.gamma(2/beta + 1) - Math.sqrt(Gamma.gamma(1/beta + 1)));
		return (alpha * Math.pow(-Math.log(Math.random()), 1/beta) + nu);
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
