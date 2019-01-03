package queues_analytical;

import java.lang.Math;

public class M_M_1_L extends Queue{
	
	private int L;
	public M_M_1_L(double lambda, double mu, int L)
	{
		super(lambda,mu);
		this.L = L;
	}
	
	protected void Calc_p()
	{
		p = lambda/mu;
	}
	
	protected void Calc_P_0()
	{
		if(p==1)
			P_0 = 1.0/(L+1.0);
		else
			P_0 = (p-1)/(Math.pow(p,L+1)-1);
	}
	
	public double P_i(int i)
	{
		return Math.pow(p,i) * P_0;
	}
	
	protected void view_P_i()
	{
		System.out.println("P(i) = p^i * (p-1)/(p^(l+1) -1)");
	}
	
	protected void Calc_E_n()
	{
		if(p==1)
			E_n = P_0 * 0.5 * L * (L+1.0);
		else
			E_n = p * (L*Math.pow(p,L+1) - (L+1)*Math.pow(p,L) + 1)/((Math.pow(p,L+1)-1)*(p-1));
	}
	
	protected void Calc_throughPut()
	{
		if(p==1)
			throughPut = mu * P_0 * L;
		else
			throughPut = lambda * (1-Math.pow(p, L))/(1-Math.pow(p, L+1));
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
		if (p==1)
			E_m = P_0 * 0.5 * L * (L-1.0);
		else
			E_m = P_0 * p * ((L-1)*Math.pow(p,L+1) - L*Math.pow(p,L) + p)/Math.pow(p-1,2);
	}
	
	protected void Calc_u()
	{
		u = (E_n - E_m)/1;
	}
		
	protected void Calc_P_QueueNotEmpty()
	{
		P_QueueNotEmpty = 1 - P_0 - P_i(1);
	}
	
	protected void Calc_P_busy()
	{
		P_busy = 1 - P_0;
	}
	
	@Override
	public void viewPerformance()
	{
		System.out.println("Queue Type: M/M/1/L");
		System.out.println("Max number of jobs in the system(L) = " + L);
		super.viewPerformance();
	}
}
