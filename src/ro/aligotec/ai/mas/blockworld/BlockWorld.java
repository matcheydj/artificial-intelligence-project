package ro.aligotec.ai.mas.blockworld;

import java.awt.GridLayout;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlockWorld {

	private int[][] plane=null;
	private int dim=0,blk=0;
	private JPanel graph=null;
	private JLabel[][] blks=null;

	
	//__________PUBLIC_CONSTRUCTORS______________________________________
	public BlockWorld(int Dimension){
		dim = Dimension;
		plane = new int[dim][dim];
		iniBlankWorld();
	}
	
	public BlockWorld(int Dimension, int NoOfBlocks){
		dim = Dimension;
		blk = NoOfBlocks;
		plane = new int[dim][dim];
		iniRandomWorld();
	}
	//__________PRIVATE_INITIALIZATORS____________________________________
	private void iniBlankWorld(){
		for(int i=0;i<dim;i++)
			for(int j=0;j<dim;j++)
				plane[i][j]=0;
	}
	private void iniRandomWorld(){
		iniBlankWorld();
		Random gen = new Random();
		for(int i=1;i<=blk;i++){
			int r = gen.nextInt(dim);
			for(int j=dim-1;j>=0;j--)
				if(plane[j][r]==0){plane[j][r]=i;break;}
		}
	}
	private void iniGraphics(){
		graph = new JPanel(new GridLayout(dim,dim));
		blks  = new JLabel[dim][dim];
		for(int i=0;i<dim;i++)
			for(int j=0;j<dim;j++){
				blks[i][j] = new JLabel(Block.imageOf(this.plane[i][j]));
				graph.add(blks[i][j]);
			}
	}
	private void updateGraphics(){
		for(int i=0;i<dim;i++)
			for(int j=0;j<dim;j++)
				blks[i][j].setIcon(Block.imageOf(this.plane[i][j]));
	}
	
	//__________PUBLIC_FUNCTIONS____________________________________________	
	public BlockWorld copy(){
		BlockWorld bo = new BlockWorld(this.dim);
		for(int i=0;i<this.dim;i++)
			for(int j=0;j<this.dim;j++)
				bo.plane[i][j]=this.plane[i][j];
		return bo;
	}
	public boolean equals(BlockWorld world){
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(world.plane[i][j]!=this.plane[i][j]) return false;
		return true;
	}
	public String toString(){
		StringBuffer me = new StringBuffer("");
		for(int i=0;i<this.dim;i++){
			for(int j=0;j<this.dim;j++)
				me.append(Block.nameById(this.plane[i][j])+"\t");
			me.append("\n");
		}
		return me.toString();
	}
	public int noOfblocks()	{return blk;}
	public int dimension()	{return dim;}
	public JPanel graphics(){
		if(graph==null) iniGraphics();
		else			updateGraphics();
		return graph;
	}
	public int getBlockOnTopOf(int blockId){
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(this.plane[i][j]==blockId)
					if(i==0) return 0;
					else	 return this.plane[i-1][j];
		return 0;
	}
	public String getBlockOnTopOf(String blockName){
		return Block.nameById(getBlockOnTopOf(Block.idByName(blockName)));
	}
	public int getBlockBelow(int blockId){
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(this.plane[i][j]==blockId)
					if(i==this.dim-1) return 0;
					else	 return this.plane[i+1][j];
		return 0;
	}
	public String getBlockBelow(String blockName){
		return Block.nameById(getBlockBelow(Block.idByName(blockName)));
	}
	
	//___________PUBLIC_STATE_____________________________________________________
	public boolean isMissingBlock(int blockId){
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(this.plane[i][j]==blockId) return false;
		return true;
	}
	public boolean isMissingBlock(String blockName){
		return isMissingBlock(Block.idByName(blockName));
	}
	
	public boolean isStateTrue(String state){
		String arg=state.trim().toLowerCase();
		if(arg.startsWith("ontable")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			return this.isOnTable(a);
		}else if(arg.startsWith("on(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			return this.isOn(a, b);
		}else if(arg.startsWith("clear(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			return this.isClear(a);
		}
		return false;
	}
	
	public boolean isActionDoable(String state){
		if(state==null) return false;
		String arg=state.trim().toLowerCase();
		if(arg.startsWith("stack(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			return this.isClear(b)&&this.isMissingBlock(a);
		}else if(arg.startsWith("unstack(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			return this.isOn(a, b)&&this.isClear(a);
		}else if(arg.startsWith("pickup(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			return this.isClear(a)&&this.isOnTable(a);
		}else if(arg.startsWith("putdown(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			return this.isMissingBlock(a);
		}
		return false;
	}
	//__________PUBLIC_EXECUTE____________________________________________________
	private LinkedList<String> doActionsToFreeBlock(String blockName){
		LinkedList<String> acts = new LinkedList<String>();
		if(this.isMissingBlock(blockName)) return null;
		else if(this.isClear(blockName)) return null;
		else{
			String topB = this.getBlockOnTopOf(blockName);
			if(topB==null){
				System.out.println("WORLD-ERROR: isClear "+ blockName+ " failed!");
				throw new NullPointerException();
			}else{
				acts.add(("UNSTACK("+topB+","+blockName+")").toLowerCase());
				this.doUnStack(topB, blockName);
			}
				
			acts.add(("PUTDOWN("+topB+")").toLowerCase());
			acts = concatenateListsRev(acts, doActionsToFreeBlock(topB));
			this.doPutDown(topB);
		}
		
		return acts;
	}
	private LinkedList<String> concatenateLists(LinkedList<String> l1, LinkedList<String> l2){
		if(l2==null) return l1;
		Iterator<String> it = l2.iterator();
		while(it.hasNext())
			l1.add(it.next());
		return l1;
	}
	private LinkedList<String> concatenateListsRev(LinkedList<String> l1, LinkedList<String> l2){
		if(l2==null) return l1;
		Iterator<String> it = l1.iterator();
		while(it.hasNext())
			l2.add(it.next());
		return l2;
	}
	public LinkedList<String> doActionsToRichState(String state){
		//System.out.println("WORLD-ReachState: "+state);
		LinkedList<String> acts = new LinkedList<String>();
		String arg = state.trim().toLowerCase();
		if(arg.startsWith("ontable")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			acts = concatenateLists(acts, doActionsToFreeBlock(a));
			String botB = this.getBlockBelow(a);
			acts.add(("UNSTACK("+a+","+botB+")").toLowerCase());
			acts.add(("PUTDOWN("+a+")").toLowerCase());
			this.doUnStack(a, botB);
			this.doPutDown(a);
		}else if(arg.startsWith("on(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			acts = concatenateLists(acts, doActionsToFreeBlock(a));
			acts = concatenateLists(acts, doActionsToFreeBlock(b));
			String botB = this.getBlockBelow(a);
			if(botB==null){
				acts.add(("PICKUP("+a+")").toLowerCase());
				this.doPickUp(a);
			}
			else{
				acts.add(("UNSTACK("+a+","+botB+")").toLowerCase());
				this.doUnStack(a, botB);
			}
			acts.add(("STACK("+a+","+b+")").toLowerCase());
			this.doStack(a, b);
		}else if(arg.startsWith("clear")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			acts = concatenateLists(acts, doActionsToFreeBlock(a));
		}else{
			System.out.println("ERROR: processing state: "+ arg +" failed!");
		}
		return acts;
	}
	
	public int doStack(int blockId1, int blockId2){
		if(isClear(blockId2)){
			for(int i=this.dim-1;i>=0;i--)
				for(int j=0;j<dim;j++)
					if(plane[i][j]==blockId2){
						if(i==0){System.out.println("ERROR: World dimension too small ["+dim+
								" rows]! b1:"+blockId1+" b2:"+blockId2);plane[-1][j]=0;}
						plane[i-1][j]=blockId1; return 0;
					}
		}
		System.out.println("WORLD: doStack("+blockId1+","+blockId2+") failed!");
		return -1;
	}
	public int doStack(String blockName1, String blockName2){
		return doStack(Block.idByName(blockName1),Block.idByName(blockName2));
	}
	public int doUnStack(int blockId1, int blockId2){
		if(isClear(blockId1)&&isOn(blockId1,blockId2)){
			for(int i=this.dim-1;i>=0;i--)
				for(int j=0;j<dim;j++)
					if(plane[i][j]==blockId2){
						if(i==0){System.out.println("ERROR: World dimension too small ["+dim+
								" rows]! b1:"+blockId1+" b2:"+blockId2);plane[-1][j]=0;}
						if(plane[i-1][j]==blockId1){
							plane[i-1][j]=0; return blockId1;
						}
						else return -1;
					}
		}
		System.out.println("WORLD: doUnStack("+blockId1+","+blockId2+") failed!");
		return -1;
	}
	public int doUnStack(String blockName1, String blockName2){
		return doUnStack(Block.idByName(blockName1),Block.idByName(blockName2));
	}
	public int doPickUp(int blockId){
		if(isClear(blockId)&&isOnTable(blockId)){
			//System.out.println("WORLD: doPickUp("+blockId+")");
			for(int i=this.dim-1;i>=0;i--)
				for(int j=0;j<dim;j++)
					if(plane[i][j]==blockId){
						plane[i][j]=0; return blockId;
					}
		}
		System.out.println("WORLD: doPickUp("+blockId+") failed!");
		return -1;
	}
	public int doPickUp(String blockName){
		return doPickUp(Block.idByName(blockName));
	}
	public int doPutDown(int blockId){
		if(isMissingBlock(blockId)){
			for(int i=this.dim-1;i>=0;i--)
				if(plane[dim-1][i]==0){
					plane[dim-1][i]=blockId; return 0;
				}
			System.out.println("ERROR: World dimension too small ["+dim+" rows]! doPutDown:"+blockId+" ");
		}
		System.out.println("WORLD: doPutDown("+blockId+") failed!");
		return -1;
	}
	public int doPutDown(String blockName){
		return doPutDown(Block.idByName(blockName));
	}
	
	public String doProcessQuery(String query){
		String arg = query.trim().toLowerCase();
		if(arg.startsWith("stack")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			if(this.doStack(a, b)<0) return arg.toUpperCase();
		}else if(arg.startsWith("unstack")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			if(this.doUnStack(a, b)<0) return arg.toUpperCase();
		}else if(arg.startsWith("pickup")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			if(this.doPickUp(a)<0) return arg.toUpperCase();
		}else if(arg.startsWith("putdown")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			if(this.doPutDown(a)<0) return arg.toUpperCase();
		}else if(arg.startsWith("ontable")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			if(this.isOnTable(a))	return "true";
			else					return "false";
		}else if(arg.startsWith("on(")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(",")).trim();
			String b = arg.substring(arg.indexOf(",")+1, arg.indexOf(")")).trim();
			if(this.isOn(a, b)) 	return "true";
			else					return "false";
		}else if(arg.startsWith("clear")){
			String a = arg.substring(arg.indexOf("(")+1, arg.indexOf(")")).trim();
			if(this.isClear(a))		return "true";
			else					return "false";
		}
		
		System.out.println("ERROR: processing command: "+ arg +" failed!");
		return null;
	}
	
	//____________PRIVATE_STATE_________________________________________________
	private boolean isOnTable(int blockId){
		for(int i=0;i<this.dim;i++)
			if(plane[dim-1][i]==blockId) return true;
		return false;
	}
	private boolean isOnTable(String blockName){
		return isOnTable(Block.idByName(blockName));
	}
	private boolean isOn(int topBlock,int botBlock){
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(plane[i][j]==botBlock)
					if(i==0) return false;
					else 	if(plane[i-1][j]==topBlock) return true;
							else return false;
		return false;
	}
	private boolean isOn(String nameTopBlock,String nameBotBlock){
		return isOn(Block.idByName(nameTopBlock),Block.idByName(nameBotBlock));
	}
	private boolean isClear(int blockId){
		boolean r=false;
		for(int i=this.dim-1;i>=0;i--)
			for(int j=0;j<this.dim;j++)
				if(plane[i][j]==blockId)
					if(i==0) return true;
					else 	if(plane[i-1][j]==0) return true;
							else return false;
		//System.out.println("WORLD-Clear:"+blockId+"="+r);
		return r;
	}
	private boolean isClear(String blockName){
		boolean r = isClear(Block.idByName(blockName));
		//System.out.println("WORLD-Clear:"+blockName+"="+r);
		return r;
	}
	
	
	public static void main(String[] args) {
		BlockWorld me = new BlockWorld(10,20);
		System.out.println(me.toString());
		System.out.println("Q1\t=> ON(M,A):"+me.doProcessQuery("ON(M,A)"));
		System.out.println("Q2\t=> ONTABLE(A):"+me.doProcessQuery("ONTABLE(A)"));
		System.out.println("Q3\t=> ONTABLE(B):"+me.doProcessQuery("ONTABLE(B)"));
		System.out.println("Q4\t=> CLEAR(A):"+me.doProcessQuery("CLEAR(A)"));
		System.out.println("Q5\t=> CLEAR(O):"+me.doProcessQuery("CLEAR(O)"));
		System.out.println("Q6\t=> ONTABLE(J):"+me.doProcessQuery("ONTABLE(J)"));
		
		System.out.println("Q7\t=> PICKUP(J):"+me.doProcessQuery("PICKUP(J)"));
		System.out.println("Q8\t=> PICKUP(B):"+me.doProcessQuery("PICKUP(B)"));
		System.out.println("Q9\t=> PICKUP(D):"+me.doProcessQuery("PICKUP(D)"));
		System.out.println("Q10\t=> STACK(Z,A):"+me.doProcessQuery("STACK(Z,A)"));
		System.out.println("Q11\t=> STACK(Y,L):"+me.doProcessQuery("STACK(Y,L)"));
		
		System.out.println("\n"+me.toString());
		
		JFrame f = new JFrame("World");
		f.setSize(350, 400);
		f.add(me.graphics());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

}
