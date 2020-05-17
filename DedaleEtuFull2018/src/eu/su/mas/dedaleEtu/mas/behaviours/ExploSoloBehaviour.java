package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.behaviours.OneShotBehaviour;


/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class ExploSoloBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;
	private AgentWrapper agent;
	private int exitCode = 0;
	public ExploSoloBehaviour(final AgentWrapper myagent) {
		this.agent = myagent;
	}

	@Override
	public synchronized void action() {

		
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.agent).getCurrentPosition();
	
		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.agent.doWait(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Couple<String,List<Couple<Observation,Integer>>> obj : lobs) {
				if (!obj.getRight().isEmpty()) {
					NodeData node = new NodeData( obj.getLeft(), obj.getRight());
					this.agent.addObservation(new Couple(obj.getLeft(), node));
				}
			}
			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
			for (Couple<Observation, Integer> c : lObservations) {
				if (c.getLeft().equals(Observation.GOLD)) {
					((AbstractDedaleAgent) this.myAgent).openLock(Observation.GOLD);
				}
				if (c.getLeft().equals(Observation.DIAMOND)) {
					((AbstractDedaleAgent) this.myAgent).openLock(Observation.DIAMOND);
				}
				if (c.getLeft().equals(Observation.ANY_TREASURE)) {
					((AbstractDedaleAgent) this.myAgent).openLock(Observation.ANY_TREASURE);
				}
			}
			//1) remove the current node from openlist and add it to closedNodes.
			this.agent.getClosedNodes().add(myPosition);
			this.agent.getOpenNodes().remove(myPosition);

			this.agent.addNodeToMap(myPosition);
			
			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			
			while(iter.hasNext()){
				Couple<String, List<Couple<Observation,Integer>>> obj = iter.next();
				
				String nodeId=obj.getLeft();
				if (!this.agent.getClosedNodes().contains(nodeId)){
					if (!this.agent.getOpenNodes().contains(nodeId)){
						this.agent.getOpenNodes().add(nodeId);
						this.agent.addNodeToMap(nodeId, MapAttribute.open);
						this.agent.addEdgeToMap(myPosition, nodeId);	
						
					}else{
						//the node exist, but not necessarily the edge
						this.agent.addEdgeToMap(myPosition, nodeId);
					}
					if (nextNode==null) nextNode=nodeId;
				}
			}
			

			this.agent.getMyTreasures();
			//3) while openNodes is not empty, continues.
			if (this.agent.getOpenNodes().isEmpty()){
				//Explo finished
				exitCode = 1;
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					List<String> l = new ArrayList<String>(this.agent.getOpenNodes());
					nextNode=this.agent.getShortestPathMap(myPosition, l.get(0)).get(0);
					
				}
				boolean move = false;
				int cpt = 10;
				while (! move) {
					move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
					Random rand = new Random();
					nextNode = lobs.get(rand.nextInt(lobs.size())).getLeft();
					if (cpt == 0) {
						break;
					}
					cpt--;
				
				}
				
			}
			

		}
		
	}

	@Override
	public int onEnd() {
		return exitCode;
		
		
	}

}