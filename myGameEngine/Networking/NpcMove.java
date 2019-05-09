package myGameEngine.Networking;

import myGameEngine.Networking.GameServerUDP;

import java.lang.NullPointerException;
import ray.ai.behaviortrees.*;

import ray.rml.*;

public class NpcMove extends BTAction{
	
	private NPC npc;
	private boolean goingUp;
	private GameServerUDP server;
	private NPCcontroller npcCtrl;
	
	public NpcMove(NPC n, GameServerUDP server, NPCcontroller npcC) { 
		npc = n;
		this.server = server;
		goingUp = true;
		npcCtrl = npcC;
	}
 
	protected BTStatus update(float elapsedTime){
		//System.out.println("NpcMove goingUp" + goingUp);
		try{
			server.sendRequestPosition();
		}
		catch(NullPointerException e){
			System.out.println("NpcMove sendRequestPosition NullPointerException");
		}
		
		//System.out.println("Tree Update");
		npc.setTarget(findClosest(npcCtrl.getpos()));

		return BTStatus.BH_SUCCESS;
	}

	private Vector3 findClosest(Vector3[] players) {
		Vector3 close = Vector3f.createFrom(0.0f,0.0f,0.0f);
		float dist = Float.POSITIVE_INFINITY;
		try {
			for(int i = 0; i < players.length; i++) {
				float temp = (float) Math.sqrt(Math.pow(npc.getX()-players[i].x(), 2) + Math.pow(npc.getY()-players[i].y(), 2) + Math.pow(npc.getZ()-players[i].z(), 2));
				if(temp < dist) {
					dist = temp;
					close = players[i];
				}
			}
		}catch(NullPointerException e){
			//System.out.println("NPCMove: No players Found");
		}	
		return close;
	}
}