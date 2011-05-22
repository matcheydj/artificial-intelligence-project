package ro.aligotec.ai.nn;

import java.util.Iterator;
import java.util.LinkedList;

public class Synapse {
	private long id = 0;
	private double weight = 0;
	private Neuron source = null;
	private Neuron target = null;
	private int layer = 0;
	
	private static long idGenerator = 10000000;
	
	/** Synapses pool */
	public static LinkedList<Synapse> synapses = new LinkedList<Synapse>();
	
	/*-----------------------------------------------------------------*/
	/** GET Synapse from Synapses pool */
	public static Synapse getById(long id){
		if(synapses.size()>0){
			Iterator<Synapse> it = synapses.iterator();
			while(it.hasNext()){
				Synapse s = it.next();
				if(s.getId() == id){
					return s;
				}
			}
		}
		return null;
	}
	/** ADD Synapses in pool */
	public static void addSynapse(Synapse synapse){
		if(synapses.size()>0){
			Iterator<Synapse> it = synapses.iterator();
			while(it.hasNext()){
				Synapse cs = it.next();
				if(cs.equals(synapse)){
					return;
				}
			}
		}
		synapses.add(synapse);
	}
	/** REMOVE Synapses from pool */
	public static void removeSynapse(Synapse synapse){
		if(synapses.size()>0){
			Iterator<Synapse> it = synapses.iterator();
			while(it.hasNext()){
				if(it.next().equals(synapse)){
					it.remove();
					return;
				}
			}
		}
	}
	/*-----------------------------------------------------------------*/
	/** Constructor */
	private Synapse(){
		id = idGenerator++;
		synapses.add(this);
	}
	/** Constructor */
	public Synapse(Neuron source, Neuron target){
		this(source, target, 0);
	}
	/** Constructor */
	public Synapse(Neuron source, Neuron target, double weight){
		this();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	public void fire(){
		target.setValue(target.getValue() + source.getValue() * this.weight);
	}
	
	public long getId(){
		return this.id;
	}
	public void setId(long id){
		this.id = id;
	}
	
	public double getWeight() {
		return this.weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public Neuron getSource() {
		return this.source;
	}
	public void setSource(Neuron source) {
		this.source = source;
	}
	public Neuron getTarget() {
		return this.target;
	}
	public void setTarget(Neuron target) {
		this.target = target;
	}
	public void setLayer(int layer){
		this.layer = layer;
	}
	public int getLayer(){
		return this.layer;
	}
	
	public boolean equals(Synapse synapse){
		return (this.id == synapse.getId());
	}
	
}
