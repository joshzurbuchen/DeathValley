var JavaPackages = new JavaImporter(
	Packages.ray.rage.scene.SceneManager,
	Packages.ray.rage.scene,
	Packages.ray.rage.rendersystem.Renderable.Primitive,
	Packages.java.lang.RunTimeException,
	Packages.ray.rage.scene.generic.GenericSceneNode
);



//creates a RAGE object
with(JavaPackages){
	
	//parameter names refer to Rage entity, scene node, entity name, node name,
	//mesh = model
	function mkObj(sm, entity, node, eName, nName, mesh){
		
		entity = sm.createEntity(eName, mesh);
		entity.setPrimitive(Primitive.TRIANGLES); 
		
		node = sm.getRootSceneNode().createChildSceneNode(nName);
		node.attachObject(entity);
	}
	
	function updateWorldObjects(sm){
		
		var objectList = [];
		 
		 try{
			var treeE1 = sm.createEntity("treeE1", "lowPolyPineTreeblend.obj");
			treeE1.setPrimitive(Primitive.TRIANGLES); 
		
			var treeN1 = sm.getRootSceneNode().createChildSceneNode("treeN1");
			treeN1.attachObject(treeE1);
			
			objectList.push(treeN1);
			
			treeN1.scale(1.5, 2.5, 1.0);
			treeN1.setLocalPosition(5, 5, 5);	
		 }
		 catch(RunTimeException){}
		 objectList[0].scale(2, 2, 2);
		 
		 try{
			var treeE2 = sm.createEntity("treeE2", "lowPolyPineTreeblend.obj");
			treeE2.setPrimitive(Primitive.TRIANGLES); 
		
			var treeN2 = sm.getRootSceneNode().createChildSceneNode("treeN2");
			treeN2.attachObject(treeE2);
			
			treeN2.scale(1.5, 2.5, 1.0);
			treeN2.setLocalPosition(6, 0, 6);	
		 }
		 catch(RunTimeException){}
		
		
		var treeE3;
		var treeN3;
		try{
			treeE3 = sm.createEntity("treeE3", "lowPolyPineTreeblend.obj");
			treeE3.setPrimitive(Primitive.TRIANGLES); 
		
			treeN3 = sm.getRootSceneNode().createChildSceneNode("treeN3");
			treeN3.attachObject(treeE3);
			
			treeN3.scale(1.5, 11.5, 1.0);
			treeN3.setLocalPosition(3, 0, 3);	
		 }
		 catch(RunTimeException){}
		 
		
		
	}
}