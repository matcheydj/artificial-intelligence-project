package ro.aligotec.ai.mas.blockworld;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import ro.aligotec.ai.mas.blockworld.*;

public class Plan {

	private Queue<String> plan=new LinkedList<String>();
	private Queue<String> goal=new LinkedList<String>();
	private BlockWorld vw=null;
	
	//_________________________PUBLIC_MEMBERS___________________________
	public Plan(Queue<String> g, BlockWorld w){
		vw	= w.copy();
		goal= g;
		sortGoal();
		makePlan();
	}
	public boolean feasibleIn(BlockWorld w){
		if(plan==null) return true;
		String item = plan.peek();
		return w.isActionDoable(item);
	}
	public String getNextAction(){
		if(plan==null) return null;
		return plan.poll();
	}
	public int getSize(){
		return plan.size();
	}
	public boolean completed(){
		if(plan==null) return true;
		return false;
	}
	public void postpone(String action){
		plan.add(action);
	}
	public Queue<String> getPlan(){
		return plan;
	}
	public String toString(){
		if(plan==null) return null;
		String[] col = plan.toArray(new String[0]);
		StringBuffer b = new StringBuffer();
		for(int i=0;i<col.length;i++)
			b.append(col[i]+"\n");
		return b.toString();
	}
	
	//_________________________PRIVATE_MEMBERS___________________________	
	private void makePlan(){
		if(vw==null||goal==null){
			plan=null; return;
		}
		Iterator<String> it = goal.iterator();
		String item = null;
		while(it.hasNext()){
			item=(String)it.next();
			if(!vw.isStateTrue(item)){
				addActionsToPlan(vw.doActionsToRichState(item));
			}
		}
		vw = null;
	}
	private void addActionsToPlan(LinkedList<String> acts){
		Iterator<String> it = acts.iterator();
		while(it.hasNext()){
			this.plan.add(it.next());
		}
	}
	private void sortGoal(){
		Queue<String> sg = new LinkedList<String>();
		for(Iterator<String> h=goal.iterator(); h.hasNext();){
			String item = (String) h.next().trim().toLowerCase();
			if(item.indexOf("ontable(")>=0)
				if(!stateInQueue(item,sg)) sg.add(item);
		}
		boolean change=true;
		while(change||(goal.size()!=sg.size())){
			//System.out.println("->Goal:"+goal.toString());
			change=false;
			StringBuffer pg = new StringBuffer();
			// pg = blocks in sorted goal
			for(Iterator<String> h=sg.iterator(); h.hasNext();){
				String item = (String) h.next().trim().toLowerCase();
				pg.append(parametersOf(item,0));
			}
			// for elements in goal but not in sorted goal
			// and for blocks already in sorted goal
			for(Iterator<String> h=goal.iterator(); h.hasNext();){
				String item = (String) h.next().trim().toLowerCase();
				if(!stateInQueue(item,sg))
					if(parametersOf(item,2).length()>0)
						if(pg.indexOf(parametersOf(item,2))>=0){
							sg.add(item); change=true;
						}
			}
			if(!change){
				for(Iterator<String> h=goal.iterator(); h.hasNext();){
					String item = (String) h.next().trim().toLowerCase();
					if(!stateInQueue(item,sg)){
						if(pg.indexOf(parametersOf(item,2))<0){
							sg.add(item); change=true; break;//out of for
						}
					}
				}
			}
		}
		this.goal = sg;
	}
	
	public static String parametersOf(String action, int parOrder){
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
	
	private boolean stateInQueue(String s, Queue<String> q){
		if(q==null||s==null) return false;
		s=s.trim().toLowerCase();
		for(Iterator<String> h=q.iterator(); h.hasNext();){
			String item = (String) h.next().trim().toLowerCase();
			if(item.equalsIgnoreCase(s)) return true;
		}
		return false;
	}

	public static void main(String[] args) {
		Queue<String> g = new LinkedList<String>();
		g.add("ON(C,A)");
		g.add("ON(E,D)");
		g.add("ON(I,H)");
		g.add("ON(J,I)");
		g.add("ON(D,B)");
		g.add("ONTABLE(A)");
		g.add("ONTABLE(B)");
		BlockWorld w = new BlockWorld(10,10);
		System.out.println(w.toString());
		Plan p = new Plan(g,w);
		System.out.println("Goal: " + p.goal);
		System.out.println("Plan: " + p.plan);
		
		System.out.println(p.vw.toString());
		System.out.println(w.toString());
	}

}
