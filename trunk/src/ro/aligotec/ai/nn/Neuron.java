package ro.aligotec.ai.nn;

import java.util.Iterator;
import java.util.LinkedList;

public class Neuron {
	private long id = 0;
	private double value = 0;
	private double thresh = 0;
	
	private LinkedList<Synapse> incoming = new LinkedList<Synapse>();	
	private LinkedList<Synapse> outgoing = new LinkedList<Synapse>(); 
	
	
	private static long idGenerator = 1000000;
	
	/** Neurons pool */
	public static LinkedList<Neuron> neurons = new LinkedList<Neuron>();
	
	/*-----------------------------------------------------------------*/
	/** GET Neuron form Neurons pool */
	public static Neuron getById(long id){
		if(neurons.size()>0){
			Iterator<Neuron> it = neurons.iterator();
			while(it.hasNext()){
				Neuron n = it.next();
				if(n.getId() == id){
					return n;
				}
			}
		}
		return null;
	}
	public static void addNeuron(Neuron neuron){
		if(neurons.size()>0){
			Iterator<Neuron> it = neurons.iterator();
			while(it.hasNext()){
				Neuron cn = it.next();
				if(cn.equals(neuron)){
					return;
				}
			}
		}
		neurons.add(neuron);
	}
	public static void removeNeuron(Neuron neuron){
		if(neurons.size()>0){
			Iterator<Neuron> it = neurons.iterator();
			while(it.hasNext()){
				if(it.next().equals(neuron)){
					it.remove();
					return;
				}
			}
		}
	}
	/*-----------------------------------------------------------------*/
	/** Constructor */
	public Neuron(){
		id = idGenerator++;
	}
	
	public long getId(){
		return this.id;
	}
	public void setId(long id){
		this.id = id;
	}
	
	public double getValue(){
		return this.value;
	}
	public void setValue(double value){
		this.value = value;
	}
	
	public double getThreshold(){
		return this.thresh;
	}
	public void setThreshold(double threshold){
		this.thresh = threshold;
	}
	
	public LinkedList<Synapse> getIncoming() {
		return incoming;
	}
	public void setIncoming(LinkedList<Synapse> incoming) {
		this.incoming = incoming;
	}
	public void addIncoming(Synapse synapse){
		this.incoming.add(synapse);
	}
	public LinkedList<Synapse> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(LinkedList<Synapse> outgoing) {
		this.outgoing = outgoing;
	}
	public void addOutgoing(Synapse synapse){
		this.outgoing.add(synapse);
	}
	
	
	
	public boolean equals(Neuron neuron){
		return (this.id == neuron.getId());
	}
}
