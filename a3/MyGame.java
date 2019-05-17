package a3;

import myGameEngine.Networking.ProtocolClient;
import myGameEngine.Networking.GhostAvatar;
import myGameEngine.Networking.NetworkingServer;
import myGameEngine.Networking.GameServerUDP;
import myGameEngine.Networking.AvatarNear;
import myGameEngine.Networking.NPC;
import myGameEngine.Networking.NPCcontroller;
import myGameEngine.Networking.OneSecPassed;
import myGameEngine.Networking.NpcMove;
import myGameEngine.Networking.ThreeSecPassed;
import myGameEngine.Networking.NpcDrop;
import myGameEngine.GameObject.Spell;

import a3.DisplaySettingsDialog;


//setting up for every keyboard
import java.util.ArrayList;
import net.java.games.input.Controller;

/*
import myGameEngine.Actions.MoveForwardAction;
import myGameEngine.Actions.MoveBackwardAction;
import myGameEngine.Actions.MoveLeftAction;
import myGameEngine.Actions.MoveRightAction;
import myGameEngine.Actions.PitchUpAction;
import myGameEngine.Actions.PitchDownAction;
import myGameEngine.Actions.YawLeftAction;
import myGameEngine.Actions.YawRightAction;
*/
import myGameEngine.Networking.*;
import myGameEngine.Actions.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.GraphicsDevice;
import java.io.*;
import java.util.*;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Invocable;


import java.util.UUID;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.lang.RuntimeException;

import net.java.games.input.Event;


import ray.rage.*;
import ray.rage.asset.material.*;
import ray.rage.asset.texture.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.rendersystem.states.*;
import ray.rage.rendersystem.states.RenderState.Type;
import ray.rage.rendersystem.shader.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rage.asset.texture.*;
import ray.rage.util.*;

import ray.networking.IGameConnection.ProtocolType;

import ray.input.*;
import ray.input.action.*;
import ray.input.action.AbstractInputAction;


import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Version;
import net.java.games.input.Component;

import ray.rml.*;

import static ray.rage.scene.SkeletalEntity.EndType.*;

import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.physics.PhysicsEngineFactory;

import ray.audio.*;
import com.jogamp.openal.ALFactory;

public class MyGame extends VariableFrameRateGame {

	// to minimize variable allocation in update()
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, dispStr, deathStr;
	int elapsTimeSec;
	int deathCtr = 0;

	private SceneManager sm;
	//private SceneNode avatarN;
	//private SceneNode dolphinChildN;
	private SceneNode avatarN;
	private SceneNode avatarChildN;
	private SceneNode cameraN;
	private SceneNode tessN;
	private SceneNode treeN;
	private SceneNode spellController;
	
	private int gitTest;

	private Tessellation tessE;

	private Camera camera;

	private InputManager im = new GenericInputManager();

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected;
	private Vector<UUID> gameObjectsToRemove;

	private Random rand = new Random();


	//Sound
	IAudioManager audioMgr;
	Sound shipSound, hearSound;

	private PhysicsEngine physicsEng;

	private static final String HEIGHTMAP_NAME = "heightMap2.PNG";
	private static final String GROUND_TEXTURE = "greenHills.PNG";

	private static final String SKYBOX_NAME = "SkyBox";
	private boolean skyBoxVisible = true;

	private ScriptEngine jsEngine;
	private Invocable invocableEngine;
	private File scriptFile1;
	private File worldObjectFile;
	private long fileLastModifiedTime;
	private long worldObjectsFileLastModifiedTime;
	private	float up[] = {0,1,0};
	
	private DisplaySettingsDialog dSettings;

	private RotationController rc;
	
	private Light playerlight;
	private boolean lightOn = false;

    public MyGame(String serverAddr, int sPort) {
        super();
		serverAddress = serverAddr;
		serverPort = sPort;
		serverProtocol = ProtocolType.UDP; //TCP

		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");
    }

    public static void main(String[] args) {
        MyGame game = new MyGame(args[0], Integer.parseInt(args[1])); //, args[2]);

		ScriptEngineManager factory = new ScriptEngineManager();
		String scriptFileName = "hello.js";

		//get a list of the script engines on this platform
		List<ScriptEngineFactory> list = factory.getEngineFactories();

		System.out.println("Script Engine Factories found:");
		for(ScriptEngineFactory f:list){

			System.out.println(" Name = " + f.getEngineName()
				+ " language = " + f.getLanguageName()
				+ " extensions = " + f.getExtensions());
		}

		//get the JavaScript engine
		ScriptEngine jsEngine = factory.getEngineByName("js");

		//run the script
		game.executeScript(jsEngine, scriptFileName);

        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            game.shutdown();
            game.exit();
        }
    }

	private void executeScript(ScriptEngine engine, String scriptFileName){

		try{
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader); //execute the script statements in the file
			fileReader.close();
		}
		catch(FileNotFoundException e1){
			System.out.println(scriptFileName + " not found " + e1);
		}
		catch(IOException e2){
			System.out.println("IO problem with " + scriptFileName + e2);
		}
		catch(ScriptException e3){
			System.out.println("ScriptException in " + scriptFileName + e3);
		}
		catch(NullPointerException e4){
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
	}

	private void runScript(File script){

		try{
			FileReader fileReader = new FileReader(script);
			jsEngine.eval(fileReader); //execute the script statements in the file
			fileReader.close();
		}
		catch(FileNotFoundException e1){
			System.out.println(script + " not found " + e1);
		}
		catch(IOException e2){
			System.out.println("IO problem with " + script + e2);
		}
		catch(ScriptException e3){
			System.out.println("ScriptException in " + script + e3);
		}
		catch(NullPointerException e4){
			System.out.println("Null ptr exception in " + script + e4);
		}
	}


	public void setIsConnected(boolean connected){
			isClientConnected = connected;
	}

	private void setupNetworking(){

		gameObjectsToRemove = new Vector<UUID>();
		isClientConnected = false;

		try{
			System.out.println("Attempting to get ProtocolClient"); //added for debugging
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		if(protClient == null)
			System.out.println("missing protocol host");
		else
			protClient.sendJoinMessage();
			
		setupInputs();
	}

	protected void setupInputs(){

		String gpName = im.getFirstGamepadName();
		//String kbName = im.getKeyboardName();
		ArrayList<Controller> controllers = im.getControllers();


		Action moveForward = new MoveForwardAction(avatarN, protClient, jsEngine, this);
		Action moveBackward = new MoveBackwardAction(avatarN, protClient, jsEngine, this);
		Action moveLeft = new MoveLeftAction(avatarN, protClient, jsEngine, this);
		Action moveRight = new MoveRightAction(avatarN, protClient, jsEngine, this);
		Action pitchUp = new PitchUpAction(avatarChildN, protClient, jsEngine);
		Action pitchDown = new PitchDownAction(avatarChildN, protClient, jsEngine);
		Action yawLeft = new YawLeftAction(avatarN, protClient, jsEngine);
		Action yawRight = new YawRightAction(avatarN, protClient, jsEngine);
		Action quit = new SendCloseConnectionPacketAction();//protClient, isClientConnected);
		Action light = new PlayerLightAction(this);
		
		Action xAxis = new XAxisAction(avatarN, protClient, jsEngine, this);
		Action yAxis = new YAxisAction(avatarN, protClient, jsEngine, this);
		Action rXAxis = new RXAxisAction(avatarN, protClient, jsEngine);
		Action rYAxis = new RYAxisAction(avatarChildN, protClient, jsEngine);

		for(Controller c : controllers){
			if(c.getType() == Controller.Type.KEYBOARD){
				//keyboard associations
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.P, quit, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.W, moveForward, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.S, moveBackward, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.A, moveLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.D, moveRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.UP, pitchUp, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.DOWN,	pitchDown, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.LEFT,	yawLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.RIGHT, yawRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Key.L, light, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			} else if(c.getType() == Controller.Type.GAMEPAD) {
				//Gamepad associations
				im.associateAction(c, net.java.games.input.Component.Identifier.Axis.X, xAxis, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Axis.Y, yAxis, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        		im.associateAction(c, net.java.games.input.Component.Identifier.Axis.RX, rXAxis, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        		im.associateAction(c, net.java.games.input.Component.Identifier.Axis.RY, rYAxis, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(c, net.java.games.input.Component.Identifier.Button._7, quit, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
				im.associateAction(c, net.java.games.input.Component.Identifier.Button._1, light, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

				//im.associateAction(c, net.java.games.input.Component.Identifier.Button._4, yawLeftAction2, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				//im.associateAction(c, net.java.games.input.Component.Identifier.Button._5, yawRightAction2, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			}
		}


		/*
		//Gamepad associations
		im.associateAction(gpName,
			net.java.games.input.Component.Identifier.Button._1,
			moveForward, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		*/
	}

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) {
		
		dSettings = new DisplaySettingsDialog(ge.getDefaultScreenDevice());
		
		dSettings.showIt();
		dSettings.getSelectedDisplayMode();
		rs.createRenderWindow(dSettings.getSelectedDisplayMode(), 
					dSettings.isFullScreenModeSelected());
		//rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
		
	}

    @Override
    protected void setupCameras(SceneManager sm, RenderWindow rw) {
        SceneNode rootNode = sm.getRootSceneNode();
        camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
        rw.getViewport(0).setCamera(camera);

		camera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));

		camera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));

        SceneNode cameraNode = rootNode.createChildSceneNode(camera.getName() + "Node");
        cameraNode.attachObject(camera);
		camera.setMode('n');
    }

	protected void setupAvatar(Engine eng, SceneManager sm) throws IOException {

		SkeletalEntity avatarE = sm.createSkeletalEntity("avatar", "MrPolygonSkelModel.rkm","MrPolygonSkel.rks");
		Texture tex = sm.getTextureManager().getAssetByPath("MrPolygonTexture.png");
		TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		tstate.setTexture(tex);
		avatarE.setRenderState(tstate);

		//attach the entity to a scene node
		avatarN = sm.getRootSceneNode().createChildSceneNode("avatarN");
		avatarN.scale(0.5f, 0.5f, 0.5f);
		avatarN.moveBackward(2.0f);
		avatarN.attachObject(avatarE);
		
		//attach a light to the avatar
		SceneNode avatarLightN = avatarN.createChildSceneNode(avatarN.getName() + "lightNode");
		playerlight = sm.createLight("avatarLamp", Light.Type.POINT);
		avatarLight();
		//avatarLightN.moveForward(1.0f);
		avatarLightN.moveUp(1.0f);
        playerlight.setRange(5f);

        avatarLightN.attachObject(playerlight);

		//attach camera to avatar
		avatarChildN = avatarN.createChildSceneNode(avatarE.getName() + "Node");
		avatarChildN.moveBackward(5.0f);
		avatarChildN.moveUp(6.0f);
		avatarChildN.attachObject(camera);

		//load animations
		avatarE.loadAnimation("walkAnimation","MrPolygonWalk.rka");
	}
	
	public void avatarLight(){
		
		if(lightOn){
			playerlight.setAmbient(new Color(0.0f, 0.0f, 0.0f));
			playerlight.setDiffuse(new Color(0.0f, 0.0f, 1.0f));
			playerlight.setSpecular(new Color(0.0f, 0.0f, 1.0f));
		}
		else{
			playerlight.setAmbient(new Color(0.0f, 0.0f, 0.0f));
			playerlight.setDiffuse(new Color(0.0f, 0.0f, 0.0f));
			playerlight.setSpecular(new Color(0.0f, 0.0f, 0.0f));	
		}
      
	}
	
	public void lightSwitch(){
		lightOn = !lightOn;
	}

	public void doTheWalk(){
		SkeletalEntity avatarE = (SkeletalEntity) sm.getEntity("avatar");

		avatarE.stopAnimation();
		avatarE.playAnimation("walkAnimation", 1.0f, LOOP, 1);
	}

    @Override
    protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		this.sm = sm;

		TextureManager tm = eng.getTextureManager();
		RenderSystem rs = sm.getRenderSystem();

		//******Set up sky box
		setupSkybox(eng);

		//******Set up avatar
		setupAvatar(eng, sm);

		//******spell testing
		//Spell spell = new Spell(sm);
		//spell.buildObj();

		//******add tree


	//******Lighting
        sm.getAmbientLight().setIntensity(new Color(.5f, .5f, .5f));
		spellController = sm.getRootSceneNode().createChildSceneNode("spellController");

		Light plight = sm.createLight("testLamp10", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
        plight.setDiffuse(new Color(.7f, .7f, .7f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        plight.setRange(5f);

		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
        plightNode.attachObject(plight);

		//prepare the script engine
		ScriptEngineManager factory = new ScriptEngineManager();
		List<ScriptEngineFactory> list = factory.getEngineFactories();
		//ScriptEngine jsEngine = factory.getEngineByName("js");

		//use spin speed setting from the first script to initialize dolphin rotation
		scriptFile1 = new File("initParams.js");
		this.runScript(scriptFile1);
		fileLastModifiedTime = scriptFile1.lastModified();



		worldObjectFile = new File("worldObjects.js");
		//this.runScript(worldObjectFile);
		worldObjectsFileLastModifiedTime = worldObjectFile.lastModified();

		invocableEngine = (Invocable) jsEngine;

		try{
			invocableEngine.invokeFunction("updateWorldObjects", sm);
		}
		catch(ScriptException e1){
			System.out.println("ScriptException in " + worldObjectFile + e1);
		}
		catch(NoSuchMethodException e2){
			System.out.println("No such method in " + worldObjectFile + e2);
		}
		catch(NullPointerException e3){
			System.out.println("Null ptr exception reading " + worldObjectFile + e3);
		}


        //rc = new RotationController(Vector3f.createUnitVectorY(),
								//((Double)(jsEngine.get("spinSpeed"))).floatValue());
        //rc.addNode(avatarN);
        //sm.addController(rc);

		//Physics
		initPhysicsSystem();
		setupNetworking();

		setupInputs();
		setUpTerrain();
		
		//ask for tree info if protocol isn't null
		setupTrees();
		createGroundPO(1, sm.getRootSceneNode().getWorldPosition());

		initAudio(sm);
    }
	
	public void setupTrees(){
		if(protClient == null)
			System.out.println("missing protocol host can't get tree info");
		else{
			System.out.println("MyGame sending request for tree info");
			protClient.sendTreeRequest();
		}
	}

	protected void setupSkybox(Engine eng) throws IOException{

		Configuration conf = eng.getConfiguration();
		TextureManager tm = getEngine().getTextureManager();
		tm.setBaseDirectoryPath(conf.valueOf("assets.skyboxes.mypath"));
		Texture front = tm.getAssetByPath("front.jpg");
		Texture back = tm.getAssetByPath("back.jpg");
		Texture left = tm.getAssetByPath("left.jpg");
		Texture right = tm.getAssetByPath("right.jpg");
		Texture top = tm.getAssetByPath("top.jpg");
		Texture bottom = tm.getAssetByPath("bottom.jpg");
		tm.setBaseDirectoryPath(conf.valueOf("assets.textures.path"));


		AffineTransform xform = new AffineTransform();
		xform.translate(0, front.getImage().getHeight());
		xform.scale(1d, -1d);

		front.transform(xform);
		back.transform(xform);
		left.transform(xform);
		right.transform(xform);
		top.transform(xform);
		bottom.transform(xform);

		SkyBox sb = sm.createSkyBox(SKYBOX_NAME);
		sb.setTexture(front, SkyBox.Face.FRONT);
		sb.setTexture(back, SkyBox.Face.BACK);
		sb.setTexture(left, SkyBox.Face.LEFT);
		sb.setTexture(right, SkyBox.Face.RIGHT);
		sb.setTexture(top, SkyBox.Face.TOP);
		sb.setTexture(bottom, SkyBox.Face.BOTTOM);
		sm.setActiveSkyBox(sb);
	}

	protected void setUpTerrain(){

		tessE = sm.createTessellation("tessE",6);

		//subdivisions per patch min=0, try up to 32
		tessE.setSubdivisions(0f);
		tessE.setQuality(9);

		tessN = sm.getRootSceneNode().createChildSceneNode("TessN");
		tessN.attachObject(tessE);

		//to move it, note that x and z must BOTH be positive OR negative
		//tessN.translate(Vector3f.createFrom(-6.2f, -2.2f, 2.7f));
		//tessN.yaw(Degreef.createFrom(37.2f));

		tessN.scale(1000, 10000, 1000);
		tessE.setHeightMap(this.getEngine(), HEIGHTMAP_NAME);
		tessE.setTexture(this.getEngine(), GROUND_TEXTURE);
		//tessE.setNormalMap(...);

		/*
		RenderSystem rs = sm.getRenderSystem();
		ZBufferState zstate = (ZBufferState) rs.createRenderState(RenderState.Type.ZBUFFER);
		zstate.setTestEnabled(true);
		tessE.setRenderState(zstate);
		*/

	}

	public void updateVerticalPosition(){

		//Figure out Avatar's position relative to plane
		Vector3 worldAvatarPosition = avatarN.getWorldPosition();
		Vector3 localAvatarPosition = avatarN.getLocalPosition();

		//use avatar World coordinates to get coordinates for heightMap
		Vector3 newAvatarPosition = Vector3f.createFrom(

			//keep the x coordinates
			localAvatarPosition.x(),

			//the Y coordinate is the varying heightMap
			tessE.getWorldHeight(worldAvatarPosition.x(), worldAvatarPosition.z()),

			//keep the z coordinate
			localAvatarPosition.z()
		);

		//use avatar Local coordinates to set position, including heightMap
		avatarN.setLocalPosition(newAvatarPosition);
	}

	public void updateVerticalPosition(SceneNode node){

		Vector3 worldNodePosition = node.getWorldPosition();
		Vector3 localNodePosition = node.getLocalPosition();

		//use avatar World coordinates to get coordinates for heightMap
		Vector3 newNodePosition = Vector3f.createFrom(

			//keep the x coordinates
			localNodePosition.x(),

			//the Y coordinate is the varying heightMap
			tessE.getWorldHeight(worldNodePosition.x(), worldNodePosition.z()) + 0.25f,

			//keep the z coordinate
			localNodePosition.z()
		);

		//use avatar Local coordinates to set position, including heightMap
		node.setLocalPosition(newNodePosition);
	}

    @Override
    protected void update(Engine engine) {
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		float time = engine.getElapsedTimeMillis();

		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		deathStr = Integer.toString(deathCtr);
		dispStr = "Time = " + elapsTimeStr + " Deaths = " + deathStr;
		rs.setHUD(dispStr, 15, 15);

		//run script again in update() to demonstrate dynamic modification
		long modTime = scriptFile1.lastModified();
		if(modTime > fileLastModifiedTime){
			fileLastModifiedTime = modTime;
			this.runScript(scriptFile1);
			rc.setSpeed(((Double)(jsEngine.get("spinSpeed"))).floatValue());
		}

		long wmodTime= worldObjectFile.lastModified();
		if( wmodTime > worldObjectsFileLastModifiedTime){
			worldObjectsFileLastModifiedTime = wmodTime;
			this.runScript(worldObjectFile);

			try{
				invocableEngine.invokeFunction("updateWorldObjects", sm);
			}
			catch(ScriptException e1){
				System.out.println("ScriptException in " + worldObjectFile + e1);
			}
			catch(NoSuchMethodException e2){
				System.out.println("No such method in " + worldObjectFile + e2);
			}
			catch(NullPointerException e3){
				System.out.println("Null ptr exception reading " + worldObjectFile + e3);
			}

		}

		/***Physics***/
		Matrix4 mat;
		physicsEng.update(time);
		for(SceneNode s: engine.getSceneManager().getSceneNodes()) {
			if(s.getPhysicsObject() != null) {
				mat = Matrix4f.createFrom(toFloatArray(s.getPhysicsObject().getTransform()));
				s.setLocalPosition(mat.value(0,3), mat.value(1,3), mat.value(2,3));
			}
		}


		//System.out.println(isClientConnected);
		processNetworking(elapsTime);
		im.update(elapsTime);

		//update animations
		SkeletalEntity avatarE = (SkeletalEntity) sm.getEntity("avatar");
		avatarE.update();

		/***Sound***/
		if(sm.hasSceneNode("ghostNPCN")) {
			shipSound.setLocation(sm.getSceneNode("ghostNPCN").getWorldPosition());
			//Vector3 temp = shipSound.getLocation();
			//System.out.print("Sound Pos: " + temp.x() + ", " + temp.y() + ", " +  temp.z());
		}
		
		if(sm.hasSceneNode("ghostNPCN")) {
			SceneNode npcN = sm.getSceneNode("ghostNPCN");
			shipSound.setLocation(npcN.getWorldPosition());
			//System.out.println("ghostNPC Sound Here---------------------");
		}
		
		setEarParameters(sm);
		avatarLight();

		checkSpellCollision();
	}

	protected void processNetworking(float elapsTime){

		if(protClient != null){
			protClient.processPackets();
			//System.out.println("MyGame askingForNPCinfo");
			protClient.askForNPCinfo();
			//protClient.askForNPCpos();
			
		}

		Iterator<UUID> it = gameObjectsToRemove.iterator();
		while(it.hasNext())
			sm.destroySceneNode(it.next().toString());

		gameObjectsToRemove.clear();
	}

	public Vector3 getPlayerPosition(){
		return avatarN.getWorldPosition();
	}

	public void addGhostAvatarToGameWorld(GhostAvatar avatar) throws IOException{
		if(avatar != null){
			System.out.println("Creating ghost avatar");
			Entity ghostE = sm.createEntity("ghost", "sphere.obj");
			ghostE.setPrimitive(Primitive.TRIANGLES);
			SceneNode ghostN = sm.getRootSceneNode().createChildSceneNode(avatar.getID().toString());
			ghostN.attachObject(ghostE);
			//ghostN.setLocalPosition(0, 0 , -4); //these hardcoded numbers need some enumeration later
			avatar.setNode(ghostN);
			avatar.setEntity(ghostE);
			//avatar.setPosition(); sample says this could be redundent. Leaving it commented out for now
		}
	}

	public void addGhostNPCtoGameWorld(GhostNPC npc)throws IOException{
		//System.out.println("MyGame creating NPC in game world");
		if(npc != null){
			try{
				//System.out.println("Creating ghost npc");
				Entity ghostNPCE = sm.createEntity("ghostNPC", "cube.obj");
				ghostNPCE.setPrimitive(Primitive.TRIANGLES);
				SceneNode ghostNPCN = sm.getRootSceneNode().createChildSceneNode("ghostNPCN");
				ghostNPCN.attachObject(ghostNPCE);
				ghostNPCN.setLocalPosition(0, 0, 0); //these hardcoded numbers need some enumeration later
				npc.setNode(ghostNPCN);
				npc.setEntity(ghostNPCE);
				//avatar.setPosition(); sample says this could be redundent. Leaving it commented out for now
				
				SceneNode lightNode = ghostNPCN.createChildSceneNode("lightNode");
				
				Light slight = sm.createLight("NPCSpotLight", Light.Type.SPOT);
				slight.setAmbient(new Color(15, 0, 0));
				slight.setDiffuse(java.awt.Color.red);
				slight.setSpecular(java.awt.Color.red);
				slight.setRange(20f);
				Angle rotAmt = Degreef.createFrom(90.0f);
				Angle coneCut = Degreef.createFrom(11.25f);
				lightNode.pitch(rotAmt);
				slight.setConeCutoffAngle(coneCut);
				slight.setConstantAttenuation(0.001f);
				lightNode.attachObject(slight);
				
				
			}catch(RuntimeException r){}
		}
	}
	
	public void addGhostTreetoGameWorld(int id, Vector3 position){
		float mass = 100.0f;
		float[] halfExtents = {1.0f, 0.3f, 0.3f};

		try{
			Entity treeE = sm.createEntity("tree" + id, "lowPolyPineTreeblend.obj");
			treeE.setPrimitive(Primitive.TRIANGLES);
			SceneNode treeN = sm.getRootSceneNode().createChildSceneNode("treeNode" + id);
			treeN.attachObject(treeE);
			treeN.setLocalPosition(position);
			updateVerticalPosition(treeN);
			treeN.scale(2.0f, 2.0f, 2.0f);
			
			if(treeN.getLocalPosition().y() <= .5) {
				treeN.setLocalPosition(treeN.getLocalPosition().add(0.0f,5.0f,0.0f));
				double[] temptf = toDoubleArray(treeN.getLocalTransform().toFloatArray());
				PhysicsObject treePO = physicsEng.addCylinderObject(physicsEng.nextUID(), mass, temptf, halfExtents);
				treePO.setBounciness(0.0f);
				treeN.setPhysicsObject(treePO);
			}
			//createGroundPO(id, position);
			//avatar.setPosition(); sample says this could be redundent. Leaving it commented out for now
		}catch(IOException e){}
	}


	/*****************************Sword Spell and basic collision*****************************/
	public void addGhostSpell(int ghostSpellID, Vector3 ghostPosition) throws IOException{
		
		float mass = 10.0f;
		float[] halfExtents = {1.0f, 0.3f, 0.3f};
		
		Spell spell = new Spell(sm, spellController, ghostSpellID, ghostPosition);
		spell.buildObj();
		
		double[] temptf = toDoubleArray(spell.getParentNode().getLocalTransform().toFloatArray());
		PhysicsObject spellPO = physicsEng.addCylinderObject(physicsEng.nextUID(), mass, temptf, halfExtents);
		spellPO.setBounciness(0.0f);
		spell.getParentNode().setPhysicsObject(spellPO);

		//herehere
	}

	public void checkSpellCollision() {
		Iterable<Node> i = spellController.getChildNodes();

		for(Node n: i) {
			float dist = distTo(avatarN.getWorldPosition(), n.getWorldPosition());
			if(dist < 2.0f) {
				deathCtr++;
				avatarN.setLocalPosition(rand.nextFloat() * 10.0f, 0.0f, rand.nextFloat() * 10.0f);
			}
		}
	}

	public void createGroundPO(int id, Vector3 position) {

		//Entity gndE = sm.createEntity("gnd" + id, "cube.obj");
		SceneNode gndN = sm.getRootSceneNode().createChildSceneNode("gndN" + id);
		//gndN.attachObject(gndE);
		gndN.setLocalPosition(position);
		//updateVerticalPosition(gndN); //Set this if you want to tress to fly away

		double[] temptf = toDoubleArray(gndN.getLocalTransform().toFloatArray());
		PhysicsObject gndPO = physicsEng.addStaticPlaneObject(physicsEng.nextUID(), temptf, up, 0.0f);

		gndPO.setBounciness(0.0f);
		gndN.scale(.5f, .05f, .5f);
		
		gndN.setPhysicsObject(gndPO);

	}

	public void removeGhostAvatarFromGameWorld(GhostAvatar avatar){
		if(avatar != null)
			gameObjectsToRemove.add(avatar.getID());
	}

	private class SendCloseConnectionPacketAction extends AbstractInputAction{

		public void performAction(float time, Event e){
			if(protClient != null && isClientConnected == true){
				System.out.println("Bye");
				protClient.sendByeMessage();
			}
			exit();
		}
	}

    public static float distTo(Vector3 p1, Vector3 p2) {
        return (float) Math.sqrt(Math.pow(p1.x() - p2.x(), 2) + Math.pow(p1.y() - p2.y(), 2) + Math.pow(p1.z() - p2.z(), 2));
    }

/*****************HERE BE PHYSICS*****************/
	private void initPhysicsSystem() {
		String engine = "ray.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0,-3.0f,0};

		physicsEng = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEng.initSystem();
		physicsEng.setGravity(gravity);
	}

	private float[] toFloatArray(double[] arr) {
		if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (float)arr[i];
		}
		
		return ret;
	}

	private double[] toDoubleArray(float[] arr) { 
		if (arr == null) return null;
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (double)arr[i];
		}
	
		return ret;
	}

/**********************Sound***********************/
	public void initAudio(SceneManager sm) {
		AudioResource resource1, resource2;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");

		if(!audioMgr.initialize()) {
			System.out.print("Audio Manager failed to initialize!");
			return;
		}

		resource1 = audioMgr.createAudioResource("ship-sound-MONO.wav", AudioResourceType.AUDIO_SAMPLE);
		shipSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);

		shipSound.initialize(audioMgr);

		if(!shipSound.getIsSoundValid()) {
			System.out.println("MyGame.java: ShipSound not Valid!");
		}

		if(resource1.getIsLoaded()) {
			System.out.println(resource1.getFileName() + " Loaded!");
		} else {
			System.out.println("File not Loaded");
		}

		try {
			shipSound.setMaxDistance(10.0f);
			shipSound.setMinDistance(0.5f);
			shipSound.setRollOff(5.0f);
		} catch(NullPointerException e) {
			System.out.println("MyGame.java: shipSound issues!");
		}

		if(sm.hasSceneNode("ghostNPCN")) {
			SceneNode npcN = sm.getSceneNode("ghostNPCN");
			shipSound.setLocation(npcN.getWorldPosition());
			System.out.println("ghostNPC Sound Here---------------------");
		}
		
		setEarParameters(sm);
		shipSound.play();
	}

	public void setEarParameters(SceneManager sm) {
		//SceneNode avatarN = sm.getSceneNode("avatarN");
		Vector3 avDir = avatarN.getWorldForwardAxis();

		audioMgr.getEar().setLocation(avatarN.getWorldPosition());
		audioMgr.getEar().setOrientation(avDir, Vector3f.createFrom(0,1,0));
	}

}
