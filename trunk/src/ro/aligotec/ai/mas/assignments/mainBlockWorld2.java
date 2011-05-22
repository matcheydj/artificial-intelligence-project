package ro.aligotec.ai.mas.assignments;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import ro.aligotec.ai.mas.blockworld.*;
import ro.aligotec.ai.mas.*;

/**
 * Running the BlockWorld with 2 Agents with
 * disjunct interests.
 * 
 * @author TEACA IGOR
 *
 */
public class mainBlockWorld2 extends JFrame implements Runnable{
	private static final long serialVersionUID = 2826122955133123608L;
	private int DIM=16,BLK=16;
	private BlockWorld w = null;
	private JPanel ww=null,g1=null,g2=null;
	
	private JTextArea com1 = new JTextArea(15,10);	//goal1
	private JTextArea com2 = new JTextArea(15,10);	//goal2
	
	private JTextArea aga1 = new JTextArea(15,10);	//plan1
	private JTextArea aga2 = new JTextArea(15,10);	//plan2
	
	private BDIAgent a1=null,a2=null;
	private ReactiveAgent sup1=null;
	
	public mainBlockWorld2(){
		//int d = 600;
		super("BLOCK WORLD: 2 agents with 2 big goals");
		this.isDoubleBuffered();
		this.setSize(800,600);
		this.setLayout(new BorderLayout());
		JButton R = new JButton("LOAD GOAL");
		R.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				System.out.println("LOADING GOALs:");
				a1.setGoal(com1.getText().split("\n"));
				a2.setGoal(com2.getText().split("\n"));
			}
		});
		JButton R2 = new JButton("REPAINT");
		R2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				//System.out.println("NEW WORLD: "+com.getText());
				//w = new world(DIM,BLK);rePaint();
				//w.loadWorld(com.getText());
				//w.print();
				rePaint();
			}
		});
		
		JButton R3 = new JButton("STEP++");
		R3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				System.out.println("Supervisor: Step++");
				sup1.allowStep();
				//try{Thread.sleep(200);}catch(Exception ex){ex.printStackTrace();}
				rePaint();
			}
		});
		JPanel Nord = new JPanel(new FlowLayout());
		Nord.add(R);
		Nord.add(R2);
		Nord.add(R3);
		this.add(Nord,BorderLayout.NORTH);
		//16 -  A B C D E F G I J K L M N O P
		com1.setText("ONTABLE(A)\nON(O,P)\nON(C,A)\nON(B,C)\nONTABLE(P)\nONTABLE(J)\nON(I,J)\nON(K,I)\nON(N,K)\nON(M,N)");
		com2.setText("ONTABLE(A)\nON(O,P)\nON(C,A)\nON(B,C)\nONTABLE(P)\nONTABLE(J)\nON(I,J)\nON(K,I)\nON(N,K)\nON(M,N)");

		w  = new BlockWorld(DIM,BLK);
		ww = w.graphics();
		this.add(ww,BorderLayout.CENTER);
		
		JPanel Est = new JPanel(new GridLayout(2,2));
		JPanel Est1 = new JPanel(new FlowLayout());
		Est.add(com1);
		Est.add(com2);
		Est.add(aga1);
		Est.add(aga2);
		Est1.add(Est);
		this.add(Est1,BorderLayout.EAST);
		
		a1 = new BDIAgent("BDI-Agent1",w);
		a2 = new BDIAgent("BDI-Agent2",w);
		sup1 = new ReactiveAgent("REA-Agent3");
		a1.setSupervisor(sup1);
		a2.setSupervisor(sup1);
		
		a1.setGoal(com1.getText().split("\n"));
		a2.setGoal(com2.getText().split("\n"));
		g1 = a1.graphics();
		g2 = a2.graphics();
		Nord.add(g1);
		Nord.add(g2);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		Thread na1 = new Thread(a1,"th1");
		Thread na2 = new Thread(a2,"th1");
		Thread na3 = new Thread(sup1,"th1");
		na3.start();
		na1.start();
		na2.start();
	}
	public void rePaint(){
		ww = w.graphics();
		g1 = a1.graphics();
		g2 = a2.graphics();
		
		Plan p = a1.getPlan();
		if(p==null)	aga1.setText("none");
		else		aga1.setText(p.toString());
		
		p = a2.getPlan();
		if(p==null)	aga2.setText("none");
		else		aga2.setText(p.toString());
		
		this.paintComponents(getGraphics());		
	}
		
	public static void main(String[] args) {
		mainBlockWorld2 me = new mainBlockWorld2();
		Thread met = new Thread(me,"mainthread");
		met.run();
	}
	
	@Override
	public void run() {
		while(true){
			//System.out.println("repaint");
			rePaint();
			try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
			rePaint();
		}
		
	}
}
