import java.util.ArrayList;

public class RandomVariatesTest {

	RandomVariates rv = new RandomVariates();

	public RandomVariatesTest() {
		// TODO Auto-generated constructor stub
	}
	
	private void generateHistogram(int sampleSize, ArrayList<Double> samples, double min, double max)
	{
		int histogramSize = (int)Math.sqrt(sampleSize);
		int[] histogram = new int[histogramSize];
		double histogramIntervalWidth = (max-min)/histogramSize;
		for(double value:samples)
		{
			histogram[(int)((value-min-Math.ulp(value))/histogramIntervalWidth)] += 1;
		}
		System.out.println("Histogram:");
		for(int i=0; i<histogramSize; i++)
		{
			System.out.println("Interval " + i + ": " + histogram[i]);
		}
	}

	void ExpTest(double mean) {
		double testMean = 0.0;
		double firstTest = rv.exponential(mean);
		double max = firstTest;
		double min = firstTest;
		double temp;
		int numberOfTests = 1000000;
		ArrayList<Double> generatedList = new ArrayList<>();
		generatedList.add(firstTest);
		for (int i = 0; i < (numberOfTests-1); ++i) {
			temp = rv.exponential(mean);
			testMean += temp;
			generatedList.add(temp);
			if(temp>max)
				max = temp;
			else if(temp<min)
				min = temp;
		}
		testMean /= numberOfTests;

		System.out.println(
				"Mean to be tested : " + Double.toString(rv.getMean()) + ", Measured Mean : " + Double.toString(testMean));
		generateHistogram(numberOfTests, generatedList, min, max);		
	}
	
	void logNormalTest(double mean, double variance) {
		double testMean = 0.0;
		int numberOfTests = 1000000;
		for (int i = 0; i < 1000000; ++i) {
			testMean += rv.logNormal(mean, variance);
		}
		testMean /= numberOfTests;

		System.out.println(
				"Mean to be tested : " + Double.toString(rv.getMean()) + ", Measured Mean : " + Double.toString(testMean));

	}
	
	void weibullTest(double alpha, double beta, double nu) {
		
		double testMean = 0.0;
		int numberOfTests = 1000000;
		for (int i = 0; i < 1000000; ++i) {
			testMean += rv.weibull(alpha, beta, nu);
		}
		testMean /= numberOfTests;

		System.out.println(
				"Mean to be tested : " + Double.toString(rv.getMean()) + ", Measured Mean : " + Double.toString(testMean));

		
	}

}
