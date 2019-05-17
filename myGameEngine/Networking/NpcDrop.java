package myGameEngine.Networking;

//import myGameEngine.Networking.GameServerUDP;

import java.lang.NullPointerException;
import ray.ai.behaviortrees.*;

import ray.rml.*;

public class NpcDrop extends BTAction{
	
	private NPC npc;
	private boolean goingUp;
	//private GameServerUDP server;
	private NPCcontroller npcCtrl;
	private float prevTime;
	
	public NpcDrop(NPC npc, NPCcontroller npcC) { 
		//System.out.println("NpcDrop constructor");
		this.npc = npc;
		//this.server = server;
		npcCtrl = npcC;
	}
 
	protected BTStatus update(float elapsedTime){
		System.out.println("NpcDrop update");
		if(npcCtrl.getdropFlag()){
			//System.out.println("NpcDrop update*********************************************");
			npcCtrl.drop(npc.getX(), npc.getY(), npc.getZ());
			npcCtrl.setdropFlag(false);
		}

		return BTStatus.BH_SUCCESS;
	}

	
}