package ro.aligotec.ai.mas;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ro.aligotec.ai.mas.comunication.Communication;
import ro.aligotec.ai.mas.comunication.Message;
import ro.aligotec.ai.utils.FileParser;
import ro.aligotec.ai.utils.STRING;

/**
 * <b>Agent</b><br/>
 * 
 * Basic Agent Object - provides basic information about
 * an agent
 * 
 * @author Igor Teaca
 *
 */
public class Agent implements Communication, Runnable{
	private class W{
		public Task what = null;
		public Agent who = null;
		public double price = 0;
		public W(Task t, Agent a, double p){
			what = t;
			who = a;
			price = p;
		}
	}
	
	private long id = 0;
	private long idCounter = 1000001;
	
	private String name = "Agent";
	
	private double budget = 0;
	private double balance = 0;
	
	/** ECONOMIC - The Agent will try to delegate
	 *  even the tasks it can do but which can be
	 *  executed by other agents at a lower cost */
	private boolean ECONOMIC = true;
	private LinkedList<W> offers = new LinkedList<W>(); 
	
	private LinkedList<Goal> goals = new LinkedList<Goal>();
	private LinkedList<Task> tasks = new LinkedList<Task>();
	private boolean tmod = true;	//task modified - for synchronization
	
	private LinkedList<Ability> abilities = new LinkedList<Ability>();
	private LinkedList<Task> completedTasks = new LinkedList<Task>();
	
	/** LOGGING: */
	private LinkedList<String> log = new LinkedList<String>();
		
	
	private static LinkedList<Agent> agents = new LinkedList<Agent>();
	
	
	public static Agent getAgentByName(String name){
		if(agents.size()<1) return null;
		Iterator<Agent> it = agents.iterator();
		while(it.hasNext()){
			Agent a = it.next();
			if(a.getName().trim().toLowerCase().equalsIgnoreCase(
					name.trim().toLowerCase())){
				return a;
			}
		}
		return null;
	}
	/**
	 * Constructor
	 */
	public Agent (){
		this.id = this.generateId();
		this.name = this.name + this.id;
		agents.add(this);
	}
	
	/**
	 * Constructor
	 */
	public Agent(String name){
		this();
		this.name = name;
	}
	
	public boolean equals(Agent agent){
		return (this.id == agent.id ||
				this.name.equalsIgnoreCase(agent.getName()));
	}
	
	private long generateId(){return idCounter++;}
	/**
	 * Id Getter/Setter
	 */
	public String getId(){
		return this.name;
	}
	private void setId(long id){this.id = id;}
	/**
	 * Name Getter/Setter
	 */
	public String getName(){return this.name;}
	private void setName(String name){this.name = name;}
	
	/**
	 * Goal Getter/Setter/Adder
	 */
	public List<Goal> getGoals(){return goals;}
	public void setGoals(List<Goal> goals){this.goals.addAll(goals);}
	public void addGoal(Goal goal){this.goals.add(goal);}
	
	/**
	 * @param amount of the budget
	 */
	private void setBudget(double amount){
		this.budget = amount;
	}
	/**
	 * @return Budget of the Agent
	 */
	public double getBudget(){
		return this.budget;
	}
	
	/**
	 * @param amount of profit made so far
	 */
	private void setProfit(double amount){
		this.balance = amount;
	}
	/**
	 * @return the Profit of the Agent  
	 */
	public double getProfit(){
		return this.balance;
	}
	
	/**
	 * Ability adder
	 */
	public void addAbility(Ability ability){this.abilities.add(ability);}
	
	public boolean hasAbility(Ability ability){
		Iterator<Ability> it = abilities.iterator();
		while(it.hasNext()){
			if(it.next()==ability) return true;
		}
		return false;
	}
	public boolean hasAbility(String abilityName){
		return hasAbility(Ability.getAbilityByNameAndAgent(abilityName, this));
	}
	
	/**
	 * Task adder
	 */
	public void addTask(Task task){
		//verify if the task is already assigned
		System.out.println("Assign Task " + task.getName() + " to Agent " + this.name);
		if((task.getAssignedTo() == null) || !task.getAssignedTo().equals(this)){
			task.assignTo(this);
		}
		if(tasks.size()>0){
			Iterator<Task> it = tasks.iterator();
			while(it.hasNext()){
				if(it.next().equals(task)){
					System.out.println("Task " + task.getName() + " not added!");
					return;					
				}
			}
		}
		this.tasks.add(task);
	}
	public boolean hastTask(String task){
		return hasTask(Task.getTaskByName(task));
	}
	public boolean hasTask(Task task){
		return tasks.contains(task);
	}
	
	public static Agent getAgentThatHasTheTask(String task){
		if(agents.size()>0){
			Iterator<Agent> it = agents.iterator();
			while(it.hasNext()){
				Agent a = it.next();
				if(a.hastTask(task)) return a;
			}
		}
		return null;
	}
	
	public boolean canDoTheTask(Task task){
		//if(task.getEstimateFor(this)>this.budget) return false;
		return (this.hasAbility(task.getRequiredAbility()));
		
	}
	
	public synchronized void setTaskCompleted(Task task){
		
		Iterator<Task> it = tasks.iterator();
		
		while(it.hasNext()){
			Task t = it.next();
			if(task.equals(t)){
				completedTasks.add(t);
				t.setCompleted(this);
				balance += t.getReward() - t.getFinalCost();
				budget -= t.getFinalCost();
				this.logIt("I have completed " + task.getName() + 
						" with cost " + t.getFinalCost() + 
						" but rewarded with " + t.getReward());
				it.remove();
				return;
			}
		}
	}
	public String tasksToString(){
		StringBuffer r = new StringBuffer();
		r.append("TASKS:\n");
		r.append(STRING.repeat("-", 100));
		if(tasks.size()>0){
			System.out.println("" + this.name + " has " + tasks.size() + " tasks.");
			Iterator<Task> it = tasks.iterator();
			while(it.hasNext()){
				r.append("\n>>> " + it.next().toString());
			}
		}else{
			r.append("\nNONE");
		}
		return r.toString();
	}
	
	public String toString(){
		//#Agent(NumeAgent,budget,balance,[Ability1,Cost1,Ability2,Cost2...])
		StringBuffer s = new StringBuffer();
		s.append("#Agent(");
		s.append(this.name + ",");
		s.append(this.budget + ",");
		s.append(this.balance + ",[");
		Iterator<Ability> it = this.abilities.iterator();
		while(it.hasNext()){
			Ability a = it.next();
			if(a != null){
				s.append(a.getName() + "," + a.getCost());
				if(it.hasNext()) s.append(",");
			}
		}
		s.append("])");
		return s.toString();
	}
	
	public static Agent parse(String agentAsString){
		String taskAsString = agentAsString;
		if(taskAsString==null || (taskAsString.toLowerCase().indexOf("#agent(")<0)){
			return null;
		}
		// getting the name of the task
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf("(") + 1,
				taskAsString.length()).trim();
		String newName = taskAsString.substring(0,taskAsString.toLowerCase().indexOf(",")).trim();
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		//getting the budget of the Agent
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf(",") + 1,
				taskAsString.length()).trim();
		double newBudget = Double.parseDouble(taskAsString.substring(0,
				taskAsString.toLowerCase().indexOf(",")).trim());
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		//getting the balance of the Agent
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf(",") + 1,
				taskAsString.length()).trim();
		double newBalance = Double.parseDouble(taskAsString.substring(0,
				taskAsString.toLowerCase().indexOf(",")).trim());
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		
		Agent newAgent = new Agent(newName);
		newAgent.setBudget(newBudget);
		newAgent.setProfit(newBalance);
		
		//getting the ability(es)
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf("[") + 1,
				taskAsString.toLowerCase().indexOf("]"));
		String[] abilities = taskAsString.split(",");
		if(abilities.length>1 && (abilities.length%2 == 0)){
			System.out.println("Creating new Agent: " + abilities.length/2 + " ability(ies) found!");
			for(int i = 0; i < abilities.length/2; i++){
				Ability newAbility = Ability.AbilityFactory(abilities[i*2], newAgent, Double.parseDouble(abilities[i*2+1]));
				newAgent.addAbility(newAbility);
			}
		}else{
			System.out.println("Creating new Agent: no abilities found!");
		}

		return newAgent;
	}
	
	/** FOR TESTING */
	public static void main(String[] args){
		Agent t = Agent.parse("#Agent(SomeAgent,100,0,[SomeAbility1,10,SomeAbility2,20])");
		System.out.println(t.toString());
	}
	
	private void addOffer(W offer){
		Iterator<W> it = offers.iterator();
		while(it.hasNext()){
			W o = it.next();
			if(o.what == offer.what && o.who == offer.who){
				o.price = offer.price;
				return;
			}
		}
		offers.add(offer);
	}
	
	@Override
	public synchronized int receive(Message query) {
		logIt("Received messeage from " + 
				query.getSender().getId() + "\n\tMessage Text [" +
				query.getType() + "]: " + query.getText());
		switch(query.getType()){
			case ACK:{
				return 0;
			}
			case CANCEL:{
				return 0;
			}
			case DELEGATE:{
				if(query.getText().toLowerCase().indexOf("#task")>=0){
					Task t = Task.getTaskByName(query.getText());
					if(t == null) return -1;
					if(this.canDoTheTask(t)){
						query.getSender().receive(new Message(
								Message.Type.ACK,"",this,query.getSender()));
						this.setTaskCompleted(t);
						//t.setCompleted(this);
						tmod=true;
						this.tasks.remove(t);
						return 0;
					}else{ //refuses the offer 
						FileParser.facilitator.receive(new Message(
								Message.Type.CANCEL,query.getText(),this,FileParser.facilitator));
						return -1;
					}
				}
				break;
			}
			case OFFER:{
				if(query.getText().toLowerCase().indexOf("#task")>=0){
					Task t = Task.getTaskByName(query.getText());
					if(t == null) return -1;
					if(this.hasAbility(t.getRequiredAbility())){
						// sends back the price
						query.getSender().receive(new Message(
								Message.Type.INFO,
								"PRICE FOR: " + t.getName() + " IS " + t.getEstimateFor(this),
								this,query.getSender()));
					}else{ //refuses the offer 
						query.getSender().receive(new Message(
								Message.Type.REFUSE,
								"CAN'T DO <t>" + t.getName() + "</t>!",this,query.getSender()));
					}
				}
				break;
			}
			case REFUSE:{
				if(query.getText().toLowerCase().indexOf("answerforcfp:")>=0){
					Task w = Task.getTaskByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("<t>")+3,
							query.getText().toLowerCase().indexOf("</t>")).trim());
					Agent a = Agent.getAgentByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("<a>")+3,
							query.getText().toLowerCase().indexOf("</a>")).trim());
					this.addOffer(new W(w, a, Double.MAX_VALUE));
					return 0;
				}
			}
			case INFO:{
				if(query.getText().toLowerCase().indexOf("price for:")>=0){
					Task w = Task.getTaskByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("price for:"),
							query.getText().toLowerCase().indexOf(" is ")));
					double p = Double.parseDouble(query.getText().substring(
							query.getText().toLowerCase().indexOf(" is ") + 3,
							query.getText().length()).trim());
					Agent a = Agent.getAgentByName(query.getSender().getId());
					this.addOffer(new W(w, a, p));
					return 0;
				}else
				if(query.getText().toLowerCase().indexOf("agentslist:")>=0){
					//agent already has access to list of agents
					/* Basically the agent should make himself a list with
					 * agents and with Agent.getAgentByName(name) populate
					 * this list - LinkedList<Agent> agents */
					System.out.println("Agent " + this.name + " received the list of all agents.");
					return 0;
				}else
				if(query.getText().toLowerCase().indexOf("answerforcfp:")>=0){
					Task w = Task.getTaskByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("<t>")+3,
							query.getText().toLowerCase().indexOf("</t>")).trim());
					double p = Double.parseDouble(query.getText().substring(
							query.getText().toLowerCase().indexOf("<p>") + 3,
							query.getText().toLowerCase().indexOf("</p>")).trim());
					Agent a = Agent.getAgentByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("<a>")+3,
							query.getText().toLowerCase().indexOf("</a>")).trim());
					this.addOffer(new W(w, a, p));
					return 0;
				}
				break;
			}
		}
		return -1;
	}
	
	public void setEconomicState(boolean b){
		this.ECONOMIC = b;
	}
	
	public void askForHelp(){
		if(tasks.size()>0){
			while(tmod){
				tmod=false;
				Iterator<Task> it = tasks.iterator();
				while(it.hasNext()){
					if(tmod){
						break;
					}
					Task t = it.next();
					if(ECONOMIC || this.hasAbility(t.getRequiredAbility())){
						if(FileParser.facilitator == null){
							// send to all agents except himself
							Iterator<Agent> ia = agents.iterator();
							while(ia.hasNext()){
								Agent a = ia.next();
								if(a != this){ 
									logIt("Sent an offer to " + a.getName() + ": " + t.toString());
									a.receive(new Message(Message.Type.OFFER,
											t.toString(),this,a));
								}else{//SAME... debugging...
									logIt("Sent an offer to " + a.getName() + ": " + t.toString());
									a.receive(new Message(Message.Type.OFFER,
											t.toString(),this,a));
								}
							}
						}else{
							// send to facilitator only
							logIt("Call for proposals through Facilitator: " + t.toString());
							FileParser.facilitator.receive(new Message(Message.Type.CFP,
									t.toString(),this,FileParser.facilitator));
						}
					}
				}
			}
		}
	}
	
	private boolean hasTasksToComplete(){
		return (tasks.size()>0);
	}
	
	@Override
	public void run() {
		logIt("Started run");
		while(hasTasksToComplete()){
			askForHelp();
			while(!receivedAnswers()){
				sleep();
			}
		}
		logIt("Done my tasks.");
		// Completed his tasks waiting to help others 
		while(true){
			sleep();
		}
	}
	
	private boolean receivedAnswerFor(Task task){
		if(offers.size()>0){
			int noOfAns = 0, noOfAgents = agents.size();
			Iterator<W> it = offers.iterator();
			while(it.hasNext()){
				W o = it.next();
				if(o.what.equals(task)){
					noOfAns++;
				}
			}
			if(noOfAns >= noOfAgents - 1){
				double bestPrice = Double.MAX_VALUE;
				Agent winA = this;
				Iterator<W> it2 = offers.iterator();
				while(it2.hasNext()){
					W o = it2.next();
					if(o.what.equals(task)){
						if(o.price<bestPrice){
							winA = o.who;
						}
					}
				}
				if(winA==this){
					this.logIt("Best price for " + task.getName() + " is mine!");
					this.setTaskCompleted(task);
				}else{
					this.logIt("Best price for " + task.getName() + " is " + winA.getName() +"'s!");
					tasks.remove(task);
					task.assignTo(winA);
					if(winA.receive(new Message(Message.Type.DELEGATE,task.toString(),this,winA)) <0){
						FileParser.facilitator.receive(new Message(
								Message.Type.CANCEL,task.toString(),this,FileParser.facilitator));
					}
				}
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	private boolean receivedAnswers(){
		if(tasks.size()>0){
			Iterator<Task> it = tasks.iterator();
			while(it.hasNext()){
				Task t = it.next();
				if(!receivedAnswerFor(t)){
					return false;
				}else{
					//TODO:
					return false;
				}
			}
		}
		return true;
	}
	
	
	public void sleep(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public String logToString(){
		Iterator<String> it = log.iterator();
		StringBuffer r = new StringBuffer();
		while(it.hasNext()){
			r.append(it.next());
			if(it.hasNext()) r.append("\n");
		}
		return r.toString();
	}
	private void logIt(String what){
		String x = logTime() + "| " + what;
		log.add(x);
		System.out.println(x);
	}
	private String logTime(){
		return (new java.sql.Timestamp((new java.util.Date()).getTime())).toString();
	}
}
