
public class RandomVariatesTest {

	RandomVariates rv = new RandomVariates();

	public RandomVariatesTest() {
		// TODO Auto-generated constructor stub
	}

	void ExpTest(double mean) {
		double testMean = 0.0;
		int numberOfTests = 1000000;
		for (int i = 0; i < 1000000; ++i) {
			testMean += rv.exponential(mean);
		}
		testMean /= numberOfTests;

		System.out.println(
				"Mean to be tested : " + Double.toString(mean) + ", Measured Mean : " + Double.toString(testMean));

	}
	
	void logNormalTest(double mean, double variance) {
		double testMean = 0.0;
		int numberOfTests = 1000000;
		for (int i = 0; i < 1000000; ++i) {
			testMean += rv.logNormal(mean, variance);
		}
		testMean /= numberOfTests;

		System.out.println(
				"Mean to be tested : " + Double.toString(mean) + ", Measured Mean : " + Double.toString(testMean));

	}

}
