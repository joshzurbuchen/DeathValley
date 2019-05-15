package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;
import a3.MyGame;

import javax.script.ScriptEngine;

import ray.rml.*;
import ray.rage.scene.*;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class XAxisAction extends AbstractInputAction {
    private Node avN;
    private ProtocolClient protClient;
    private ScriptEngine engine;
    private MyGame game;
    private float prevTime;

    public XAxisAction(Node n, ProtocolClient p, ScriptEngine engine, MyGame game) { 
        avN = n;
        protClient = p;
        this.engine = engine;
        this.game = game;
        prevTime = 0;
    }

    public void performAction(float time, Event e) {
        time = time / 1000;

        if(e.getValue() < -0.5) {
            float vel = ((Double)(engine.get("moveLeftVelocity"))).floatValue();
            avN.moveLeft(vel);

            if((time - prevTime) >= 1){
                game.doTheWalk();
                prevTime = time;
            }
        } else if(e.getValue() > 0.5) {
            float vel = ((Double)(engine.get("moveRightVelocity"))).floatValue();
            avN.moveRight(vel);

            if((time - prevTime) >= 1){
                game.doTheWalk();
                prevTime = time;
            }
        }
        game.updateVerticalPosition();

		if(protClient != null) {
		    protClient.sendMoveMessage(avN.getWorldPosition());
        }
    }
}

