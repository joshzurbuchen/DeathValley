package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;

import javax.script.ScriptEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;

import ray.rml.*;
import net.java.games.input.Event;


public class PitchUpAction extends AbstractInputAction{
	
		private Node avN;
		private ProtocolClient protClient;
		private ScriptEngine engine;
	
		public PitchUpAction(Node n, ProtocolClient p, ScriptEngine engine){
			avN = n;
			protClient = p;
			this.engine = engine;
		}
		
		public void performAction(float time, Event e){
			//System.out.println("Move forward");
			float vel = ((Double)(engine.get("rotate"))).floatValue();
			
			Angle rotAmt = Degreef.createFrom(-vel);
			avN.pitch(rotAmt);
			/*  need to implement rotate messages
			if(protClient != null)
				protClient.sendMoveMessage(avN.getWorldPosition());
			*/
		}
}