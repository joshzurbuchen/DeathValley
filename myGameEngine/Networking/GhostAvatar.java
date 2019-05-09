package myGameEngine.Networking;

import java.util.UUID;

import ray.rage.scene.*;
import ray.rml.*;

public class GhostAvatar{
	
	private UUID id;
	private SceneNode node;
	private Entity entity;
	private Vector3 position;
	
	public GhostAvatar(UUID id, Vector3 position){
		this.id = id;
		this.position = position;
	}
	
	public UUID getID(){
		return id;
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}
	
	public void setNode(SceneNode node){
		this.node = node;
	}
	
	public SceneNode getNode(){
		return node;
	}
	
	public void setPosition(Vector3 pos){
		position = pos;
		this.getNode().setLocalPosition(pos);
		//System.out.println("Set Position: " + pos.x() + ", " + pos.y() + ", " + pos.y());
	}
}