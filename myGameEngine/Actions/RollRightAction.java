package myGameEngine.Actions;

import ray.rml.*;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;

import myGameEngine.Networking.ProtocolClient;
import javax.script.ScriptEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class RollRightAction extends AbstractInputAction {
    private Node avN;
	private ProtocolClient protClient;
	private ScriptEngine engine;

    public RollRightAction(Node n, ProtocolClient p, ScriptEngine engine) { 
        avN = n;
		protClient = p;
        this.engine = engine;
    }

    public void performAction(float time, Event event) {
		float vel = ((Double)(engine.get("rotate"))).floatValue();
        avN.roll(Degreef.createFrom(vel));
    }
}