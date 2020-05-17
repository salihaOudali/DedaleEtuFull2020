package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.graphstream.graph.EdgeRejectedException;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgentWrapper extends AbstractDedaleAgent  {
	private static final long serialVersionUID = -1784844593772918359L;
	
	
	private MapRepresentation map = new MapRepresentation();
	private List<String> openNodes = new ArrayList<String>();
	private List<String> closedNodes = new ArrayList<String>();
//	private List<Couple<String,List<Couple<Observation,Integer>>>> observations = new ArrayList<Couple<String,List<Couple<Observation,Integer>>>>();
	private List<Couple<String, String>> edges = new ArrayList<Couple<String, String>>();
	private List<NodeData> nodes = new ArrayList<NodeData>();
	private List<String> hist = new ArrayList<String>();
	private List<String> agents;
	private List<String> pathToTreasure = new ArrayList<String>();
	private List<String> pathToHelp = new ArrayList<String>();
	private List<String> pathToAvoid = new ArrayList<String>();
	private List<String> path = new ArrayList<String>();
	private String deadLockPos = null, deadLockSender = null;
	private HashMap<String, NodeData> knowledge;
	private HashMap<String, NodeData> myTreasures = new HashMap<String, NodeData>();
	private int priority = 0, deadLockPriority = 0, lastBehaviour = 0;
	
	public MapRepresentation getMap() {
		return map;
	}
	

	
	public List<String> getClosedNodes(){
		
		return closedNodes;
	}
	

	
	public void addObservation(Couple<String,NodeData> obj) {
		String id = obj.getLeft();
		if(knowledge.containsKey(id)) {
			
				if (knowledge.get(id).getLastUpdate() < obj.getRight().getLastUpdate()) {
					knowledge.put(id, obj.getRight());
				}
		}else {
			knowledge.put(id, obj.getRight());
		}
	}
	
	
	public List<String> getPath(){
		return path;
	}
	
	public List<String> getPathToAvoid(){
		return pathToAvoid;
	}
	
	public void setPathToAvoid( List<String> path){
		this.pathToAvoid = path;
	}
	public List<String> getShortestPathMap(String from, String to) {
		return map.getShortestPath(from, to);
	}
	
	public void addNode(String id, long time, List<Couple<Observation, Integer>> ob) {
		nodes.add(new NodeData(id, time, ob));
	}
	
	public List<NodeData> getNodes(){
		return nodes;
	}
	
	public void addNodeToMap(String node) {
		map.addNode(node);
	}
	public void addNodeToMap(String node, MapAttribute att) {
		map.addNode(node, att);
	}	
	
	public void addEdgeToMap(String pos, String id) {
		try {
			map.addEdge(pos, id);
			this.edges.add(new Couple<String, String>(pos, id));
		}catch(Exception e) {
			//Do not add an existing edge
		}
		
		
	}
	
	
	public List<Couple<String, String>> getEdges(){	
		return edges;
	}
	
	
	public HashMap<String, NodeData> getObservationsData(){
		return knowledge;
	}
	
	public HashMap<String, NodeData> getMyTreasures(){
		this.myTreasures= new HashMap<String, NodeData>();
		this.updateMyTreasures(); 
		return myTreasures;
	}
	
	public void updateMyTreasures() {
		this.myTreasures= new HashMap<String, NodeData>();
		for (String key : knowledge.keySet()) {
			NodeData node = knowledge.get(key);
			for(Couple<Observation, Integer> c : node.getAttributes()) {
				if (c.getLeft().getName().equals(this.getMyTreasureType().getName())){
					this.myTreasures.put(key, node);
					
				}
			}
		}
	}
	
	public void removeTreasure(String treasure) {
		this.myTreasures.remove(treasure);
	}
	
	public List<String> getPathToClosestTreasure() {
		int max = Integer.MAX_VALUE;
		List<String> path;
		for(String node: myTreasures.keySet()) {
			path = this.getShortestPathMap(getCurrentPosition(), node);
			if(path.size() < max) {
				max = path.size();
				this.pathToTreasure = path;
				this.path = path;
			}
			
		}
		
		return pathToTreasure;
	}
	
	
	protected void setup() {
		super.setup();
		knowledge = new HashMap<>();
	}

	
	public List<String> getOpenNodes(){

		return openNodes;
	}
	
	public List<String> getHist(){
		return hist;
	}
	
	public void addHist(String h) {
		this.hist.add(h);
	}
	
	public List<String> setPathToHelp(String node){
		this.pathToHelp = map.getShortestPath(this.getCurrentPosition(), node);
		this.path = this.pathToHelp;
		return this.pathToHelp;
	}
	
	public void removeNodeFromPathToHelp(String node) {
		this.pathToHelp.remove(node);
		this.path.remove(node);
	}
	
	public List<String> getPathToHelp(){
		return this.pathToHelp;
	}
	
	public List<String> getPathToTreasure(){
		return this.getPathToClosestTreasure();
	}
	
	public void setPathToTreasure( List<String> p){
		this.path = p;
		this.pathToTreasure=p;
	}
	
	public void clearPathToTreasure( ){
		this.path.clear();
		this.pathToTreasure.clear();
	}
	
	public void removeNodeFromPathToTreasure(String node ){
		this.pathToTreasure.remove(node);
		this.path.remove(node);
	}
	
	public List<String> getAgents() {
		return agents;
	}
	
	public void setAgents(List<String> a) {
		this.agents = a;
	}
	
	public void setPriority(int p) {
		this.priority = p;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public void setDeadLockPriority(int p) {
		this.deadLockPriority = p;
	}
	
	public int getDeadLockPriority() {
		return this.deadLockPriority;
	}
	
	public void setDeadLockPos(String p) {
		this.deadLockPos = p;
	}
	
	public String getDeadLockPos() {
		return this.deadLockPos;
	}
	
	public void setDeadLockSender(String p) {
		this.deadLockSender = p;
	}
	
	public String getDeadLockSender() {
		return this.deadLockSender;
	}
	
	public void resetDeadLock() {
		this.deadLockSender = null;
		this.deadLockPos = null;
		this.pathToAvoid.clear();
	
		
	}
	
	public int getLastBehaviour() {
		return lastBehaviour;
	}
	
	public void updateLastBehaviour(int b) {
		this.lastBehaviour = b;
	}


}