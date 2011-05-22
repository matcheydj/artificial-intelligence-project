package ro.aligotec.ai.mas.comunication;

public class Message {
	public enum Type {
		OK,
		CANCEL,
		YES,
		NO,
		ACK,
		EXECUTE,
		REGISTER,
		OFFER,
		INFO,
		REQUEST,
		REFUSE,
		ALLOWED,
		DENIED
	};
	
	private Type type;
	private int mID,rID;	//message ID and in-replay-to ID
	private String text;
	private Communication s,r;
	private static int gen = 1001; 
	
	public Message(){
		this(Type.INFO,"");
	}
	public Message(Type TheType){
		this(TheType,"");
	}
	public Message(Type TheType, String TheText){
		this(TheType,"",null,null);
	}
	public Message(Type TheType, String TheText,Communication sender,Communication receiver){
		this.type=TheType;
		this.text=TheText;
		s=sender;
		r=receiver;
		mID = gen++;
		rID = 0;
	}
	
	public int getID()		{return mID;}
	public Type getType()	{return type;}
	public String getText()	{return text;}
	public String toString(){
		return this.type+":"+this.text+" ";
	}
	public void setType(Type t)		{type=t;}
	public void setText(String t)	{text=t;}
	public void setReplyToMessageID(int id)	{rID=id;}
	public int getReplyToMessageID()		{return rID;}
	
	public Communication getSender(){return s;}
	public Communication getReceiver(){return r;}
	public void setSender(Communication sender){s=sender;}
	public void setReceiver(Communication receiver){r=receiver;}
	
	public void answerAck(){
		Message back = new Message(Type.ACK,null,r,s);
		back.setReplyToMessageID(this.rID);
		this.s.receive(back);
	}
}

