package queues_analytical;

import java.lang.Math;

public class M_M_1 extends Queue{
	public M_M_1(double lambda, double mu)
	{
		super(lambda,mu);
		
	}
	
	protected void Calc_p()
	{
		p = lambda/mu;
	}
	
	protected void Calc_P_0()
	{
		P_0 = 1-p;
	}
	
	public double P_i(int i)
	{
		return Math.pow(p,i) * (1-p);
	}
	
	protected void view_P_i()
	{
		System.out.println("P(i) = p^i * (1-p)");
	}
	
	protected void Calc_E_n()
	{
		E_n = p/(1-p);
	}
	
	protected void Calc_throughPut()
	{
		throughPut = lambda;
	}
	
	protected void Calc_E_t()
	{
		E_t = 1/((1-p)*mu);
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
	
	protected void Calc_P_QueueNotEmpty()
	{
		P_QueueNotEmpty = 1 - P_0 - P_i(1);
	}
	
	protected void Calc_P_busy()
	{
		P_busy = 1- P_0;
	}
	
	@Override
	public void viewPerformance()
	{
		System.out.println("Queue Type: M/M/1");
		if(mu>lambda)
			super.viewPerformance();
		else
			System.out.println("THE SYSTEM IS UNSTABLE");
	}
}
