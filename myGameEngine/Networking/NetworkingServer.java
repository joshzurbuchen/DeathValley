package myGameEngine.Networking;

import java.io.IOException;
import ray.networking.IGameConnection.ProtocolType;


public class NetworkingServer{
	
	private GameServerUDP thisUDPServer;
	//private GameServerTCP thisTCPServer; no sample given for the tcp model
	
	//NPC related attributes
	private NPCcontroller npcCtrl;
	private long startTime;
	private long lastUpdateTime;
	
	
	
	public NetworkingServer(int serverPort, String protocol){
		startTime = System.nanoTime();
		lastUpdateTime = startTime;
		//npcCtrl = new NPCcontroller(thisUDPServer);
		npcCtrl = new NPCcontroller();
		
		try{
			if(protocol.toUpperCase().compareTo("TCP") == 0){
				//thisTCPServer = new GameServerTCP(serverPort);
			}
			else{
				thisUDPServer = new GameServerUDP(serverPort, npcCtrl);
				npcCtrl.setServer(thisUDPServer);
			}
			
		}
		catch (IOException e){
			e.printStackTrace();
		} 
		
		//start NPC control loop
		System.out.println("NetworkingServer constructor making setting up trees");
		npcCtrl.setupTrees();
		System.out.println("NetworkingServer constructor making setting up NPCs");
		npcCtrl.setupNPC();
		
		npcLoop();
	}
	
	 public void npcLoop(){ // NPC control loop
		//System.out.println("NetworkingServer: npcLoop");
		while (true){
			long frameStartTime = System.nanoTime();
			float elapMilSecs = (frameStartTime-lastUpdateTime)/(1000000.0f);
			if (elapMilSecs >= 50.0f){
				lastUpdateTime = frameStartTime;
				npcCtrl.updateNPCs();
				thisUDPServer.sendNPCinfo();
			}
			Thread.yield();
		}
	}
	
	public static void main(String[] args){
		if(args.length > 1){
			NetworkingServer app = new NetworkingServer(Integer.parseInt(args[0]), args[1]);
		} 
	} 
}