package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class CollectorAgent extends AgentWrapper {
	private static final long serialVersionUID = 4641146536413948081L;

	private FSMBehaviour fsm ;
	
	@SuppressWarnings("unchecked")
	protected void setup(){
		super.setup();	
		
		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			this.setAgents((List<String>) args[2]);
			//these data are currently not used by the agent, its just to show you how to get them if you need it 
		}else{	
			System.out.println("Erreur lors du tranfert des parametres");
		}		
		this.setPriority(3);
		fsm = new FSMBehaviour(this);
		// Define the different states and behaviours
		fsm.registerFirstState (new ExploSoloBehaviour(this), "ExploSoloBehaviour");
		fsm.registerState (new SendMapBehaviour(this), "SendMapBehaviour");
		fsm.registerState (new ReceiveMapBehaviour(this), "ReceiveMapBehaviour");
		fsm.registerState(new CollectBehaviour(this), "CollectBehaviour");
		fsm.registerState(new CheckMailBehaviour(this), "CheckMailBehaviour");
		fsm.registerState(new GoToTankerBehaviour(this), "GoToTankerBehaviour");
		fsm.registerState(new GoToHelpBehaviour(this), "GoToHelpBehaviour");
		fsm.registerState(new InformDeadLockBehaviour(this), "InformDeadLockBehaviour");
		fsm.registerState(new AvoidDeadLockBehaviour(this), "AvoidDeadLockBehaviour");
		fsm.registerState(new ExploOldestBehaviour(this), "ExploOldestBehaviour");
		
		fsm.registerDefaultTransition("ExploSoloBehaviour","SendMapBehaviour");
		fsm.registerDefaultTransition("SendMapBehaviour","ReceiveMapBehaviour");
		fsm.registerDefaultTransition("ReceiveMapBehaviour", "ExploSoloBehaviour");
		fsm.registerTransition("ExploSoloBehaviour", "CollectBehaviour", 1);
		
		fsm.registerDefaultTransition("CollectBehaviour", "CheckMailBehaviour");
		fsm.registerTransition("CollectBehaviour", "GoToTankerBehaviour", 1);
		fsm.registerDefaultTransition("GoToTankerBehaviour", "CheckMailBehaviour");
		fsm.registerDefaultTransition("CheckMailBehaviour", "CollectBehaviour");
		fsm.registerTransition("GoToTankerBehaviour", "CollectBehaviour", 1);
		
		fsm.registerTransition("CollectBehaviour", "InformDeadLockBehaviour", 5);
		fsm.registerTransition("GoToHelpBehaviour", "InformDeadLockBehaviour", 2);
		fsm.registerTransition("CheckMailBehaviour", "AvoidDeadLockBehaviour", 2);
		fsm.registerTransition("GoToTankerBehaviour", "InformDeadLockBehaviour", 2);
		fsm.registerTransition("CollectBehaviour", "ExploOldestBehaviour", 4);
		fsm.registerTransition("ExploOldestBehaviour", "CollectBehaviour", 1);
		fsm.registerTransition("ExploOldestBehaviour", "InformDeadLockBehaviour", 2);
		fsm.registerDefaultTransition("ExploOldestBehaviour", "CheckMailBehaviour");
		
		fsm.registerDefaultTransition("InformDeadLockBehaviour", "CheckMailBehaviour"); 
		fsm.registerDefaultTransition("AvoidDeadLockBehaviour", "CheckMailBehaviour");
		
		fsm.registerTransition("CheckMailBehaviour", "GoToHelpBehaviour", 1);
		fsm.registerDefaultTransition("GoToHelpBehaviour", "CheckMailBehaviour");
		fsm.registerTransition("GoToHelpBehaviour", "CollectBehaviour", 1);
		
		fsm.registerTransition("CheckMailBehaviour", "CollectBehaviour", 10);
		fsm.registerTransition("CheckMailBehaviour", "GoToTankerBehaviour", 11);
		fsm.registerTransition("CheckMailBehaviour", "GoToHelpBehaviour", 12);

		fsm.registerTransition("AvoidDeadLockBehaviour", "InformDeadLockBehaviour", 1);
		fsm.registerTransition("AvoidDeadLockBehaviour", "CollectBehaviour", 10);
		fsm.registerTransition("AvoidDeadLockBehaviour", "GoToTankerBehaviour", 11);
		fsm.registerTransition("AvoidDeadLockBehaviour", "GoToHelpBehaviour", 12);
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(fsm);
		/***
		* MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		*/
		addBehaviour(new startMyBehaviours(this,lb));	
		System.out.println("the  agent "+this.getLocalName()+ " is started");
	
		
	
	}
	
	public void beforeMove(){
		super.beforeMove();
		System.out.println("Save everything (and kill GUI) before move");
		this.getMap().prepareMigration();
		}
	
	/**
	* Method automatically called by Jade after a migration
	*/
	public void afterMove(){
	super.afterMove();
	System.out.println("Restore data (and GUI) after moving");

	this.getMap().loadSavedData();
	}
}
