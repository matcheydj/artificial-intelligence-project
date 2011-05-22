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
 * Running the BlockWorld with 1 Agent.
 * 
 * 
 * @author TEACA IGOR
 *
 */
public class mainBlockWorld1 extends JFrame implements Runnable{
	private static final long serialVersionUID = 2826122955133123608L;
	private int DIM=16,BLK=16;
	private BlockWorld w = null;
	private JPanel ww=null,g1=null;
	
	private JTextArea com = new JTextArea(15,10);	//goal
	private JTextArea aga = new JTextArea(15,10);	//plan
	private BDIAgent a1=null;
	
	public mainBlockWorld1(){
		//int d = 600;
		super("BLOCK WORLD: 1 agent with 1 big goal");
		this.isDoubleBuffered();
		this.setSize(700,600);
		this.setLayout(new BorderLayout());
		JButton R = new JButton("LOAD GOAL");
		R.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { 
				System.out.println("LOAD GOAL: "+com.getText());
				a1.setGoal(com.getText().split("\n"));
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
				System.out.println("Step++");
				a1.allowStep();
				try{Thread.sleep(200);}catch(Exception ex){ex.printStackTrace();}
				rePaint();
			}
		});
		JPanel Nord = new JPanel(new FlowLayout());
		Nord.add(R);
		Nord.add(R2);
		Nord.add(R3);
		this.add(Nord,BorderLayout.NORTH);
		//16 -  A B C D E F G I J K L M N O P
		com.setText("ONTABLE(A)\nON(O,P)\nON(C,A)\nON(B,C)\nONTABLE(P)\nONTABLE(J)\nON(I,J)\nON(K,I)\nON(N,K)\nON(M,N)");
		//this.add(aga);
		w  = new BlockWorld(DIM,BLK);
		ww = w.graphics();
		this.add(ww,BorderLayout.CENTER);
		
		JPanel Est = new JPanel(new GridLayout(2,1));
		Est.add(com);
		Est.add(aga);
		this.add(Est,BorderLayout.EAST);
		
		a1 = new BDIAgent("BDI-Agent1",w);
		a1.setGoal(com.getText().split("\n"));
		g1 = a1.graphics();
		Nord.add(g1);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		Thread na1 = new Thread(a1,"th1");
		na1.start();
	}
	public void rePaint(){
		ww = w.graphics();
		g1 = a1.graphics();
		Plan p = a1.getPlan();
		if(p==null)	aga.setText("none");
		else		aga.setText(p.toString());
		this.paintComponents(getGraphics());		
	}
		
	public static void main(String[] args) {
		mainBlockWorld1 me = new mainBlockWorld1();
		Thread met = new Thread(me,"mainthread");
		met.run();
	}
	
	@Override
	public void run() {
		while(true){
			//System.out.println("repaint");
			rePaint();
			try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
			//rePaint();
		}
		
	}
}
