package eu.su.mas.dedaleEtu.mas.behaviours;


import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMapBehaviour extends OneShotBehaviour{
	private static final long serialVersionUID = 8567689731496787661L;
	private AgentWrapper receiverAgent;
	private boolean finished = false;
	public ReceiveMapBehaviour(AgentWrapper agent) {
		receiverAgent = agent;
		
	}

	@Override
	public void action() {
		final MessageTemplate template= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("SendMapProtocol"));
		final ACLMessage msg = receiverAgent.receive(template);
		if (msg!=null){
			// Processing of the message
			try {

				
				Object knowledge =  msg.getContentObject();

					List knowledgeList = (List) knowledge;
					HashMap<String, NodeData> obs = (HashMap<String, NodeData>) knowledgeList.get(0);
					List<Couple<String,String>> edges = (List<Couple<String, String>>) knowledgeList.get(1);
					List<String> closedNodes = (List<String>) knowledgeList.get(2);
					List<String> openNodes = (List<String>) knowledgeList.get(3);
					String myPosition = (String) knowledgeList.get(4);
					for (String node: closedNodes) {
						if(!this.receiverAgent.getClosedNodes().contains(node)) {
							this.receiverAgent.getClosedNodes().add(node);
							this.receiverAgent.getOpenNodes().remove(node);
							this.receiverAgent.addNodeToMap(node);
							
						}
					}
					for (String node: openNodes) {
						if((!this.receiverAgent.getOpenNodes().contains(node) && !this.receiverAgent.getClosedNodes().contains(node))) {
							this.receiverAgent.getOpenNodes().add(node);
							this.receiverAgent.addNodeToMap(node, MapAttribute.open);
							
						}
					}
					for (Couple<String, String> edge: edges) {
						this.receiverAgent.addEdgeToMap(edge.getLeft(), edge.getRight());
					}
					for ( String c: obs.keySet()) {
						this.receiverAgent.addObservation(new Couple(c, obs.get(c)));
					}
					
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
		
				
		finished = true;
			
	}

	@Override
	public int onEnd() {
		return 0;
	}

}