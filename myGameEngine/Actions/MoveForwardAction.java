package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;
import a3.MyGame;

import javax.script.ScriptEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;

import ray.rml.*;
import net.java.games.input.Event;


public class MoveForwardAction extends AbstractInputAction{
	
		private Node avN;
		private ProtocolClient protClient;
		private ScriptEngine engine;
		private MyGame game;
		private float prevTime;
	
		public MoveForwardAction(Node n, ProtocolClient p, ScriptEngine engine, MyGame game){
			avN = n;
			protClient = p;
			this.engine = engine;
			this.game = game;
			prevTime = 0;
		}
		
		public void performAction(float time, Event e){
			//System.out.println("Move forward");
			float vel = ((Double)(engine.get("moveForwardVelocity"))).floatValue();
			
			time = time / 1000;
			
			avN.moveForward(vel);
			game.updateVerticalPosition();
			
			if((time - prevTime) >= 1){
				game.doTheWalk();
				prevTime = time;
			}
			
			
			if(protClient != null)
				protClient.sendMoveMessage(avN.getWorldPosition());
			
		}
}