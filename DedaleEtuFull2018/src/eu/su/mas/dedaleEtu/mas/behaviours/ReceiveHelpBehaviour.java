package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveHelpBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;
	private AgentWrapper receiverAgent;
	private int exitCode =0;
	public ReceiveHelpBehaviour(AgentWrapper agent) {
		receiverAgent = agent;
		
	}

	@Override
	public void action() {
		final MessageTemplate template= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("HelpProtocol"));
		final ACLMessage msg = receiverAgent.receive(template);

		if (msg!=null){
			// Processing of the message
			try {

				Object knowledge =  msg.getContentObject();
					
					List knowledgeList = (List) knowledge;
					HashMap<String, NodeData> obs = (HashMap<String, NodeData>) knowledgeList.get(0);
					String myPosition = (String) knowledgeList.get(1);
					Integer priority = (Integer) knowledgeList.get(2);
					if (this.receiverAgent.getPriority() > priority) {
						exitCode = 1;
					}
					else {
						
					}

					for ( String c: obs.keySet()) {
						this.receiverAgent.addObservation(new Couple(c, obs.get(c)));
					}
					
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
		
				
			
	}

	@Override
	public int onEnd() {
		return exitCode;
	}

}
