package ro.aligotec.ai.mas;

import java.util.*;
import ro.aligotec.ai.mas.comunication.*;

public class ReactiveAgent extends AbstratAgent implements Communication{	
	
	private Set<Communication> agents = null;
	private Set<Message> requests = null;
	
	private long tms = 0;
	
	public ReactiveAgent(String TheName) {
		super(TheName);
		agents = new HashSet<Communication>();
		requests = new HashSet<Message>();
	}
	
	private boolean allAgentsRequested(){
		int i=0;
		Iterator<Message> it = requests.iterator();
		while(it.hasNext()){
			Message m = it.next();
			if(agents.contains(m.getSender()))	i++;
			else	System.out.println(this.getName()+
						"- ERROR: received requests from unregistred Agents ->"+m.getText());
			
		}
		if(agents.size()==i){
			System.out.println(this.getName()+": All agents has sent the requests!");
			return true;
		}
		return false;
	}
	private boolean qsOK(String M1, String M2){
		//TODO: ?parameters compare?
		System.out.println(M1+"=?"+M2);
		return M1.equalsIgnoreCase(M2);
	}
	private String parametersOf(String action, int parOrder){
		if(action.isEmpty()) return "#";
		//System.out.println("parametersOf: "+action);
		String al="",be="";
		if((action.toLowerCase().indexOf("on(")>=0)||(action.toLowerCase().indexOf("stack(")>=0)||
				(action.toLowerCase().indexOf("unstack(")>=0)){
			al = action.substring(action.indexOf("(")+1, action.indexOf(",")).trim();
			be = action.substring(action.indexOf(",")+1, action.indexOf(")")).trim();
		}else if((action.toLowerCase().indexOf("ontable(")>=0)||(action.toLowerCase().indexOf("putdown(")>=0)||
				(action.toLowerCase().indexOf("pickup(")>=0)||(action.toLowerCase().indexOf("clear(")>=0)){
			al = action.substring(action.indexOf("(")+1, action.indexOf(")")).trim();
		}
		if(parOrder==1)	return al;
		else if(parOrder==2) if(be.isEmpty()) return "#"; else return be; 
		return al+be;
	}
	private void broadcastAnswers(){
		System.out.println(this.getName()+":Requests Received:"+requests.toString());
		Iterator<Message> it = requests.iterator();
		while(it.hasNext()){
			Message m = it.next();
			String M1 = m.getText();
			Iterator<Message> it2 = requests.iterator();
			while(it2.hasNext()){
				Message m2 = it2.next();
				String M2 = m2.getText();
				if(!m.equals(m2) && qsOK(M1,M2)){
					System.out.println(this.getName()+": Agents in conflict: "+m.getText());
					Random gen = new Random();
					int k = gen.nextInt(2);
					if(k==0){
						Message ans = new Message(Message.Type.DENIED,"NO!",this,m.getSender());
						m.getSender().receive(ans);
						it.remove();
						return;
					}else{
						Message ans = new Message(Message.Type.DENIED,"NO!",this,m2.getSender());
						m2.getSender().receive(ans);
						it2.remove();
						return;
					}
				}
			}
		}
		//sending 'go' to all agents
		System.out.println(this.getName()+": Sending OKs!");
		it = requests.iterator();
		while(it.hasNext()){
			Message m = it.next();
			m.getSender().receive(new Message(Message.Type.ALLOWED,"OK",this,m.getSender()));
		}
		//delete old requests: only if a 'go' answer was send to every agent:
		requests = new HashSet<Message>();
	}

	@Override
	public int receive(Message query) {
		System.out.println(this.getName()+": Received->"+query.getType()+": "+query.getText());
		if(query.getType()==Message.Type.REQUEST){
			requests.add(query);
			if(allAgentsRequested()&&this.allowStep)
					broadcastAnswers();
			return 1;
		}else if(query.getType()==Message.Type.REGISTER){
			agents.add(query.getSender());
			return 1;
		}else if(query.getType()==Message.Type.ACK){
			return 1;
		}else if(query.getType()==Message.Type.OK){
			return 1;
		} 
		System.out.println("Communication problem!");
		return -1;
	}

	@Override
	protected void execution() {
		while(true){
			this.sleep();
			if(allowStep){
				tms++;
				if(tms>5){
					if(requests.size()<1){
						System.out.println(this.getName()+": Forcing GO!");
						Iterator<Communication> it = agents.iterator();
						while(it.hasNext()){
							Communication a = it.next();
							a.receive(new Message(Message.Type.ALLOWED,"GO",this,a));
						}
					}
					tms=0;
				}
			}
		}
	}
	@Override
	public String getId() {
		return this.getName();
	}
}
