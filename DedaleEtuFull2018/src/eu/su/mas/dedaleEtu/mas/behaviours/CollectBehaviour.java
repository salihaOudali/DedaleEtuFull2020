package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.behaviours.OneShotBehaviour;

public class CollectBehaviour extends OneShotBehaviour{

	private AgentWrapper agent;
	private int exitCode =0;
	public CollectBehaviour(final AgentWrapper agent) {
		this.agent = agent;
	}
	@Override
	public void action() {
		//Example to retrieve the current position
		this.agent.updateLastBehaviour(10);
			try {
				this.agent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		        exitCode =0;
				String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
				int pick;
				if (myPosition!=null){
					this.agent.removeNodeFromPathToTreasure(myPosition );
					if (this.agent.getBackPackFreeSpace()<=0) {
						exitCode = 1; //The agent will go to the tanker in order to empty its backpack
					}else {
						//List of observable from the agent's current position
						List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
											
						//list of observations associated to the currentPosition
						List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();
						Observation myTreasureType = ((AbstractDedaleAgent) this.myAgent).getMyTreasureType();
						//example related to the use of the backpack for the treasure hunt
						
						for(Couple<Observation,Integer> o:lObservations){
								//if I find a treasure, either I pick it (if I can) and update knowledge or I ask for help
								if(o.getLeft().getName().equals(myTreasureType.getName())) {
									if (((AbstractDedaleAgent) this.myAgent).openLock(myTreasureType)) {
										pick = ((AbstractDedaleAgent) this.myAgent).pick();
										// Update my knowledge and send it to other agents
										exitCode = 2;
									}
									else {
										//Ask for help
										exitCode = 3;
										try {
											this.agent.doWait(1000);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
								
						
						}
	
						//If the agent picked (part of) the treasure
						if (exitCode == 2){
							List<Couple<String,List<Couple<Observation,Integer>>>> lobs2=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
							
							for(Couple<String,List<Couple<Observation,Integer>>> obj : lobs2) {
								boolean notEmpty = false;
								
								if (!obj.getRight().isEmpty()) {
									for (Couple<Observation,Integer> couple : obj.getRight()) {
										if (couple.getLeft().equals(this.agent.getMyTreasureType())) {
											notEmpty = true;
											break;
										}
									}
									NodeData node = new NodeData( obj.getLeft(), obj.getRight());
									this.agent.addObservation(new Couple(obj.getLeft(), node));
									
								this.agent.updateMyTreasures();
								}
							}
						}else {
							if(exitCode == 0) { //No treasure in my node
								
								List<String > path = this.agent.getPathToTreasure();
								boolean move = false;
								if(!path.isEmpty()) {
										String nextNode=path.get(0);
										move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
									
								}else {
									this.agent.setPathToTreasure(this.agent.getPathToClosestTreasure());
									
									if(!this.agent.getPathToTreasure().isEmpty()) {
										String nextNode=this.agent.getShortestPathMap(myPosition, this.agent.getPathToTreasure().get(0)).get(0);
										 move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
										
									}
								}
								if (! move) {
									exitCode = 5;
									if(this.agent.getMyTreasures().isEmpty()) { // If there is no treasure to visit, the agent has to explore the environment 
										exitCode = 4; // The agent will explore the oldest nodes which are more likely to be affected by the activity of the wumpus
									}
								}
								
								
							} 
						}
						
					
	
					}
				}

			}
	
	
	@Override
	public int onEnd() {
		return exitCode;
		
		
	}
		
}


