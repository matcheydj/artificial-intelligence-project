package ro.aligotec.ai.mas;

import java.util.Iterator;
import java.util.LinkedList;

public class Ability {
	private long id = 0;
	private long idCounter = 8001;
	
	private double cost = 0;	//cost of using the ability as per unit of Task
	private String name = "Task";
	private Agent agent = null;
	
	public static LinkedList<Ability> allAbilities = new LinkedList<Ability>();
	
	/** Constructor - Private */
	private Ability(String name, Agent AgentAssigned, double costOfUse){
		this.id = generateId();
		this.name = name;
		this.agent = AgentAssigned;
		this.cost = costOfUse;
		allAbilities.add(this);
	}
	
	public static Ability AbilityFactory(String name, Agent AgentAssigned, double costOfUse){
		Iterator<Ability> it = allAbilities.iterator();
		while(it.hasNext()){
			Ability a = it.next();
			String existing = a.name;
			if(existing.toLowerCase().equalsIgnoreCase(name.trim().toLowerCase())){
				if(a.getAgentAssigned().equals(AgentAssigned)){
					return null;
				}
			}
		}
		Ability a = new Ability(name.trim(), AgentAssigned, costOfUse);
		return a;
	}
	
	public static Ability getAbilityByNameAndAgent(String name, Agent agent){
		Iterator<Ability> it = allAbilities.iterator();
		while(it.hasNext()){
			Ability a = it.next();
			String existing = a.name;
			if(existing.toLowerCase().equalsIgnoreCase(name.trim().toLowerCase())){
				if(a.agent.equals(agent)){
					return a;
				}
			}
		}
		return null;
	}
	
	/** Id Generator */
	private long generateId()	{return idCounter++;}
	
	/** Id Getter/Setter */
	public long getId()			{return id;}
	public void setId(long id)		{this.id = id;}
	
	/** Name Getter/Setter */
	public String getName()		{return this.name;}
	public void setName(String name){this.name = name;}
	
	/** Assigned Agent Getter/Setter */
	public Agent getAgentAssigned()		{return this.agent;}
	public void setAgentAssigned(Agent agent){this.agent = agent;}
	
	public double getCost(){
		return this.cost;
	}
	
	public boolean equals(Ability ability){
		if(this.id == ability.id){
			return true;
		}else{
			return false;
		}
	}

	
}
