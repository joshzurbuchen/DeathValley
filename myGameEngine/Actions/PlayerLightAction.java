package myGameEngine.Actions;

import myGameEngine.Networking.ProtocolClient;
import a3.MyGame;

import javax.script.ScriptEngine;

import ray.rml.*;
import ray.rage.scene.*;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class PlayerLightAction extends AbstractInputAction {
	
    private MyGame game;

    public PlayerLightAction(MyGame game){ 
      
        this.game = game;
    }

    public void performAction(float time, Event e) {
        
		game.lightSwitch();
    }
}

