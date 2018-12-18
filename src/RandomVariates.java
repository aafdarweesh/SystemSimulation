import java.util.Scanner;

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

}
