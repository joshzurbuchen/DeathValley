package myGameEngine.Networking;

import ray.rml.*;

public class NPC{
	double locX, locY, locZ; // other state info goes here (FSM)
	
	private boolean goingUp = true;
	private double yRate = 0.05;
	private double xRate = 0.1;
	private double zRate = 0.1;
	private Vector3 target;
	
	public NPC(){
			target = Vector3f.createFrom(0.0f, 0.0f, 0.0f);
	}
	
	public double getX() 
	{ return locX; }
	
	public void setX(double x){
		locX = x;
	}
	
	public double getY() 
	{ return locY; }
	
	public void setY(double y){
		locY = y;
	}
	
	public double getZ() 
	{ return locZ; }
	
	public void setZ(double z){
		locZ = z;
	}
	
	public void randomizeLocation(double x, double y, double z){
		locX = x;
		locY = y;
		locZ = z;
	}
	
	public Vector3 getTarget() {
		return target;
	}
	
	public void setTarget(Vector3 t) {
		target = t;
	}

	public void updateLocation(){
		//System.out.println("NPC " + locX + " " + locY + " " + locZ);
		float xDiff = 0, zDiff = 0;		
		
		try {
			xDiff = (float)locX - target.x();
			zDiff = (float)locZ - target.z();
		}catch(NullPointerException e){
			System.out.println("NPC.java: No players Found");
		}
		
		if(xDiff < -0.1 && xDiff > -0.1) {
		} else if(xDiff < -0.1) {
			locX += xRate;
		} else if (xDiff > 0.1) {
			locX -= xRate;
		}
		//System.out.println("NPC moved X: " + locZ);
		
		if(zDiff < -0.1 && zDiff > -0.1) {
			locZ = target.z();
		} else if(zDiff < -0.1) {
			locZ += zRate;
		} else if (zDiff > 0.1) {
			locZ -= zRate;
		}
		//System.out.println("NPC moved Z: " + locZ);
		
		
		if(locY > 12.5)
			goingUp = false;
		else if(locY < 10)
			goingUp = true;
		
		if(goingUp)
			locY += yRate;
		else
			locY -= yRate;
	}
		
}