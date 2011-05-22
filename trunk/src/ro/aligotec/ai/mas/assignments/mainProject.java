package ro.aligotec.ai.mas.assignments;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import ro.aligotec.ai.mas.blockworld.*;
import ro.aligotec.ai.mas.*;
import ro.aligotec.ai.utils.FileParser;
import ro.aligotec.ai.utils.STRING;


public class mainProject extends JFrame implements ActionListener {
	private static final long serialVersionUID = 5680462659533494743L;
	
	private JButton btnLoad	= new JButton("LOAD");
	private JButton btnRun	= new JButton("RUN");
	
	private JTextField txtFile = new JTextField("Load a file...");
	private JPanel agentsPanel = new JPanel(new FlowLayout());
	
	private JTextArea txtTasks = new JTextArea(15,15);
	private JTextArea txtLogs = new JTextArea(15,15);
	
	private LinkedList<Agent> agents = new LinkedList<Agent>();
	private LinkedList<Task> tasks = new LinkedList<Task>();
	private LinkedList<Thread> threads = new LinkedList<Thread>();
	
	private void setLayout(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1000, 600);
		//this.removeAll();
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		btnLoad.setActionCommand("LOAD");
		btnLoad.addActionListener(this);
		topPanel.add(btnLoad, BorderLayout.WEST);
		topPanel.add(txtFile, BorderLayout.CENTER);
		btnRun.setActionCommand("RUN");
		btnRun.addActionListener(this);
		topPanel.add(btnRun, BorderLayout.EAST);
		this.add(topPanel, BorderLayout.NORTH);
		
		addAgents();
		this.add(agentsPanel, BorderLayout.SOUTH);
		
		JPanel centerPanel = new JPanel(new GridLayout(1,2));
		
		//txtTasks.setText("TASKS:\n");
		txtTasks.setWrapStyleWord(true);
		txtTasks.setBackground(new Color(100,200,80));
		centerPanel.add(txtTasks);
		//txtLogs.setText("LOGS:\n");
		txtLogs.setAutoscrolls(true);
		txtLogs.setWrapStyleWord(true);
		txtLogs.setBackground(new Color(100,132,200));
		centerPanel.add(txtLogs);
		
		this.add(centerPanel, BorderLayout.CENTER);
		//agentsPanel.removeAll();
		this.validate();
		System.out.println("Layout set!");
	}
	
	private void addAgents(){
		if(agents.size()<1) return;
		agentsPanel.removeAll();
		Iterator<Agent> it = agents.iterator();
		while(it.hasNext()){
			Agent a = it.next();
			JButton btnA = new JButton(a.getName());
			btnA.setActionCommand(a.getName());
			btnA.addActionListener(this);
			agentsPanel.add(btnA);
		}
		this.validate();
	}
	
	public mainProject(){
		super("Communication assignment...");
		/*
		//test
			Agent a = new Agent("John001");
			agents.add(a);
			a = new Agent("Bill-921");
			agents.add(a);
		//test
		 */
		setLayout();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		
		mainProject p = new mainProject();

	}
	private void stopThreads(){
		if(threads.size()>0){
			Iterator<Thread> it = threads.iterator();
			while(it.hasNext()){
				it.next().stop();
				it.remove();
			}
		}
	}
	private void createThreads(){
		if(threads.size()>0){
			Iterator<Agent> it = agents.iterator();
			while(it.hasNext()){
				Agent a = it.next();
				threads.add(new Thread(a,a.getName()));
			}
		}
	}
	private void startAllThreads(){
		if(threads.size()>0){
			Iterator<Thread> it = threads.iterator();
			while(it.hasNext()){
				it.next().start();
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.getActionCommand());
		if(arg0.getActionCommand().equalsIgnoreCase("LOAD")){
			//stop all current threads
			stopThreads();
			//agentsPanel.removeAll();
			final JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(this);
            txtFile.setText(fc.getSelectedFile().getAbsolutePath());
            agents = FileParser.loadAgents(txtFile.getText());
            tasks = FileParser.loadTasks(txtFile.getText());
            
            txtTasks.setText("");
            addAgents();
            txtLogs.setText("File loaded: " + txtFile.getText());
            /* */
            Iterator<Task> it = tasks.iterator();
            while(it.hasNext()){
            	System.out.println("Task loaded: " + it.next().toString());
            }
            createThreads();
            startAllThreads();
		}else if(arg0.getActionCommand().equalsIgnoreCase("RUN")){
			
		}else{
			Agent a = Agent.getAgentByName(arg0.getActionCommand());
			txtTasks.setText("INFO: \n" + STRING.repeat("-", 100) +"\n" +
					a.toString() + "\n\n\n" + a.tasksToString());
			
			txtLogs.setText("LOG OF " + a.getName() + ":\n" +
					STRING.repeat("-", 100)+"\n" + a.logToString());
		}
	}

}
