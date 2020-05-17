package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.behaviours.OneShotBehaviour;

public class ExploOldestBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3912044501329336080L;

	private AgentWrapper agent;
	private int exitCode = 0;
	
	public ExploOldestBehaviour(AgentWrapper agent) {
		this.agent = agent;
	}
	@Override
	public void action() {
		/**
		 * Just added here to let you see what the agent is doing, otherwise he will be too quick
		 */
		try {
			this.agent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
				this.agent.updateLastBehaviour(10);
		//0) Retrieve the current position
				String myPosition=((AbstractDedaleAgent)this.agent).getCurrentPosition();
				
				if (myPosition!=null){
					
					if(this.agent.getOpenNodes().isEmpty()) {
						this.agent.getClosedNodes().clear();
						this.agent.getOpenNodes().add(myPosition);
					}
					List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

					
					for (Couple<String,List<Couple<Observation,Integer>>> obj : lobs) {
						if (!obj.getRight().isEmpty()) {
							NodeData node = new NodeData( obj.getLeft(), obj.getRight());
							this.agent.addObservation(new Couple(obj.getLeft(), node));
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
					}
					//1) remove the current node from openlist and add it to closedNodes.
					this.agent.getClosedNodes().add(myPosition);
					this.agent.getOpenNodes().remove(myPosition);

					
					//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
					String nextNode=null;
					Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
					
					while(iter.hasNext()){
						Couple<String, List<Couple<Observation,Integer>>> obj = iter.next();
						
						String nodeId=obj.getLeft();
						if (!this.agent.getClosedNodes().contains(nodeId)){
							if (!this.agent.getOpenNodes().contains(nodeId)){
								this.agent.getOpenNodes().add(nodeId);	
								
							}
							if (nextNode==null) nextNode=nodeId;
						}
					}
					

					this.agent.getMyTreasures();
					//3) while openNodes is not empty, continues.
					if (this.agent.getOpenNodes().isEmpty() || !(this.agent.getMyTreasures().isEmpty())){
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
						move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);						
						if (! move) {
							exitCode = 2;
						}
						
						
					}
					

				}
				
			}

			@Override
			public int onEnd() {
				return exitCode;
				
				
			}


}
