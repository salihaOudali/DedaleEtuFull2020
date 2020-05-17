package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.agents.dummies.TankerAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;

public class GoToTankerBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8567689731496787661L;
	private int exitCode = 0;
	private AgentWrapper agent;
	private MapRepresentation map;
	
	public GoToTankerBehaviour(AgentWrapper agent) {
		this.agent = agent;
		map = agent.getMap();
	}
	
	@Override
	public void action() {
		this.agent.updateLastBehaviour(11);
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		try {
			this.agent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (myPosition!=null){
			
			String tankerPos = map.getTankerPosition();
			try {
				this.agent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (this.agent instanceof TankerAgent) {
				if (myPosition.equals(tankerPos)) {
					exitCode = 1;
					
				}else {
					String nextNode=this.agent.getShortestPathMap(myPosition, tankerPos).get(0);
					if(!((AbstractDedaleAgent)this.agent).moveTo(nextNode)) {
						exitCode = 2;
					}
					
				}
			}
			else {
				if (this.agent.emptyMyBackPack("Tanker1")) {
					exitCode = 1;
				}else {
					List<String> path = this.agent.getShortestPathMap(myPosition, tankerPos);
					if (! path.isEmpty()) {
						String nextNode=path.get(0);
						
						if(!((AbstractDedaleAgent)this.agent).moveTo(nextNode)) {
							exitCode = 2;
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
