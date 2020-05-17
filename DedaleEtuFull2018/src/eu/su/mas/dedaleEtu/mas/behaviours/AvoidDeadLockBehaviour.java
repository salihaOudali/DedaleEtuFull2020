package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.*;
import jade.core.behaviours.OneShotBehaviour;

public class AvoidDeadLockBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6034574051693132155L;

	private AgentWrapper agent;
	private int exitCode = 0;
	
	public AvoidDeadLockBehaviour(AgentWrapper agent) {
		this.agent = agent;
	}
	@Override
	public void action() {
		try {
			this.agent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		exitCode = this.agent.getLastBehaviour();
		
		String myPosition=((AbstractDedaleAgent)this.agent).getCurrentPosition();
		if(this.agent.getPriority() < this.agent.getDeadLockPriority() || (this.agent.getPriority() == this.agent.getDeadLockPriority() && this.agent.getDeadLockSender().compareTo(this.agent.getLocalName())< 0)) {

			List<String> pathToAvoid = this.agent.getPathToAvoid();
			if (myPosition!=null){
				
				//List of observable from the agent's current position
				String nextNode = null;
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs = ((AbstractDedaleAgent)this.agent).observe();
				for (Couple<String,List<Couple<Observation,Integer>>> obs : lobs){
					if (!obs.getLeft().equals(this.agent.getDeadLockPos()) && !obs.equals(lobs.get(0))) {
						nextNode = obs.getLeft();
						if (! pathToAvoid.contains(obs.getLeft())) {
							
							if (((AbstractDedaleAgent)this.agent).moveTo(nextNode)) {
								
								exitCode = 2;
								this.agent.resetDeadLock();
								if (this.agent instanceof TankerAgent) {
									this.agent.setPriority(1);
								}else {
									if(this.agent instanceof ExploreSoloAgent ) {
										this.agent.setPriority(2);
									}else {
										this.agent.setPriority(3);
									}
								}
								break;
							}
						}
					}
				}
				if (nextNode == null){
					this.agent.setPriority(this.agent.getPriority()*10);
					exitCode = 1;
				}else {
					boolean move = false;
					if(exitCode !=2) {
						for (Couple<String,List<Couple<Observation,Integer>>> obs : lobs){
							
							if (!obs.getLeft().equals(this.agent.getDeadLockPos()) && !obs.equals(lobs.get(0)) && this.agent.getPath().contains(obs.getLeft())) {
								nextNode = obs.getLeft();
								move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
								if (move) {
										break;
								}
								
							}
						}
						if(!move) {
							for (Couple<String,List<Couple<Observation,Integer>>> obs : lobs){
								
								if (!obs.getLeft().equals(this.agent.getDeadLockPos()) && !obs.equals(lobs.get(0)) && this.agent.getPath().contains(obs.getLeft())) {
									nextNode = obs.getLeft();
									move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
									if (move) {
											break;
									}
									
								}
							}
							if (! move) {
								this.agent.setPriority(this.agent.getPriority()*10);
								exitCode = 1;
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
