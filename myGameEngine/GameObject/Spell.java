package myGameEngine.GameObject;

import java.awt.*;
import java.io.IOException;

import ray.rage.scene.controllers.RotationController;
import ray.rage.scene.*;
import ray.rml.*;

public class Spell{
	
	private SceneManager sm;
	private SceneNode spellController;
	private SceneNode parent;
	private SceneNode childN1;
	private SceneNode childN2;
	private SceneNode childN3;
	
	private float dfp = 1.5f; //dfp = distant from parent
	
	private int ID;
	private Vector3 position;
	
	public Spell(SceneManager sm, SceneNode spells, int ghostSpellID, Vector3 ghostPosition){
		
		this.sm = sm;
		spellController = spells;
		ID = ghostSpellID;
		position = ghostPosition;
	}
	
	public void buildObj() throws IOException{
	
		//make the nodes for our object
		parent = spellController.createChildSceneNode("sParent" + ID);
		parent.setLocalPosition(position);
		
		childN1 = parent.createChildSceneNode("child1" + ID);
		childN2 = parent.createChildSceneNode("child2" + ID);
		childN3 = parent.createChildSceneNode("child3" + ID);
		
		Entity entity1E = sm.createEntity("entity1E" + ID, "sword.obj");
		Entity entity2E = sm.createEntity("entity2E" + ID, "sword.obj");
		Entity entity3E = sm.createEntity("entity3E" + ID, "sword.obj");
		
		//entitys to nodes
		childN1.attachObject(entity1E);
		childN1.moveForward(dfp);
		
		childN2.attachObject(entity2E);
		Angle rotAmt = Degreef.createFrom(120.0f);
		childN2.yaw(rotAmt);
		childN2.moveForward(dfp);
		
		childN3.attachObject(entity3E);
		Angle rotAmt2 = Degreef.createFrom(240.0f);
		childN3.yaw(rotAmt2);
		childN3.moveForward(dfp);
		
		RotationController rc = new RotationController(Vector3f.createUnitVectorY(), 0.1f);
		rc.addNode(parent);
        sm.addController(rc);
		
	}
	
	public void scale(float x, float y, float z){
		
		childN1.scale(x, y, z);
		childN2.scale(x, y, z);
		childN3.scale(x, y, z);
	}
	
	public void setLocation(Vector3 loc){
			
		parent.setLocalPosition(loc);
	}
	
	public SceneNode getParentNode(){
		return parent;
	}
}