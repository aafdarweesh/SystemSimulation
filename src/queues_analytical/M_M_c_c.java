package queues_analytical;

import java.lang.Math;
import auxMath.*;

public class M_M_c_c extends Queue{
	private int c;
	public M_M_c_c(double lambda, double mu, int c)
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
		for(int i=0; i<=c; i++)
			sum1 += (Math.pow(c*p,i))/(Factorial.getFact(i));
		P_0 = 1/sum1;
	}
	
	public double P_i(int i)
	{
		return ((Math.pow(c*p,i))/(Factorial.getFact(i))) * P_0;
	}
	
	protected void view_P_i()
	{
		System.out.println("P(i) = (cp)^i / i! * P_0");
	}
	
	protected void Calc_E_n()
	{
		Double sum1 = 0.0;
		for(int i=0; i<=c; i++)
			sum1 += (i*Math.pow(c*p,i))/(Factorial.getFact(i));
		E_n = P_0*sum1;
	}
	
	protected void Calc_throughPut()
	{
		throughPut = mu*E_n;
	}
	
	protected void Calc_E_t()
	{
		E_t = 1/mu;
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
		E_m = 0;
	}
	
	protected void Calc_u()
	{
		u = (E_n - E_m) / c;
	}
	
	protected void Calc_P_busy()
	{
		P_busy = P_0 * ((Math.pow(c*p,c))/(Factorial.getFact(c)));
	}
	
	protected void Calc_P_QueueNotEmpty()
	{
		P_QueueNotEmpty = P_busy - P_i(c);
	}
	
	@Override
	public void viewPerformance()
	{
		System.out.println("Queue Type: M/M/c/c");
		System.out.println("Number of servers = " + c);
		System.out.println("Maximum number of jobs in the system = " + c);
		super.viewPerformance();
	}
}
