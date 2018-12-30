package application;

import randomGens.ExponentialGenerator;
import randomGens.LogNormalGenerator;
import randomGens.RandomGenerator;
import randomGens.TestGenerator;
import randomGens.WeibullGenerator;

public class mainApp {

	public static void main(String[] args) {
		
		/*System.out.println("Now testing Expo:");
		TestGenerator.test(new ExponentialGenerator(2.0));
		
		System.out.println("Now testing Lognormal:");
		TestGenerator.test(new LogNormalGenerator(2.0, 1.0));
		
		System.out.println("Now testing weibull:");
		TestGenerator.test(new WeibullGenerator(2.0, 1.0, 0.0));*/
		
		RandomGenerator randomGenerator = new ExponentialGenerator(2);
		for(int i=0; i<60; i++)
			System.out.println(randomGenerator.generate());
	}

}