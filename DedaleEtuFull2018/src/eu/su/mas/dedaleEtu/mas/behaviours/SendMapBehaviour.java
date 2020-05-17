package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

public class SendMapBehaviour extends OneShotBehaviour{
	private static final long serialVersionUID = 8567689731496787661L;
	private AgentWrapper senderAgent;
	public SendMapBehaviour(final AgentWrapper agent) {
		this.senderAgent=agent;
		
		
		
	}

	@Override
	public synchronized void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.senderAgent.getAID());
        msg.setProtocol("SendMapProtocol");
        HashMap<String, NodeData> obs = this.senderAgent.getObservationsData();
        List<Object> toSend = new ArrayList<>();
        toSend.add(obs);
        toSend.add(this.senderAgent.getEdges());
        toSend.add(this.senderAgent.getClosedNodes());
        toSend.add(this.senderAgent.getOpenNodes());
        toSend.add(this.senderAgent.getCurrentPosition());
        try {
			msg.setContentObject((Serializable) toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
        if (senderAgent.getAgents() != null) {
	        for(String agent : senderAgent.getAgents()){
	            if (!agent.equals(myAgent.getName()))
	                msg.addReceiver(new
	                		AID(agent,AID.ISLOCALNAME));
	        }
        }
        this.senderAgent.sendMessage(msg);
		
	}
	


	@Override
	public int onEnd() {
		
		return 0;
	}

}