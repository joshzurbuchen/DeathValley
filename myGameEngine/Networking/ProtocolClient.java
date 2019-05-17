package myGameEngine.Networking;

import a3.MyGame;
import myGameEngine.Networking.GhostAvatar;

import java.util.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.UUID;
import java.io.IOException;
import java.net.InetAddress;

import ray.networking.client.GameConnectionClient;
import ray.networking.IGameConnection.ProtocolType;

import ray.rml.*;


public class ProtocolClient extends GameConnectionClient{
	
	private MyGame game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private Vector<GhostNPC> ghostNPCs;
	private Vector<GhostNPC> treeNPCs;
	
	public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, MyGame game)throws IOException{
	
		super(remAddr, remPort, pType);
		this.game = game;
		id = UUID.randomUUID();
		ghostAvatars = new Vector<GhostAvatar>();
		ghostNPCs = new Vector<GhostNPC>();
		treeNPCs = new Vector<GhostNPC>();
		System.out.println("Protocol Client instantiated");
	}
	
	@Override
	protected void processPacket(Object msg){
		
		String strMessage = (String)msg;
		String[] messageTokens = strMessage.split(",");
		
		if(messageTokens.length > 0){
			if(messageTokens[0].compareTo("join") == 0) 
			{ // format: join, success or join, failure
				if(messageTokens[1].compareTo("success") == 0){
					game.setIsConnected(true);
					System.out.println("Connected");
					sendCreateMessage(game.getPlayerPosition());
				}
				if(messageTokens[1].compareTo("failure") == 0){
					game.setIsConnected(false);
				}
			}
			if(messageTokens[0].compareTo("bye") == 0) 
			{ // format: bye, remoteId
				UUID ghostID = UUID.fromString(messageTokens[1]);
				removeGhostAvatar(ghostID);
			}
			if ((messageTokens[0].compareTo("dsfr") == 0 ) 
			 || (messageTokens[0].compareTo("create")==0))
			{ // format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				
					createGhostAvatar(ghostID, ghostPosition);
			}
			if(messageTokens[0].compareTo("wsds") == 0) 
			{ // etc….. 
				UUID ghostID = UUID.fromString(messageTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition());
			}
			
			if(messageTokens[0].compareTo("move") == 0)
			{ // etc….. 
				UUID ghostID = UUID.fromString(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
					
				//System.out.println("Ghost move: " + Arrays.toString(messageTokens));
				moveGhostAvatar(ghostID, ghostPosition);
			}
			
			//handle updates to NPC positions
			//format (mnpc,npcID,x,y,z)
			if(messageTokens[0].compareTo("mnpc")==0){
				int ghostNPCID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				updateGhostNPC(ghostNPCID, ghostPosition);
				
			}
			
			if(messageTokens[0].compareTo("needNPC")==0){
				//System.out.println("ProtocolClient recieved needNPC message from GameServerUDP");
				int ghostNPCID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
					
				try{
					createGhostNPC(ghostNPCID, ghostPosition);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
			//recieve request for position message
			if(messageTokens[0].compareTo("rpos")==0){
				sendPos();
			}
			
			if(messageTokens[0].compareTo("tree")==0){
				int ghostNPCID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				
				try{
					createGhostTree(ghostNPCID, ghostPosition);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			
			if(messageTokens[0].compareTo("drop")==0){
				int ghostSpellID = Integer.parseInt(messageTokens[1]);
				Vector3 ghostPosition = Vector3f.createFrom(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
					
					//try{
						createGhostSpell(ghostSpellID, ghostPosition);
					//}
					//catch(IOException e){
						//e.printStackTrace();
					//}
			}
		} 
	}
	

/************************************/
/*		Send messages section		*/

	public void sendJoinMessage(){
		
		try{
			String msg = "join," + id.toString();
			sendPacket(msg);
		}
		catch(IOException e){
				e.printStackTrace();
		}
	}
	
	public void sendCreateMessage(Vector3 pos){
		
		try{
			String message = new String("create," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch(IOException e){
				e.printStackTrace();
		}
	}
	
	public void sendByeMessage(){
		
		try{
			String message = new String("bye," + id.toString());
			sendPacket(message);
		}
		catch(IOException e){
				e.printStackTrace();
		}
	}
	
	public void sendDetailsForMessage(UUID remid, Vector3 pos){
		
		try{
			String message = new String("dsfr," + id.toString());
			message += "," + remid.toString();
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendMoveMessage(Vector3 pos){
		
		try{
			String message = new String("move," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			//System.out.println(message);
			sendPacket(message);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	protected void createGhostAvatar(UUID ghostID, Vector3 ghostPosition){
		
		GhostAvatar avatar = new GhostAvatar(ghostID, ghostPosition);
		ghostAvatars.add(avatar);
		try{
			game.addGhostAvatarToGameWorld(avatar);
		} 
		catch (IOException e){
			System.out.println("error creating ghost avatar");
		} 
		
	}
	
	protected void removeGhostAvatar(UUID ghostID){
		
		GhostAvatar ptr = getGhostAvatar(ghostID);
		
		if(ptr != null){
			ghostAvatars.remove(ptr);
			game.removeGhostAvatarFromGameWorld(ptr);
		}
	}
	
	protected void moveGhostAvatar(UUID ghostID, Vector3 pos){
		
		GhostAvatar ptr = getGhostAvatar(ghostID);
		//System.out.println("Actual Position: " + pos.x() + ", " + pos.y() + ", " + pos.y());
		if(ptr != null){
			ptr.setPosition(pos);
			ptr.getNode().setLocalPosition(pos);
			Vector3 temp = ptr.getNode().getWorldPosition();
		}
	}
	
	protected GhostAvatar getGhostAvatar(UUID ghostID){
		
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		GhostAvatar ptr = null;
		while(it.hasNext()){
			ptr = it.next();
			if(ptr.getID() == ghostID)
				return ptr;
		}
		
		return ptr;
	}
	
	
//************** Ghost NPC stuff
	private void createGhostNPC(int id, Vector3 position)throws IOException{
		
		//System.out.println("ProtocolClient calling MyGame.addGhostNPCtoGameWorld");
		GhostNPC newNPC = new GhostNPC(id, position);
		
		if(newNPC == null)
			System.out.println("newNPC is null");
		else{
			ghostNPCs.add(newNPC);
			
			try{
				game.addGhostNPCtoGameWorld(newNPC);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private void updateGhostNPC(int id, Vector3 position){
		//System.out.println("ProtocolClient updatting NPC " + position);
		if(ghostNPCs.size() > 0)
			ghostNPCs.get(id).setPosition(position);
	}
	
	public void askForNPCinfo(){
		//System.out.println("ProtocolClient relaying askForNPCinfo");
		try{
			sendPacket(new String("needNPC," + id.toString()));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void askForNPCpos(){
		
		try{
			sendPacket(new String("mnpc," + id.toString()));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendPos(){
			
		try{
			Vector3 pos = game.getPlayerPosition();
			String message = new String("rpos," + id.toString());
			message += "," + pos.x() + "," + pos.y() + "," + pos.z();
			sendPacket(message);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sendTreeRequest(){
		
		try{
			sendPacket(new String("tree," + id.toString()));
			System.out.println("ProtocolClient sendTreeRequest " + id.toString());
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void createGhostTree(int id, Vector3 position)throws IOException{
		
		game.addGhostTreetoGameWorld(id, position);
		/*GhostNPC newNPC = new GhostNPC(id, position);
		
		if(newNPC == null)
			System.out.println("newTreeNPC is null");
		else{
			treeNPCs.add(newNPC);
			
			//try{
			game.addGhostTreetoGameWorld(id, position);
			//}catch(IOException e){
				//e.printStackTrace();
			//}
		
		}
		*/
	}
	
	public void createGhostSpell(int ghostSpellID, Vector3 ghostPosition){
	
		try{
			game.addGhostSpell(ghostSpellID, ghostPosition);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}