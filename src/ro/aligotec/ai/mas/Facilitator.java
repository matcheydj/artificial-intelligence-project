package ro.aligotec.ai.mas;

import java.util.Iterator;
import java.util.LinkedList;

import ro.aligotec.ai.mas.comunication.Communication;
import ro.aligotec.ai.mas.comunication.Message;

public class Facilitator implements Communication, Runnable{
	
	private LinkedList<String> log = new LinkedList<String>();
	private LinkedList<Agent> agents = null;
	private LinkedList<Task> tasks = null;
	
	public Facilitator (){
		
	}
	
	public LinkedList<Agent> getAgents() {
		return agents;
	}

	public void setAgents(LinkedList<Agent> agents) {
		this.agents = agents;
	}

	public LinkedList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(LinkedList<Task> tasks) {
		this.tasks = tasks;
	}

	public Facilitator(LinkedList<Agent> agents, LinkedList<Task> tasks){
		this.agents = agents;
		this.tasks = tasks;
	}
	
	@Override
	public String getId() {
		return "10000";
	}

	@Override
	public synchronized int receive(Message query) {
		logIt("Received messeage from " + 
				query.getSender().getId() + "\n\tMessage Text [" +
				query.getType() + "]: " + query.getText());
		switch(query.getType()){
			case ACK: 	
				return 0;
			case CANCEL:
				/**	AN AGENT CANCELs a TASK */
				if(query.getText().toLowerCase().indexOf("#task")>=0){
					String t = query.getText();
					int end = t.indexOf(")");
					if(t.indexOf(",")>0){
						end = t.indexOf(",");
					}
					cancelAssignment(t.substring(t.indexOf("("), end).trim());
					return 0;
				}
				break;	
			case CFP:
				/**	AN AGENT CALLS FOR PROPOSALS TO EXECUTE THE TASK */
				if(query.getText().toLowerCase().indexOf("#task")>=0){
					String t = query.getText();
					// send to all agents
					Iterator<Agent> ia = agents.iterator();
					while(ia.hasNext()){
						Agent a = ia.next();
							logIt("Sent an offer to " + a.getName() + ": " + t.toString());
							a.receive(new Message(Message.Type.OFFER,
									t.toString(),this,a));
					}
				}
				break;
			case REFUSE://TODO:
				break;
			case REGISTER:
				if(query.getText().toLowerCase().indexOf("something")>=0){
					
					return 0;
				}
				break;
			case REQUEST:
				/** Can request: getTasks */
				if(query.getText().toLowerCase().indexOf("gettasks")>=0){
					query.getSender().receive(new Message(
							Message.Type.INFO,
							returnTasks(query.getSender().getId()),
							this,
							query.getSender()));
					return 0;
				}
				break;
			case INFO:
				/**	SUPPORTED INFO: listAllAgents */
				if(query.getText().toLowerCase().indexOf("listallagents")>=0){
					query.getSender().receive(new Message(
							Message.Type.INFO,
							agentsToString(),
							this,
							query.getSender()));
					return 0;
				}else
				if(query.getText().toLowerCase().indexOf("price for:")>=0){
					Task w = Task.getTaskByName(query.getText().substring(
							query.getText().toLowerCase().indexOf("price for:"),
							query.getText().toLowerCase().indexOf(" is ")));
					double p = Double.parseDouble(query.getText().substring(
							query.getText().toLowerCase().indexOf(" is ") + 3,
							query.getText().length()).trim());
					Agent a = Agent.getAgentByName(query.getSender().getId());
					Agent requester = Agent.getAgentThatHasTheTask(w.getName());
					if((requester == null) || (a == null)) return -1;
					
					requester.receive(new Message(
							Message.Type.INFO,
							"AnswerForCFP: (" + a.getName() + ") can execute [" + w.getName() + "] for <" + p + ">",
							this,
							requester));
					return 0;
				}
				break;
		}
		return -1;
	}
	
	private String agentsToString(){
		StringBuffer r = new StringBuffer();
		r.append("AgentsList: ");
		Iterator <Agent> it = agents.iterator();
		while(it.hasNext()){
			r.append(it.next().getName());
			if(it.hasNext()){
				r.append(",");
			}
		}
		return r.toString();
	}
	
	private String returnTasks(String agentName){
		StringBuffer r = new StringBuffer();
		Iterator <Task> it = tasks.iterator();
		while(it.hasNext()){
			//TODO:
		}
		return r.toString();
	}
	
	private void cancelAssignment(String taskName){
		Task t = Task.getTaskByName(taskName);
		t.assignTo(null);
	}
	
	public void sleep(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		logIt("Started run");
		while(true){
			sleep();
		}
	}

	public String logToString() {
		Iterator<String> it = log.iterator();
		StringBuffer r = new StringBuffer();
		while(it.hasNext()){
			r.append(it.next());
			if(it.hasNext()) r.append("\n");
		}
		return r.toString();
	}
	private void logIt(String what){
		log.add(logTime() + "| " + what);
	}
	private String logTime(){
		return (new java.sql.Timestamp((new java.util.Date()).getTime())).toString();
	}
}
