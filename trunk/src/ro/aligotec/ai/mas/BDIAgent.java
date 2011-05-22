package ro.aligotec.ai.mas;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ro.aligotec.ai.mas.blockworld.*;
import ro.aligotec.ai.mas.comunication.*;

public class BDIAgent extends AbstratAgent implements Communication{
	private BlockWorld world=null;
	private boolean HasSupervisor=false;
	private int MyArm=0;
	private Communication Sup=null;
	private Plan p;
	private boolean ActionAllowed=true,LastMessageAns=false;
	private JLabel lbl_arm;
	private JPanel pnl_arm;
	private String mile=null;
	
	public BDIAgent(String TheName) {
		super(TheName);
	}
	public BDIAgent(String TheName,BlockWorld TheWorld) {
		super(TheName);
		this.world=TheWorld;
	}
	public void setSupervisor(Communication s){
		Sup=s;
		if(s!=null){
			HasSupervisor=true;
			s.receive(new Message(Message.Type.REGISTER,this.getName(),this,s));
		}
	}
	public JPanel graphics(){
		if(lbl_arm==null) lbl_arm = new JLabel(Block.imageOf(MyArm));
		else			lbl_arm.setIcon(Block.imageOf(MyArm));
		if(pnl_arm==null){
			pnl_arm = new JPanel(new FlowLayout());
			pnl_arm.add(lbl_arm);
		}
		return pnl_arm;
	}
	
	public Plan getPlan(){
		return p;
	}
	
	private void doStack(String blockName1, String blockName2){
		if(MyArm==0) System.out.println(this.getName()+"-ERROR[stack]: Arm empty!");
		MyArm = world.doStack(blockName1, blockName2);
	}
	private void doUnStack(String blockName1, String blockName2){
		if(MyArm!=0) System.out.println(this.getName()+"-ERROR[unstack]: Arm not empty!");
		MyArm = world.doUnStack(blockName1, blockName2);
	}
	private void doPickUp(String blockName){
		if(MyArm!=0) System.out.println(this.getName()+"-ERROR[pickup]: Arm not empty!");
		MyArm = world.doPickUp(blockName);
	}
	private void doPutDown(String blockName){
		if(MyArm==0) System.out.println(this.getName()+"-ERROR[putdown]: Arm empty!");
		MyArm = world.doPutDown(blockName);
	}
	
	
	@Override
	public int receive(Message query) {
		System.out.println(this.getName()+": Received: "+query.getText());
		if(query.getType()==Message.Type.ALLOWED){
			ActionAllowed=true;
			LastMessageAns=true;
			allowStep=true;
			return 1;
		}
		if(query.getType()==Message.Type.DENIED){
			ActionAllowed=false;
			LastMessageAns=true;
			allowStep=true;
			return 1;
		}
		if(query.getType()==Message.Type.EXECUTE){
			allowStep=true;
			return 1;
		}
		System.out.println("Communication problem! "+query.getText());
		return -1;
	}

	
	@Override
	protected void execution() {
		if(p==null){
			p = new Plan(this.getGoal(),this.world);
			mile=null;
			System.out.println(this.getName()+"-Plan:"+p.getPlan());
		}
		if(!p.feasibleIn(world)&&!HasSupervisor){
			p = new Plan(this.getGoal(),this.world);
			mile=null;
			System.out.println(this.getName()+"-RePlan:"+p.getPlan());
		}
		if(p.completed()){
			System.out.println(this.getName()+"-Goal achived! [sleeping]");
		}else{
			if(allowStep||freeRun){
				String action = p.getNextAction();
				if(world.isActionDoable(action)){
					if(!HasSupervisor) exe(action);
					else{
						LastMessageAns=false;
						Message m = new Message(Message.Type.REQUEST,action,this,Sup);
						Sup.receive(m);
						while(!LastMessageAns){
							this.sleep();
						}
						if(ActionAllowed) exe(action);
						else			p.postpone(action);
					}
				}else{
					if( (action!=null) && (!action.isEmpty()) &&
							(action.toLowerCase().indexOf("putdown")>=0) ){
						System.out.println(this.getName() + "ERROR: can't execute ["+action+"]");
					}
					/* if BDIAgent has to stack a block and action is not possible
					 * it will put the block down									*/
					if( (action!=null) && (!action.isEmpty()) && 
							(action.toLowerCase().indexOf("stack")>=0) ){
						action = "putdown("+Plan.parametersOf(action, 1)+")";
						if(!HasSupervisor) exe(action);
						else{
							LastMessageAns=false;
							Message m = new Message(Message.Type.REQUEST,action,this,Sup);
							Sup.receive(m);
							while(!LastMessageAns){
								this.sleep();
							}
							if(ActionAllowed) exe(action);
							else			p.postpone(action);
						}
					}else{
						if(mile==null) mile=action;
						else if(action.equalsIgnoreCase(mile)){
							if(HasSupervisor){
								LastMessageAns=false;
								Message m = new Message(Message.Type.REQUEST,"NOOP",this,Sup);
								Sup.receive(m);
							}
						}
					}
					p.postpone(action);
				}
			}
		}
	}
	protected void exe(String act){
		mile=null;
		String arg=act.trim().toLowerCase();
		if(arg.startsWith("stack(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			this.doStack(a, b);
		}else if(arg.startsWith("unstack(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			this.doUnStack(a, b);
		}else if(arg.startsWith("pickup(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			this.doPickUp(a);
		}else if(arg.startsWith("putdown(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			this.doPutDown(a);
		}
		if(HasSupervisor&&ActionAllowed) ActionAllowed=false;
		allowStep=false;
	}
	@Override
	public String getId() {
		return this.getName();
	}
}
