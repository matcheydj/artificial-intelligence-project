package ro.aligotec.ai.mas.comunication;

public interface Communication {
	
	public String getId();
	public int receive(Message query);
	
}