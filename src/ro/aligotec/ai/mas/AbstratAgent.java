package ro.aligotec.ai.mas;

import java.util.LinkedList;

public abstract class AbstratAgent implements Runnable{

	private String name="Agent";
	private double score=0;
	protected boolean freeRun=false,allowStep=false;
	private int sleepTime=1000;
	private LinkedList<String> goal= new LinkedList<String>();
	
	public AbstratAgent(String TheName){
		System.out.println("System: Created Agent: "+TheName);
		if(TheName!=null) name=TheName;
	}
	
	public String getName()			{return name;}
	public double getScore()		{return score;}
	public void addPoints(double p)	{score+=p;}
	public void allowStep()			{allowStep=true;}
	public void setSleepTime(int ms){sleepTime=ms;}
	public void setFreeRun(boolean t){freeRun=t;}
	public int getSleepTime()		{return sleepTime;}
	public boolean getFreeRun()		{return freeRun;}
	public void setGoal(String g)	{goal.clear(); goal.add(g);}
	public void setGoal(String[] g)	{
		goal.clear();
		for(int i=0;i<g.length;i++)
			if(g[i]!=null&&!g[i].isEmpty())
				goal.add(g[i]);
		System.out.println(this.name+"Goal set: "+goal.toString());
	}
	public LinkedList<String> getGoal()		{return goal;}
	
	//main execution procedure
	protected abstract void execution();
	
	protected void sleep(){
		try {Thread.sleep(sleepTime);}
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	@Override
	public void run() {
		while(true){
			if(allowStep||freeRun)	execution();
			sleep();
		}
	}
	
	
}
