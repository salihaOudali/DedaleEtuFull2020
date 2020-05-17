package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;

public class ExploreSoloAgent extends AgentWrapper{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4641146536413948081L;

	
	private FSMBehaviour fsm ;
	
	@SuppressWarnings("unchecked")
	protected void setup(){
		super.setup();	
		this.setPriority(2);
		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			//these data are currently not used by the agent, its just to show you how to get them if you need it 
			this.setAgents((List<String>) args[2]);
		}else{	
			System.out.println("Erreur lors du tranfert des parametres");
		}		
		fsm = new FSMBehaviour(this);
		// Define the different states and behaviours
		fsm.registerFirstState (new ExploSoloBehaviour(this), "ExploSoloBehaviour");
		fsm.registerState (new SendMapBehaviour(this), "SendMapBehaviour");
		fsm.registerState (new ReceiveMapBehaviour(this), "ReceiveMapBehaviour");
		fsm.registerState(new CheckMailBehaviour(this), "CheckMailBehaviour");
		fsm.registerState(new ExploOldestBehaviour(this), "ExploOldestBehaviour");
		fsm.registerState(new GoToHelpBehaviour(this), "GoToHelpBehaviour");
		fsm.registerState(new InformDeadLockBehaviour(this), "InformDeadLockBehaviour");
		fsm.registerState(new AvoidDeadLockBehaviour(this), "AvoidDeadLockBehaviour");
		
		fsm.registerDefaultTransition("ExploSoloBehaviour","SendMapBehaviour");
		fsm.registerDefaultTransition("SendMapBehaviour","ReceiveMapBehaviour");
		fsm.registerDefaultTransition("ReceiveMapBehaviour", "ExploSoloBehaviour");
		fsm.registerTransition("ExploSoloBehaviour", "ExploOldestBehaviour", 1);
		
		fsm.registerDefaultTransition("ExploOldestBehaviour", "CheckMailBehaviour");
		fsm.registerDefaultTransition("CheckMailBehaviour", "ExploOldestBehaviour");	
		fsm.registerTransition("CheckMailBehaviour", "GoToHelpBehaviour", 1);
		fsm.registerDefaultTransition("GoToHelpBehaviour", "CheckMailBehaviour");
		fsm.registerTransition("GoToHelpBehaviour", "ExploOldestBehaviour", 1);
		
		fsm.registerTransition("ExploOldestBehaviour", "InformDeadLockBehaviour", 2);
		fsm.registerTransition("GoToHelpBehaviour", "InformDeadLockBehaviour", 2);
		fsm.registerTransition("CheckMailBehaviour", "AvoidDeadLockBehaviour", 2);
		fsm.registerDefaultTransition("InformDeadLockBehaviour", "CheckMailBehaviour"); 
		fsm.registerDefaultTransition("AvoidDeadLockBehaviour", "ExploOldestBehaviour");
		
		fsm.registerTransition("CheckMailBehaviour", "ExploOldestBehaviour", 10);
		fsm.registerTransition("CheckMailBehaviour", "GoToHelpBehaviour", 12);
		
		fsm.registerTransition("AvoidDeadLockBehaviour", "ExploOldestBehaviour", 10);
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