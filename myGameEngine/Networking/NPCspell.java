package myGameEngine.Networking;

import ray.rml.*;

public class NPCspell{
	double locX, locY, locZ; // other state info goes here (FSM)
	
	private boolean goingUp = true;
	private double yRate = 0.05;
	private double xRate = 0.1;
	private double zRate = 0.1;
	private int ID;
	
	public NPCspell(int ID){
		this.ID = ID;
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
	
	public int getID(){
		return ID;
	}
	
	public void setLocation(double x, double y, double z){
		locX = x;
		locY = y;
		locZ = z;
	}
	
	public void updateLocation(){
	
		
		
	}
		
}