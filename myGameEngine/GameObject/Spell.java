package myGameEngine.GameObject;

import java.awt.*;
import java.io.IOException;

import ray.rage.scene.controllers.RotationController;
import ray.rage.scene.*;
import ray.rml.*;

public class Spell{
	
	private SceneManager sm;
	private SceneNode parent;
	private SceneNode childN1;
	private SceneNode childN2;
	private SceneNode childN3;
	
	private float dfp = 1.5f; //dfp = distant from parent
	
	
	public Spell(SceneManager sm){
		
		this.sm = sm;
	}
	
	public void buildObj() throws IOException{
	
		//make the nodes for our object
		parent = sm.getRootSceneNode().createChildSceneNode("parent");
		childN1 = parent.createChildSceneNode("child1");
		childN2 = parent.createChildSceneNode("child2");
		childN3 = parent.createChildSceneNode("child3");
		
		Entity entity1E = sm.createEntity("entity1E", "sphere.obj");
		Entity entity2E = sm.createEntity("entity2E", "sphere.obj");
		Entity entity3E = sm.createEntity("entity3E", "sphere.obj");
		
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
}