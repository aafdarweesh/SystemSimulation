package queues_analytical;

import java.lang.Math;
import auxMath.*;

public class M_M_c extends Queue{

	private int c;
	public M_M_c(double lambda, double mu, int c)
	{
		super(lambda,mu);
		this.c = c;
	}
	
	protected void Calc_p()
	{
		p = lambda/(mu*c);
	}
	
	protected void Calc_P_0()
	{
		Double sum1 = 0.0;
		for(int i=0; i<c; i++)
			sum1 += (Math.pow(c*p,i))/(Factorial.getFact(i));
		P_0 = 1/(sum1 + (Math.pow(c*p,c))/(Factorial.getFact(c) * (1-p)));
	}
	
	public double P_i(int i)
	{
		if(i<c)
			return ((Math.pow(c*p,i))/(Factorial.getFact(i))) * P_0;
		else
			return ((Math.pow(c, c)*Math.pow(p,i))/(Factorial.getFact(c))) * P_0;
	}
	
	protected void view_P_i()
	{
		System.out.println("if i<c, P(i) = (cp)^i / i! * P_0");
		System.out.println("else, P(i) = ((c^c * p^i) / c!) * P_0");
	}
	
	protected void Calc_E_n()
	{
		Double sum1 = 0.0;
		for(int i=0; i<c; i++)
			sum1 += (i*Math.pow(c*p,i))/(Factorial.getFact(i));
		E_n = P_0*(sum1 + ((Math.pow(c*p,c))/(Factorial.getFact(c))) * (c+p-c*p)/Math.pow(1-p,2));
	}
	
	protected void Calc_throughPut()
	{
		Double sum1 = 0.0;
		for(int i=1; i<c; i++)
			sum1 += (i*Math.pow(c*p,i))/(Factorial.getFact(i));
		throughPut = mu*P_0*(sum1 + (c*(Math.pow(c*p,c))/(Factorial.getFact(c))) * (1)/(1-p));
	}
	
	protected void Calc_E_t()
	{
		E_t = E_n/throughPut;
	}
	
	protected void Calc_E_s()
	{
		E_s = 1/mu;
	}
	
	protected void Calc_E_w()
	{
		E_w = E_t - E_s;
	}
	
	protected void Calc_E_m()
	{
		E_m = lambda * E_w;
	}
	
	protected void Calc_u()
	{
		u = p;
	}
	
	protected void Calc_P_busy()
	{
		P_busy = P_0 * ((Math.pow(c*p,c))/(Factorial.getFact(c))) * (1)/(1-p);
	}
	
	protected void Calc_P_QueueNotEmpty()
	{
		P_QueueNotEmpty = P_busy - P_i(c);
	}
	
	@Override
	public void viewPerformance()
	{
		System.out.println("Queue Type: M/M/c");
		System.out.println("Number of servers = " + c);
		System.out.println("mu: " + mu + ", lamda: " + lambda + ", c*mu: " + (c*mu));
		if((c*mu)>lambda)
			super.viewPerformance();
		else
			System.out.println("THE SYSTEM IS UNSTABLE");
	}
}
