package ro.aligotec.ai.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;

import ro.aligotec.ai.mas.*;

public class FileParser {
	
	public static  boolean AgentsEconomicState = true;
	public static Facilitator facilitator = null;
	
	private FileParser(){
		
	}
	public static LinkedList<Agent> loadAgents(String fileName){
		LinkedList<Agent> agents = new LinkedList<Agent>();
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while ((strLine = br.readLine()) != null) {
		    	if(strLine.toLowerCase().indexOf("#agent")>=0){
		    		Agent a = Agent.parse(strLine);
		    		if(a != null){
		    			a.setEconomicState(AgentsEconomicState);
		    			agents.add(a);
		    		}
		    	}
		    }
		    System.out.println("Found " + agents.size() + " agent(s) in file " + fileName);
		    in.close();
		}catch (Exception e){
			System.err.println("FileParser Error [Agent]: ");
			e.printStackTrace();
		}
		return agents;
	}
	
	public static LinkedList<Task> loadTasks(String fileName){
		LinkedList<Task> tasks = new LinkedList<Task>();
		try{
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    int ta = 0;
		    while ((strLine = br.readLine()) != null) {
		    	if(strLine.toLowerCase().indexOf("#task")>=0){
		    		Task t = Task.parse(strLine);
		    		if(t != null){
		    			tasks.add(t);
		    		}
		    	}else if(strLine.toLowerCase().indexOf("#assigntask")>=0){
		    		ta++;
		    		Task.parseAssignment(strLine);
		    	}
		    }
		    System.out.println("Found " + tasks.size() + " task(s) in file " + fileName);
		    System.out.println("found also " + ta + " task assignment(s)!");
		    in.close();
		}catch (Exception e){
		  System.err.println("FileParser Error [Task]: ");
		  e.printStackTrace();
		}
		return tasks;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//_____________AGENTS______________
		System.out.println("PRINTING AGENTS: ");
		LinkedList<Agent> agents = FileParser.loadAgents("D:\\myAgents.txt");
		if(agents.size()>0){
			Iterator<Agent> it = agents.iterator();
			while(it.hasNext()){
				System.out.println(it.next().toString());
			}
		}
		//_____________TASKS_______________
		System.out.println("PRINTING TASKS: ");
		LinkedList<Task> tasks = FileParser.loadTasks("D:\\myAgents.txt");
		if(tasks.size()>0){
			Iterator<Task> it = tasks.iterator();
			while(it.hasNext()){
				System.out.println(it.next().toString());
			}
		}
		
	}

}
