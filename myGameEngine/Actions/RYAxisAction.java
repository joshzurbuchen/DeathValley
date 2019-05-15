package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;

import javax.script.ScriptEngine;

import ray.input.action.AbstractInputAction;
import ray.rage.scene.*;

import ray.rml.*;
import net.java.games.input.Event;

public class RYAxisAction extends AbstractInputAction {
    private Node avN;
    private ProtocolClient protClient;
    private ScriptEngine engine;

    public RYAxisAction(Node n, ProtocolClient p, ScriptEngine engine) { 
        avN = n;
        protClient = p;
        this.engine = engine;
    }

    public void performAction(float time, Event e) {
        float vel = ((Double)(engine.get("rotate"))).floatValue();
        time = time / 1000;

        if(e.getValue() < -0.5) {
            avN.pitch(Degreef.createFrom(-vel));

        } else if(e.getValue() > 0.5) {
            avN.pitch(Degreef.createFrom(vel));
        }    
    }
}

