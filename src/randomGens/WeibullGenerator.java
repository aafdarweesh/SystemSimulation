package randomGens;

import org.apache.commons.math3.special.Gamma;

public class WeibullGenerator extends RandomGenerator {

	private double alpha, beta, nu;
	public WeibullGenerator(double alpha, double beta, double nu) {
		this.alpha = alpha;
		this.beta = beta;
		this.nu = nu;
		this.setMean(nu + alpha*Gamma.gamma(1/beta + 1));
		this.setVariance(alpha*alpha * (Gamma.gamma(2/beta + 1) - Math.sqrt(Gamma.gamma(1/beta + 1))));
	}
	@Override
	public double generate() {
		return (alpha * Math.pow(-Math.log(UniformGenerator.generate()), 1/beta) + nu);
	}

}
