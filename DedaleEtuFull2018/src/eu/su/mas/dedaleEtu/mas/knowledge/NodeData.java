package eu.su.mas.dedaleEtu.mas.knowledge;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import jade.util.leap.Serializable;

public class NodeData implements Serializable {
	
	private List<Couple<Observation, Integer>> attributes;
	private long lastUpdate = 0;
	public String nodeId;
	public List<String> neighbours;
	private static final long serialVersionUID = -1333959882640838272L;

	public NodeData(String id, long date) {
		this.nodeId = id;
		this.lastUpdate = date;
	}
	
	public NodeData(String id, List<Couple<Observation, Integer>> attr ){
		nodeId = id;
		attributes = attr;
		lastUpdate=System.currentTimeMillis();
		
	}
	
	public NodeData(String id, String neighbour) {
		nodeId = id;
//		neighbours.add(neighbour);
		
	}
	
	public NodeData(String id, String neighbour, List<Couple<Observation, Integer>> attr) {
		attributes = attr;
		nodeId = id;
//		neighbours.add(neighbour);
		lastUpdate = System.currentTimeMillis();
		
	}
	
	public NodeData(String id, String neighbour, long time, List<Couple<Observation, Integer>> attr) {
		attributes = attr;
		nodeId = id;
//		neighbours.add(neighbour);
		lastUpdate = time;
		
	}
	
	public NodeData(String id, long time, List<Couple<Observation, Integer>> attr) {
		attributes = attr;
		nodeId = id;
		lastUpdate = time;
		
	}
	
	public NodeData(String id, List<String> neighbours, long time, List<Couple<Observation, Integer>> attr) {
		attributes = attr;
		nodeId = id;
//		this.neighbours= neighbours;
		lastUpdate = time;
		
	}
	
	public void updateNode(List<Couple<Observation, Integer>> attr) {
		attributes = attr;
		lastUpdate = System.currentTimeMillis();
		
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public void updateNode(long time) {
		this.lastUpdate = time;
	}
	
	public List<String> getNeighbourhood(){
		return neighbours;
	}
	
	public List<Couple<Observation, Integer>> getAttributes() {
		return attributes;
	}
}