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
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CheckMailBehaviour extends OneShotBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8681913102297365745L;
	private AgentWrapper receiverAgent;
	private int exitCode = 0;
	
	public CheckMailBehaviour(AgentWrapper receiverAgent) {
		this.receiverAgent= receiverAgent;
	}
	@Override
	public void action() {
		exitCode = this.receiverAgent.getLastBehaviour();
		this.someOneAsksForHelp();
		this.deadLockDetected();
			
	}
	
	private void someOneAsksForHelp() {
		final MessageTemplate template= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("HelpProtocol"));
		final ACLMessage msg = receiverAgent.receive(template);
		if (msg!=null){
			// Processing of the message
			try {

					Object knowledge =  msg.getContentObject();
					
					List knowledgeList = (List) knowledge;
					HashMap<String, NodeData> obs = (HashMap<String, NodeData>) knowledgeList.get(0);
					List<String> senders = (List<String>) knowledgeList.get(1);
					int priority = (Integer) knowledgeList.get(2);
					int lockpicking = (Integer) knowledgeList.get(3);
					int strength = (Integer) knowledgeList.get(4);       
			        
					for (Couple<Observation, Integer> c :this.receiverAgent.getMyExpertise()) {
			        	if (c.getLeft().equals(Observation.LOCKPICKING)) {
			        		lockpicking -= c.getRight() ;
			        	}
			        	if (c.getLeft().equals(Observation.STRENGH)) {
			        		strength -= c.getRight();
			        	}
			        }
					if (!senders.contains(this.receiverAgent.getName()) && priority > this.receiverAgent.getPriority()) {
						
						for ( String c: obs.keySet()) {
							this.receiverAgent.addObservation(new Couple(c, obs.get(c)));
						}
						if  (lockpicking > 0 || strength > 0) {
							senders.add(this.receiverAgent.getLocalName());
							
						    ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
					        msg2.setSender(this.receiverAgent.getAID());
					        msg2.setProtocol("HelpProtocol");
					        
					        obs = this.receiverAgent.getObservationsData();
					        List<Object> toSend = new ArrayList<>();
					        toSend.add(obs);
					        toSend.add(senders);
					        toSend.add(priority);
					        toSend.add(lockpicking);
					        toSend.add(strength);
					        
					        
					        try {
					        	msg2.setContentObject((Serializable) toSend);
							} catch (IOException e) {
								e.printStackTrace();
							}
					        if (receiverAgent.getAgents() != null) {
						        for(String agent : receiverAgent.getAgents()){
						            if (!agent.equals(myAgent.getLocalName()) && !senders.contains(agent))
						            	msg2.addReceiver(new
						                		AID(agent,AID.ISLOCALNAME));
						            	//System.out.println(senderAgent.getName() + "sent a message to"+agent);
						        }
					        }
					        this.receiverAgent.sendMessage(msg2);
						}
				        this.receiverAgent.setPathToHelp(obs.get(0).nodeId);
				        exitCode = 1;
					}
					
					
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void deadLockDetected() {
		final MessageTemplate template= MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchProtocol("DeadLockProtocol"));
		final ACLMessage msg = receiverAgent.receive(template);
		if (msg!=null){
			// Processing of the message
			try {

					Object knowledge =  msg.getContentObject();
					
					List knowledgeList = (List) knowledge;
					List<String> pathToAvoid = (List<String>) knowledgeList.get(0);
					String sender = (String) knowledgeList.get(1);
					String currentPos = (String) knowledgeList.get(2);
					int priority = (Integer) knowledgeList.get(3);
					this.receiverAgent.setPathToAvoid(pathToAvoid);
					this.receiverAgent.setDeadLockPos(currentPos);
					this.receiverAgent.setDeadLockPriority(priority);
					this.receiverAgent.setDeadLockSender(sender);

					exitCode = 2;
					
					
				
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
