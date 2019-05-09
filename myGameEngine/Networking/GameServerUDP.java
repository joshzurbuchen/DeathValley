package myGameEngine.Networking;

import myGameEngine.Networking.NPCcontroller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import ray.networking.server.GameConnectionServer;
import ray.networking.server.IClientInfo;

import ray.rml.*;

public class GameServerUDP extends GameConnectionServer<UUID>{
	
	private NPCcontroller npcCtrl;
	private int players = 0;
	
	public GameServerUDP(int localPort, NPCcontroller npcCtrl) throws IOException{ 
		super(localPort, ProtocolType.UDP);
		System.out.println("UDP localPort: " + localPort);
		this.npcCtrl = npcCtrl;
	}
	
	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort){
		
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if(msgTokens.length > 0){
			
			// case where server receives a JOIN message
			// format: join,localid
			if(msgTokens[0].compareTo("join") == 0 && players < 2){
				try{
					IClientInfo ci;
					ci = getServerSocket().createClientInfo(senderIP, sndPort);
					UUID clientID = UUID.fromString(msgTokens[1]);
					addClient(ci, clientID);
					sendJoinedMessage(clientID, true);
					
					npcCtrl.setpid(msgTokens[1], players);
					players++;
				}
				catch (IOException e){
					e.printStackTrace();
				} 
			}
			
			// case where server receives a CREATE message
			// format: create,localid,x,y,z
			if(msgTokens[0].compareTo("create") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
				sendCreateMessages(clientID, pos);
				sendWantsDetailsMessages(clientID);
				//sendNeedNPCinfo(clientID);
			}
			
			// case where server receives a BYE message
			// format: bye,localid
			if(msgTokens[0].compareTo("bye") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendByeMessages(clientID);
				removeClient(clientID);
			}
			
			// case where server receives a DETAILS-FOR message
			if(msgTokens[0].compareTo("dsfr") == 0){
				 UUID clientID = UUID.fromString(msgTokens[1]);
				 UUID remoteId = UUID.fromString(msgTokens[2]);
				 String[] pos = { msgTokens[3], msgTokens[4], msgTokens[5] };
				 sndDetailsMsg(clientID, remoteId, pos);
			}
			
			// case where server receives a MOVE message
			if(msgTokens[0].compareTo("move") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4] };
				sendMoveMessages(clientID, pos);
			}
			
			//also additional cases for recieving messages about NPCs, such as:
			if(msgTokens[0].compareTo("mnpc") == 0){
				UUID clientID = UUID.fromString(msgTokens[1]);
				//String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4] };
				sendNPCinfo();
			}
			
			if(msgTokens[0].compareTo("needNPC") == 0){
				//System.out.println("GameServerUDP recieved needNPC message");
				UUID clientID = UUID.fromString(msgTokens[1]);
				sendNeedNPCinfo(clientID);
			}
			
			if(msgTokens[0].compareTo("rpos")==0){
				//UUID clientID = UUID.fromString(msgTokens[1]);
				String clientID = msgTokens[1];
				Vector3 pos = Vector3f.createFrom(
					Float.parseFloat(msgTokens[2]), 
					Float.parseFloat(msgTokens[3]), 
					Float.parseFloat(msgTokens[4]));
				npcCtrl.updatePlayerLoc(clientID, pos);
			}
			
			if(msgTokens[0].compareTo("tree") == 0){
				System.out.println("GameServerUDP recieved tree message");
				UUID clientID = UUID.fromString(msgTokens[1]);
				//String[] pos = { msgTokens[2], msgTokens[3], msgTokens[4] };
				sendTreeInfo(clientID);
			}
			
			if(msgTokens[0].compareTo("collide") == 0){
				
			}
		} 
	}
	
	
	public void sendJoinedMessage(UUID clientID, boolean success){
		// format: join, success or join, failure
		try{
			String message = new String("join,");
			if (success) message += "success";
			else message += "failure";
			sendPacket(message, clientID);
		}
		catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	
	public void sendCreateMessages(UUID clientID, String[] position){

		// format: create, remoteId, x, y, z
		try{
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		}
		catch (IOException e){
			e.printStackTrace();
		} 
	}
	
	
	public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position){
		
		try{
			String message = new String("dsfr," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, remoteId);	
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void sendWantsDetailsMessages(UUID clientID){
		
		try{
			String message = new String("wsds," + clientID.toString());
			forwardPacketToAll(message, clientID);	
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void sendMoveMessages(UUID clientID, String[] position){
		
		try{
			String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e){
			e.printStackTrace();
		}	
	}
	
	
	public void sendByeMessages(UUID clientID){
		
		try{
			String message = new String("bye," + clientID.toString());
			forwardPacketToAll(message, clientID);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//******************NPC related stuff
	
	public void sendNPCinfo(){//UUID clientID){ //informs clients of new NPC positions
	
		for(int i = 0; i<npcCtrl.getNumOfNPCs(); i++){
			try{
				String message = new String("mnpc," + Integer.toString(i));
				message += "," + (npcCtrl.getNPC(i)).getX();
				message += "," + (npcCtrl.getNPC(i)).getY();
				message += "," + (npcCtrl.getNPC(i)).getZ();
				sendPacketToAll(message);
				//System.out.println("GameServerUDP sendNPCinfo " + message);
				//sendPacket(message, clientID);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void sendNeedNPCinfo(UUID clientID){
		//System.out.println("GameServerUDP sending needNPC message to " + clientID);
		for(int i = 0; i<npcCtrl.getNumOfNPCs(); i++){
			try{
				String message = new String("needNPC," + Integer.toString(i));
				message += "," + (npcCtrl.getNPC(i)).getX();
				message += "," + (npcCtrl.getNPC(i)).getY();
				message += "," + (npcCtrl.getNPC(i)).getZ();
				sendPacket(message, clientID);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void sendRequestPosition(){
	
		try{
			String message = new String("rpos");
			sendPacketToAll(message);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	
	}
	
	public void sendTreeInfo(UUID clientID){
		
		for(int i = 0; i < npcCtrl.getNumOfTrees(); i++){
			try{
				String message = new String("tree," + Integer.toString(i));
				message += "," + (npcCtrl.getTree(i)).getX();
				message += "," + (npcCtrl.getTree(i)).getY();
				message += "," + (npcCtrl.getTree(i)).getZ();
				System.out.println("GameServerUDP sendTreeInfo " + i);
				sendPacket(message, clientID);
			}
			catch(IOException e){
				e.printStackTrace();
			}	
		}
		
	}
	
	public void sendCheckForAvatarNear(){
		
	}	
}
