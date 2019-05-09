package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;
import a3.MyGame;

import javax.script.ScriptEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;

import ray.rml.*;
import net.java.games.input.Event;


public class MoveRightAction extends AbstractInputAction{
	
		private Node avN;
		private ProtocolClient protClient;
		private ScriptEngine engine;
		private MyGame game;
	
		public MoveRightAction(Node n, ProtocolClient p, ScriptEngine engine, MyGame game){
			avN = n;
			protClient = p;
			this.engine = engine;
			this.game = game;
		}
		
		public void performAction(float time, Event e){
			//System.out.println("Move forward");
			float vel = ((Double)(engine.get("moveRightVelocity"))).floatValue();
			
			avN.moveRight(vel);
			game.updateVerticalPosition();
			
			if(protClient != null)
				protClient.sendMoveMessage(avN.getWorldPosition());
		}
}