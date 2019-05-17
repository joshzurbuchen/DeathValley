package myGameEngine.Networking;

import myGameEngine.Networking.*;

import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.lang.NullPointerException;


import ray.ai.behaviortrees.BTCompositeType;
import ray.ai.behaviortrees.BTSequence;
import ray.ai.behaviortrees.BehaviorTree;

import ray.rml.*;

public class NPCcontroller{
	
	private int numNPCs = 1;
	private int numTrees = 150;
	private int spellID = 0;
	private NPC[] NPClist = new NPC[numNPCs];
	private NPC[] treelist = new NPC[numTrees];
	private Vector<NPCspell> spelllist = new Vector<NPCspell>();
	private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private long thinkStartTime;
	private long tickStartTime;
	private long lastThinkUpdateTime;
	private long lastTickUpdateTime;
	private Random rn = new Random();
	private boolean dropFlag;
	
	private GameServerUDP server;
	private int numPlayers = 2;
	private String[] pid = new String[numPlayers];
	private Vector3[] pos = new Vector3f[numPlayers];
	
	public NPCcontroller(){//GameServerUDP server){
		//this.server = server;
	}
	
	public void setServer(GameServerUDP server){
		this.server = server;
	}
	
	public int getNumOfNPCs(){
		return numNPCs;
	}
	
	public int getNumOfTrees(){
		return numTrees;
	}
 
	public void updateNPCs(){
		for(int i = 0; i < numNPCs; i++)
			NPClist[i].updateLocation();
	}
	
	public NPC getNPC(int i){
		return NPClist[i];
	}
	
	public NPC getTree(int i){
		return treelist[i];
	}

	public void start(NPC npc){
		thinkStartTime = System.nanoTime();
		tickStartTime = System.nanoTime();
		lastThinkUpdateTime = thinkStartTime;
		lastTickUpdateTime = tickStartTime;
		
		//setupNPC();
		setupBehaviorTree(npc);
		npcLoop(npc);
	}
	
	
	public void setupNPC(){
		for(int i = 0; i < numNPCs; i++){
			NPClist[i] = new NPC();
			//NPClist[i].randomizeLocation(rn.nextInt(10), rn.nextInt(10), rn.nextInt(10));
			NPClist[i].randomizeLocation(0, 10, 0);
			//setupBehaviorTree(NPClist[i]);
			start(NPClist[i]);
		}
		System.out.println("numNPCs = " + numNPCs);
	}
	
	public void setupTrees(){
		for(int i = 0; i < numTrees; i++){
			treelist[i] = new NPC();
			System.out.println("NPCcontroller setUpTrees" + i);
			treelist[i].randomizeLocation(rn.nextInt(700) - 350, 0, rn.nextInt(700) - 350);
			//treelist[i].randomizeLocation(0, 10, 0);
			
		}
		System.out.println("numTrees = " + numTrees);
	}
	
	public void npcLoop(NPC npc){
		while(true){
			//System.out.println("NPCcontroller npcLoop true");
			long currentTime = System.nanoTime();
			float elpasedThinkMilliSecs = (currentTime - lastThinkUpdateTime)/(1000000.0f);
			float elapsedTickMilliSecs = (currentTime - lastTickUpdateTime)/(1000000.0f);
			
			if(elapsedTickMilliSecs >= 50.0f){ //"TICK"
				lastTickUpdateTime = currentTime;
				npc.updateLocation();
				
				
				try{
					server.sendNPCinfo();
					//System.out.println("NPCcontroller: npcLoop");
				}catch(NullPointerException e){
					System.out.println("sendNPCinfo failed");
				}
				
			}
			
			if(elpasedThinkMilliSecs >= 500.0f){ //"THINK"
				lastThinkUpdateTime = currentTime;
				bt.update(elpasedThinkMilliSecs);
			}
			
			Thread.yield();
		}
	}
	
	public void setupBehaviorTree(NPC npc){
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10, new ThreeSecPassed(this,npc,false));
		bt.insert(10, new NpcDrop(npc,this));
		bt.insert(20, new NpcMove(npc, server, this));
	}
	
	public void setdropFlag(boolean flag){
		dropFlag = flag;
	}
	
	public boolean getdropFlag(){
		return dropFlag;
	}
	
	public void drop(double X, double Y, double Z){
		//make new spell
		NPCspell newSpell = new NPCspell(spellID);
		newSpell.setLocation(X, Y, Z);
		
		//add newSpell to spelllist
		spelllist.add(newSpell);
		
		//tell server to send connected clients to populate a spell object
		server.sendDrop(spellID, X, Y, Z);
		
		//increment spellID for the next spell created
		spellID++;
	}
	
	public void setpid(String id, int i) {
		pid[i] = id;
		System.out.println("PID " + i + " Set: " + id);
	}
	
	public Vector3[] getpos() {
		return pos;
	}	
	
	public void updatePlayerLoc(String clientID, Vector3 pPos){
		
		for(int i = 0; i < numPlayers; i++){
			
			if(pid[i] == null) {
				break;
			} else if(pid[i].equals(clientID)){
				pos[i] = pPos;
				System.out.println("NPCcontroller updatePlayerLoc " + clientID + " " + pos[i]);
				break;
			}
		}
	}
}