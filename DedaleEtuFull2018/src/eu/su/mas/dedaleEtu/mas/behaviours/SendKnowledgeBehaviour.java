package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendKnowledgeBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6393919818338223067L;
	private AgentWrapper senderAgent;
	
	public SendKnowledgeBehaviour(AgentWrapper agent) {
		this.senderAgent = agent;
	}
	
	@Override
	public void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.senderAgent.getAID());
        msg.setProtocol("SendKnowledgeProtocol");
        HashMap<String, NodeData> obs = this.senderAgent.getObservationsData();
        List<Object> toSend = new ArrayList<>();
        toSend.add(obs);
        toSend.add(this.senderAgent.getCurrentPosition());
        toSend.add( this.getClass());
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

}
