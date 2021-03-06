package randomGens;


public class LogNormalGenerator extends RandomGenerator {

	private double normalMean, normalVariance;
	public LogNormalGenerator(double mean) {
		this.setMean(mean);
		normalMean = Math.log(mean) - getVariance()/2;
		this.setVariance((Math.pow(Math.E, 0.03) - 1) * (Math.pow(Math.E, 0.03 + 2*normalMean)));
		normalVariance = 0.03; //approximates to a variance of 1 
	}
	public LogNormalGenerator(double normalMean, double normalVariance) {
		this.normalMean = normalMean;
		this.normalVariance = normalVariance;
		this.setMean(Math.pow(Math.E, normalMean + normalVariance/2));
		this.setVariance((Math.pow(Math.E, normalVariance) - 1) * (Math.pow(Math.E, normalVariance + 2*normalMean)));
	}
	public double generate() {
		double z =   Math.sqrt((-2*Math.log(UniformGenerator.generate()))) * Math.cos(2 * Math.PI * UniformGenerator.generate());
		return Math.pow(Math.E, normalMean + Math.sqrt(normalVariance)*z);
	}

}
