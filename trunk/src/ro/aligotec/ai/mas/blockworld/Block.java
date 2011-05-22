package ro.aligotec.ai.mas.blockworld;

import javax.swing.ImageIcon;

public class Block {

	private static int nAlph=27;
	private static String[] Alph = {"_","A","B","C","D","E","F","G","H","I","J",
							"K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private static ImageIcon[] icos=null;
	
	
	private String Name="";
	private int Id=0;
		
	public Block(int id){
		Id=id;Name = nameById(id);
		if(icos==null) Block.loadImageIcons();
	}
	//_________________PUBLIC_MEMBERS_________________________________	
	public String name()		{return Name;}
	public int id()				{return Id;}
	public ImageIcon getImage()	{return icos[Id];}
	
	
	//_________________PUBLIC_STATIC__________________________________
	public static void loadImageIcons(){
		java.net.URL imageURL;
		try{icos = new ImageIcon[nAlph];
			for(int i=0;i<nAlph;i++){
				imageURL = Block.class.getResource("/ro/aligotec/res/block_"+i+".gif");
				if (imageURL != null)	icos[i] = new ImageIcon(imageURL,"Block"+i);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	public static int idByName(String name){
		if(name==null)	return 0;
		if(name.equalsIgnoreCase("null"))	return 0;
		name=name.trim().toLowerCase();
		for(int i=0;i<nAlph;i++)
			if(name.equalsIgnoreCase(Alph[i].toLowerCase())) return i;
		System.out.println("Block-idByName-not-found: "+name);
		return -1;
	}
	public static String nameById(int id){
		if(id==0||id>nAlph-1) return null;
		return Alph[id];
	}
	public static ImageIcon imageOf(int i){
		if(icos==null) loadImageIcons();
		if(i<0||i>=icos.length) return null;
		return icos[i];
	}
	public static int length(){
		return nAlph-1;
	}
}
