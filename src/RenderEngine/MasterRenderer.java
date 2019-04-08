// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.*;

import Loaders.Loader;
import Materials.Material;
import Models.RawModel;
import Models.TexturedModel;
import SceneNodes.CameraBase;
import SceneNodes.DrawableSceneNode;
import SceneNodes.Light;
import Shaders.Shader;
import Shaders.ShaderScreenDrawer;

public class MasterRenderer extends Renderer
{
	private Map<Shader, Map<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>>> sceneNodes = new HashMap<Shader, Map<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>>>();
	private Map<DrawableSceneNode, TexturedModel> sceneNodesBank = new HashMap<DrawableSceneNode, TexturedModel>();
	
	protected List<Matrix4f> lightsTransformation = new ArrayList<Matrix4f>();
	protected List<Vector3f> lightsPosition = new ArrayList<Vector3f>();
	protected List<Vector3f> lightsColor = new ArrayList<Vector3f>();
	protected List<Vector3f> lightsAttenuation = new ArrayList<Vector3f>();
	protected List<Integer> lightsDepthBuffers = new ArrayList<Integer>();
	
	public RawModel squareRawModel;
	public Shader screenDrawerShader;
	
	public List<Matrix4f> GetLightsTransformation()
	{
		return this.lightsTransformation;
	}
	
	public List<Vector3f> GetLightsPosition()
	{
		return this.lightsPosition;
	}
	
	public List<Vector3f> GetLightsColor()
	{
		return this.lightsColor;
	}
	
	public List<Vector3f> GetLightsAttenuation()
	{
		return this.lightsAttenuation;
	}
	
	public List<Integer> GetLightsDepthBuffers()
	{
		return this.lightsDepthBuffers;
	}
	
	public MasterRenderer()
	{
		int vaoID = Loader.CreateVAO();
		int[] vboID = new int[2];
		
		int[] indicesArray = { //
				0, 1, 2, 0, 2, 3 };
		float[] verticesArray = { //
				-1, -1, //
				1, -1, //
				1, 1, //
				-1, 1 };
		
		vboID[0] = Loader.BindIndicesBuffer( indicesArray );
		vboID[1] = Loader.StoreDataInAttributeList( 0, 2, verticesArray );
		Loader.UnbindVAO();
		
		int[] materialOffsets = { 0 };
		
		this.squareRawModel = new RawModel( vaoID, vboID, materialOffsets, indicesArray.length );
		this.screenDrawerShader = new ShaderScreenDrawer();
	}
	
	private void Add( Shader shader, Material material, RawModel model, Integer id, DrawableSceneNode sceneNode )
	{
		Map<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>> recurence1 = this.sceneNodes.get( shader );
		if( recurence1 == null )
		{
			recurence1 = new HashMap<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>>();
			sceneNodes.put( shader, recurence1 );
		}
		
		Map<Material, Map<Integer, Set<DrawableSceneNode>>> recurence2 = recurence1.get( model );
		if( recurence2 == null )
		{
			recurence2 = new HashMap<Material, Map<Integer, Set<DrawableSceneNode>>>();
			recurence1.put( model, recurence2 );
		}
		
		Map<Integer, Set<DrawableSceneNode>> recurence3 = recurence2.get( material );
		if( recurence3 == null )
		{
			recurence3 = new HashMap<Integer, Set<DrawableSceneNode>>();
			recurence2.put( material, recurence3 );
		}
		
		Set<DrawableSceneNode> recurence4 = recurence3.get( id );
		if( recurence4 == null )
		{
			recurence4 = new HashSet<DrawableSceneNode>();
			recurence3.put( id, recurence4 );
		}
		
		recurence4.add( sceneNode );
	}
	
	private void Remove( Shader shader, Material material, RawModel model, Integer id, DrawableSceneNode sceneNode )
	{
		Map<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>> recurence1 = this.sceneNodes.get( shader );
		if( recurence1 == null )
			return;
		
		Map<Material, Map<Integer, Set<DrawableSceneNode>>> recurence2 = recurence1.get( model );
		if( recurence2 == null )
			return;
		
		Map<Integer, Set<DrawableSceneNode>> recurence3 = recurence2.get( material );
		if( recurence3 == null )
			return;
		
		Set<DrawableSceneNode> recurence4 = recurence3.get( id );
		if( recurence4 == null )
			return;
		
		recurence4.remove( sceneNode );
		
		if( recurence4.isEmpty() )
		{
			recurence3.remove( id );
			
			if( recurence3.isEmpty() )
			{
				recurence2.remove( material );
				
				if( recurence2.isEmpty() )
				{
					recurence1.remove( model );
					
					if( recurence1.isEmpty() )
					{
						this.sceneNodes.remove( shader );
					}
				}
			}
		}
	}
	
	public void AddSceneNode( DrawableSceneNode sceneNode )
	{
		this.RemoveSceneNode( sceneNode );
		
		TexturedModel texModel = sceneNode.GetModel();
		RawModel model = texModel.GetRawModel();
		
		for( int i = 0; i < model.GetMaterialsCount(); ++i )
		{
			Material material = texModel.GetMaterialSet().get( i );
			Shader shader = material.GetShader();
			this.Add( shader, material, model, i, sceneNode );
		}
		
		this.sceneNodesBank.put( sceneNode, texModel );
	}
	
	public void RemoveSceneNode( DrawableSceneNode sceneNode )
	{
		TexturedModel texModel = this.sceneNodesBank.get( sceneNode );
		if( texModel != null )
		{
			RawModel model = texModel.GetRawModel();
			
			for( int i = 0; i < model.GetMaterialsCount(); ++i )
			{
				Material material = texModel.GetMaterialSet().get( i );
				Shader shader = material.GetShader();
				this.Remove( shader, material, model, i, sceneNode );
			}
			
			this.sceneNodesBank.remove( sceneNode );
		}
	}
	
	public void Render( CameraBase camera )
	{
		// Update DrawableSceneNodes world transformation matrices:
		for( DrawableSceneNode sceneNode : this.sceneNodesBank.keySet() )
			sceneNode.UpdateWorldTransformationMatrix();
		
		// Draw shadows:
		
		// Draw scene:
		this.RenderScene( camera );
		
		// Draw shadows and lights as post process:
		this.DrawLightsToScreen();
		// Draw other post processes:
		
		// Draw GUI:
		
		// Update display manager:
		DisplayManager.Update();
	}
	
	private void RenderScene( CameraBase camera )
	{
		super.Prepare( camera );
		
		boolean generatingShadows = false;
		if( camera instanceof Light )
			generatingShadows = true;
		
		Material material;
		Shader shader;
		RawModel model;
		Integer materialId;
		
		for( Map.Entry<Shader, Map<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>>> entry1 : this.sceneNodes.entrySet() )
		{
			shader = entry1.getKey();
			shader.Start( generatingShadows );
			
			for( Map.Entry<RawModel, Map<Material, Map<Integer, Set<DrawableSceneNode>>>> entry2 : entry1.getValue().entrySet() )
			{
				model = entry2.getKey();
				model.Bind();
				
				for( Map.Entry<Material, Map<Integer, Set<DrawableSceneNode>>> entry3 : entry2.getValue().entrySet() )
				{
					material = entry3.getKey();
					if( material.HasTransparency() )
						GL11.glDisable( GL11.GL_CULL_FACE );
					else
						GL11.glEnable( GL11.GL_CULL_FACE );
					
					for( Map.Entry<Integer, Set<DrawableSceneNode>> entry4 : entry3.getValue().entrySet() )
					{
						materialId = entry4.getKey();
						
						for( DrawableSceneNode sceneNode : entry4.getValue() )
						{
							if( sceneNode.IsEnabled() )
							{
								shader.SetUniforms( sceneNode, this, material );
								GL11.glDrawElements( GL11.GL_TRIANGLES, model.GetMaterialIndexCount( materialId ), GL11.GL_UNSIGNED_INT, model.GetMaterialIndexOffset( materialId ) );
							}
						}
					}
				}
				
				model.Unbind();
			}
			
			shader.Stop();
		}
		
		camera.GetFrameBuffer().Unbind();
	}
	
	private void DrawLightsToScreen()
	{
		this.camera.GetFrameBuffer().Unbind();
		GL11.glViewport( 0, 0, Display.getWidth(), Display.getHeight() );
		
		GL11.glDisable( GL11.GL_ALPHA );
		GL11.glDisable( GL11.GL_DEPTH );
		GL11.glDisable( GL11.GL_DEPTH_TEST );
		GL11.glDisable( GL11.GL_CULL_FACE );
		GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
		
		this.squareRawModel.Bind();
		this.screenDrawerShader.Start( false );
		this.screenDrawerShader.SetUniforms( null, this, null );
		
		GL11.glDrawElements( GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0 );
		
		this.screenDrawerShader.Stop();
		this.squareRawModel.Unbind();
	}
}
