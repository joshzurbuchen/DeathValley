package myGameEngine.Networking;

import ray.ai.behaviortrees.*;

public class GetSmall extends BTAction{
	
	private NPC npc;
	
	public GetSmall(NPC n) { npc = n; }
 
	protected BTStatus update(float elapsedTime){
		npc.getSmall();
		return BTStatus.BH_SUCCESS;
	} 
}