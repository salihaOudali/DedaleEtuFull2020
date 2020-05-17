package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentWrapper;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class InformDeadLockBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4833297260989726666L;
	private AgentWrapper agent;
	private int exitCode = 0;
	
	public InformDeadLockBehaviour(AgentWrapper agent) {
		this.agent=agent;
	}
	
	@Override
	public void action() {
		exitCode = this.agent.getLastBehaviour();
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(this.agent.getAID());
        msg.setProtocol("DeadLockProtocol");
        List<Object> toSend = new ArrayList<>();
        toSend.add(this.agent.getPath());
        toSend.add(this.agent.getLocalName());
        toSend.add(this.agent.getCurrentPosition());;
        toSend.add(this.agent.getPriority());
        
        
        
        try {
			msg.setContentObject((Serializable) toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        boolean sent = false;
        if (agent.getAgents() != null) {
	        for(String ag : agent.getAgents()){
	            if (!ag.equals(agent.getLocalName()) ) {
	        	    sent = true;
	                msg.addReceiver(new
	                		AID(ag,AID.ISLOCALNAME));
	            }
	        }
        }
        if(sent) {
    
        	this.agent.sendMessage(msg);
        }
	}
	
	@Override
	public int onEnd() {
		return exitCode;
	}

}
