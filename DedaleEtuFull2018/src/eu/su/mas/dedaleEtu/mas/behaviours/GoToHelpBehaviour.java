package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import jade.core.behaviours.OneShotBehaviour;

public class GoToHelpBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3626764498881443831L;
	private AgentWrapper agent;
	private int exitCode = 0;
	
	public GoToHelpBehaviour(AgentWrapper agent) {
		this.agent=agent;
	}
	
	@Override
	public void action() {
		System.out.println(this.agent.getLocalName() + "   " + this.getClass().getCanonicalName());
		this.agent.updateLastBehaviour(12);
		boolean move = false;
		List<String> path = this.agent.getPathToHelp();
		
		if(path.isEmpty()) {
			exitCode = 1;
		}else {
			String nextNode = path.get(0);
			move = ((AbstractDedaleAgent)this.agent).moveTo(nextNode);
			if (move) {
				path.remove(nextNode);
				try {
					this.agent.doWait(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				exitCode = 2;
			}
		}

	}
	
	@Override
	public int onEnd() {
		return exitCode;
	}

}
