package myGameEngine.Networking;

import ray.rage.scene.*;
import ray.rml.*;

public class GhostNPC{
	private int id;
	private SceneNode node;
	private Entity entity;
	private Vector3 position;
 
	public GhostNPC(int id, Vector3 position) // constructor
	{ 	this.id = id;
		this.position = position;
	}
	
	public void setPosition(Vector3 position)
	{ 	node.setLocalPosition(position); }
	 
	public Vector3 getPosition()
	{ 	return node.getLocalPosition(); }
	 
	public void setNode(SceneNode node){
		this.node = node; 
	}
	
	public SceneNode getNode(){
		return node;
	}
	
	public void setEntity(Entity entity){
		this.entity = entity;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public int getId(){
		return id;
	}
}