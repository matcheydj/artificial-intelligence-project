package ro.aligotec.ai.mas;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Task {	
	private long id = 0;
	private static long idCounter = 1001;
	
	/** Name of the Task*/
	private String name = "Task";
	
	/** The reward given for completing the task*/
	private double reward = 0;
	
	/** Amount of Task - Used for computing total cost of Task as:
	 * Task.amount x Agent.abilityFor(Task).cost */
	private double amount = 1;  
	
	/** Ability Required to complete the Task*/
	private String ability = null;
	
	/** Agent that took the task */
	private Agent assignedTo = null;
	
	/** Agent that completed the Task*/
	private Agent completedBy = null;
	
	/** Completion indicator of the Task*/
	private boolean completed = false;
	
	/** Cost of the Task (depends on the Agent that completed it) */
	private double finalCost = 0;
	
	/** The pool of tasks */
	private static LinkedList<Task> tasks = new LinkedList<Task>();
	
	/** History of the Task assignment */
	private LinkedList<Agent> assignmentHistory = new LinkedList<Agent>();
	
	
	/** Constructor */
	public Task(String name, double amountOfTask, double reward, String reqAbility){
		this.id = generateId();
		this.name = name;
		this.amount = amountOfTask;
		this.ability = reqAbility;
		this.reward = reward;
		tasks.add(this);
	}
	
	/** Task retriever */
	public static Task getTaskByName(String name){
		if(name == null || tasks.size()<1) return null;
		
		if(name.toLowerCase().indexOf("#task")>=0){
			int end = name.toLowerCase().indexOf(")");
			if(name.toLowerCase().indexOf(",")>0){
				end =  name.toLowerCase().indexOf(",");
			}
			name = name.substring(
					name.toLowerCase().indexOf("(") + 1,
					end).trim();
		}
		
		Iterator <Task> it = tasks.iterator();
		while(it.hasNext()){
			Task t = it.next();
			if (t.getName().trim().toLowerCase().equalsIgnoreCase(
					name.trim().toLowerCase())){
				return t;
			}
		}
		return null;
	}
	
	/** Id Generator */
	private long generateId()	{return idCounter++;}
	
	/** Id Getter/Setter */
	public long getId()			{return id;}
	private void setId(long id)		{this.id = id;}
	
	/** Name Getter/Setter */
	public String getName()		{return this.name;}
	private void setName(String name){this.name = name;}
	
	/** Required Ability Getter/Setter */
	public String getRequiredAbility(){return ability;}
	public void setRequiredAbility(String ability){
		this.ability = ability;
	}
	
	/** Assignment Getter/Setter */
	public Agent getAssignedTo(){
		return this.assignedTo;
	}
	
	/**
	 * @param agent - Agent that got the task
	 */
	public void assignTo(Agent agent){
		if(agent == null){
			this.assignedTo = null;
			
			return;
		}
		System.out.println("Assignment: " + this.name + " to " + agent.getName());
		if(this.assignedTo != null){
			this.assignmentHistory.add(this.assignedTo);
		}
		this.assignedTo = agent;
		agent.addTask(this);
	}
	
	/**
	 * @return true if Task has an Agent assigned to
	 */
	public boolean assigned(){
		return (this.assignedTo!=null);
	}
	
	/**
	 * @return List of Agents that got the assignment
	 */
	public List<Agent> getAssignmentHistory(){
		if(this.assignmentHistory.size()>0){
			return this.assignmentHistory;
		}else{
			return null;
		}
	}
	
	/**
	 * @return true if the task was sub-contracted
	 */
	public boolean subcontracted(){
		return (this.assignmentHistory.size()>0);
	}

	public boolean equals(Task task){
		if(this.id == task.id) return true;
		return false;
	}
	
	/**
	 * @param agent - Agent that completed the task
	 */
	public void setCompleted(Agent agent){
		if(this.completed) return;
		
		this.completedBy = agent;
		this.finalCost = this.amount *
			Ability.getAbilityByNameAndAgent(this.ability, agent).getCost();
		this.completed = true;
	}
	
	/**
	 * @return the rewarded amount for completing the task
	 */
	public double getReward(){
		return reward;
	}
	
	/** 
	 * @return boolean = true if task is completed
	 */
	public boolean getCompleted(){
		return this.completed;
	}
	
	/**
	 * @return double - the final cost of completing the task
	 * as per amount of the task and the cost of Agent using the
	 * required ability
	 */
	public double getFinalCost(){
		return this.finalCost;
	}
	
	/**
	 * @param agent - Agent for which to make the estimate
	 * @return double - the cost of completing the task by the
	 * given Agent = [amount of the task] * [cost of Agent using the
	 * required ability]
	 */
	public double getEstimateFor(Agent agent){
		return this.amount *
		Ability.getAbilityByNameAndAgent(this.ability, agent).getCost();
	}
	
	/** @return Agent that completed the task */
	public Agent getCompletedBy(){
		return this.completedBy;
	}
	
	/** Task to String: #Task(TaskName,Amount,Reward,AbilityReq) */
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("#Task(");
		s.append(this.name + ",");
		s.append(this.amount + ",");
		s.append(this.reward + ",");
		s.append(this.ability + ")");
		return s.toString();
	}
	
	/** Parse a string to retrieve a Task Assignment*/
	public static Task parseAssignment(String assignment){
		if(assignment==null || (assignment.toLowerCase().indexOf("#assigntask(")<0)){
			return null;
		}
		// getting the name of the task
		assignment = assignment.substring(
				assignment.toLowerCase().indexOf("(") + 1,
				assignment.length()).trim();
		String taskName = assignment.substring(0,assignment.toLowerCase().indexOf(",")).trim();
		String agentName = assignment.substring(assignment.toLowerCase().indexOf(",") + 1,
				assignment.toLowerCase().indexOf(")")).trim();
		Task t = Task.getTaskByName(taskName);
		Agent a = Agent.getAgentByName(agentName);
		if(a == null){
			System.out.println("Agent[" + agentName +"] not found for assignment!");
			return null;
		}
			
		if(t == null){
			System.out.println("Task[" + taskName + "] not found for assignment!");
			return null;
		}
		
		t.assignTo(a);
		return t;
	}
	
	/** Parse a string to retrieve a Task */
	public static Task parse(String taskAsString){
		if(taskAsString==null || (taskAsString.toLowerCase().indexOf("#task(")<0)){
			return null;
		}
		// getting the name of the task
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf("(") + 1,
				taskAsString.length()).trim();
		String newName = taskAsString.substring(0,taskAsString.toLowerCase().indexOf(",")).trim();
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		//getting the amount of task
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf(",") + 1,
				taskAsString.length()).trim();
		double newAmountOfTask = Double.parseDouble(taskAsString.substring(0,
				taskAsString.toLowerCase().indexOf(",")).trim());
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		//getting the reward of task
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf(",") + 1,
				taskAsString.length()).trim();
		double newReward = Double.parseDouble(taskAsString.substring(0,
				taskAsString.toLowerCase().indexOf(",")).trim());
		
		if(taskAsString==null || taskAsString.trim().isEmpty()) return null;
		//getting the required ability
		taskAsString = taskAsString.substring(
				taskAsString.toLowerCase().indexOf(",") + 1,
				taskAsString.length());
		String newReqAbility = taskAsString.substring(0,taskAsString.toLowerCase().indexOf(")")).trim();
		
		return new Task(newName, newAmountOfTask, newReward, newReqAbility);
	}
	
	/** TESTING ONLY **/
	public static void main(String[] args){
		Task t = Task.parse("#Task(SomeTask,1,10,SomeAbility)");
		System.out.println(t.toString());
	}
}
