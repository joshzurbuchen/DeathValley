package myGameEngine.Networking;

import myGameEngine.Networking.NPCcontroller;
import myGameEngine.Networking.NPC;

import java.lang.NullPointerException;
import ray.ai.behaviortrees.*;

import ray.rml.*;

public class ThreeSecPassed extends BTCondition
{
	private NPCcontroller npcc;
	private NPC npc;
	private float lastUpdateTime;
	
	 public ThreeSecPassed(NPCcontroller c, NPC n, boolean toNegate){
		super(toNegate);
		npcc = c;
		npc = n;
		lastUpdateTime = System.nanoTime();
		//System.out.println("3SecPassed constructor");
	 }
	 
	 protected boolean check(){
		 
		// System.out.println("3SecPassed check");
		float elapsedMilliSecs = (System.nanoTime()-lastUpdateTime)/(1000000.0f);
		if (elapsedMilliSecs >= 3000.0f){
			lastUpdateTime = System.nanoTime();
			npcc.setdropFlag(true);
			
			return true;
		}
		
		else return false;
	} 
}