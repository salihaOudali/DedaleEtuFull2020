package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import eu.su.mas.dedaleEtu.mas.knowledge.NodeData;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AskForHelpBehaviour extends OneShotBehaviour {
	
	private static final long serialVersionUID = 8567689731496787661L;

	private AgentWrapper senderAgent;
	public AskForHelpBehaviour(AgentWrapper agent) {
		this.senderAgent = agent;
		
	}
	

	@Override
	public synchronized void action() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.senderAgent.getAID());
        msg.setProtocol("HelpProtocol");
        HashMap<String, NodeData> obs = this.senderAgent.getObservationsData();
        List<String> senders = new ArrayList<>();
        senders.add(this.senderAgent.getLocalName());
        List<Object> toSend = new ArrayList<>();
        toSend.add(obs);
        toSend.add(this.senderAgent.getCurrentPosition());;
        toSend.add(senders);
        NodeData currentObs = obs.get(this.senderAgent.getCurrentPosition());
        toSend.add(this.senderAgent.getPriority());
        toSend.add(0);
        toSend.add(0);
        int lockpicking = 0;
        int strength = 0;
        if (currentObs != null ) {
        	for (Couple<Observation, Integer> c :currentObs.getAttributes()) {
        		if (c.getLeft().equals(Observation.LOCKPICKING)) {
        			lockpicking = c.getRight();
        		}
        		if (c.getLeft().equals(Observation.STRENGH)) {
        			strength = c.getRight();
        		}
        	}
        }
        for (Couple<Observation, Integer> c :this.senderAgent.getMyExpertise()) {
        	if (c.getLeft().equals(Observation.LOCKPICKING)) {
        		toSend.set(3,lockpicking - c.getRight() );
        	}
        	if (c.getLeft().equals(Observation.STRENGH)) {
        		toSend.set(4,strength - c.getRight()  );
        	}
        }
        
        
        try {
			msg.setContentObject((Serializable) toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        boolean sent = false;
        if (senderAgent.getAgents() != null) {
	        for(String agent : senderAgent.getAgents()){
	            if (!agent.equals(myAgent.getLocalName()) && !senders.contains(agent))
	            	sent = true;
	                msg.addReceiver(new
	                		AID(agent,AID.ISLOCALNAME));
	        }
        }
        if(sent) {
        	this.senderAgent.sendMessage(msg);
        }
		
	}
	


	@Override
	public int onEnd() {
		
		return 0;
	}



}
