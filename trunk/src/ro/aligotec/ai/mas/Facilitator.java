package ro.aligotec.ai.mas;

import java.util.Iterator;
import java.util.LinkedList;

import ro.aligotec.ai.mas.comunication.Communication;
import ro.aligotec.ai.mas.comunication.Message;

public class Facilitator implements Communication, Runnable{
	
	private LinkedList<Agent> agents = null;
	private LinkedList<Task> tasks = null;
	
	
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
		while(true){
			sleep();
		}
	}
	
}
