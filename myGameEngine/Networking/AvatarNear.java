package myGameEngine.Networking;

import myGameEngine.Networking.*;

import ray.ai.behaviortrees.BTCondition;



public class AvatarNear extends BTCondition{
	
	private GameServerUDP server;
	private NPCcontroller npcc;
	private NPC npc;
 
	public AvatarNear(GameServerUDP s, NPCcontroller c, NPC n, boolean toNegate){
		super(toNegate);
		server = s;
		npcc = c;
		npc = n;
	}
	
	protected boolean check(){
		server.sendCheckForAvatarNear();
		return npcc.getNearFlag();
	} 
}