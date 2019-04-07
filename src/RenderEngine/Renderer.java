// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Materials.Material;
import Models.RawModel;
import SceneNodes.Camera;
import SceneNodes.DrawableSceneNode;
import SceneNodes.Light;

public class Renderer
{
	protected Matrix4f projectionMatrix;
	protected Matrix4f viewMatrix;
	protected Matrix4f combinedMatrix = new Matrix4f();
	protected Matrix4f fullCombinedMatrix = new Matrix4f();

	protected boolean drawingShadow;

	protected Vector3f ambientLightColor = new Vector3f( .2f, .2f, .2f );
	protected List<Light> lights = new ArrayList<Light>();

	protected Vector3f cameraLocation;

	public Matrix4f GetProjectionMatrix()
	{
		return this.projectionMatrix;
	}

	public Matrix4f GetViewMatrix()
	{
		return this.viewMatrix;
	}

	public Matrix4f GetCombinedMatrix()
	{
		return this.combinedMatrix;
	}

	public Matrix4f GetFullCombinedMatrix()
	{
		return this.fullCombinedMatrix;
	}

	public void AddLight( Light light )
	{
		this.lights.add( light );
	}

	public List<Light> GetLights()
	{
		return this.lights;
	}

	public Vector3f GetAmbientLightColor()
	{
		return this.ambientLightColor;
	}

	public void SetAmbientLightColor( Vector3f color )
	{
		this.ambientLightColor = color;
	}

	public Vector3f GetCameraLocation()
	{
		return this.cameraLocation;
	}

	public boolean DrawingShadowBuffer()
	{
		return this.drawingShadow;
	}

	protected void Prepare( Camera camera )
	{
		if( camera instanceof Light )
		{
			this.drawingShadow = true;

		} else
		{
			this.drawingShadow = false;

			GL11.glEnable( GL11.GL_ALPHA );
			GL11.glEnable( GL11.GL_ALPHA_TEST );
			GL11.glEnable( GL11.GL_DEPTH );
			GL11.glEnable( GL11.GL_DEPTH_TEST );
			GL11.glClearColor( 0.3f, 0.3f, 0.3f, 1 );
			GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
			GL11.glCullFace( GL11.GL_BACK );
			GL11.glEnable( GL11.GL_CULL_FACE );
		}

		camera.UpdateMatrices();
		this.projectionMatrix = camera.GetProjectionMatrix();
		this.viewMatrix = camera.GetViewMatrix();
		Matrix4f.mul( this.projectionMatrix, this.viewMatrix, this.combinedMatrix );

		this.cameraLocation = camera.GetLocation();
	}

	private void Render( DrawableSceneNode sceneNode )
	{
		sceneNode.UpdateWorldTransformationMatrix();
		RawModel model = sceneNode.GetModel().GetRawModel();
		List<Material> materialSet = sceneNode.GetModel().GetMaterialSet();

		model.Bind();

		int id = 2;
		for( id = 0; id < model.GetMaterialsCount(); ++id )
		{
			Material material = materialSet.get( id );
			material.GetShader().Start();
			material.GetShader().SetUniforms( sceneNode, this, material );
			if( material.HasTransparency() )
				GL11.glDisable( GL11.GL_CULL_FACE );
			else
				GL11.glEnable( GL11.GL_CULL_FACE );
			GL11.glDrawElements( GL11.GL_TRIANGLES, model.GetMaterialIndexCount( id ), GL11.GL_UNSIGNED_INT, model.GetMaterialIndexOffset( id ) * 4 );
			material.GetShader().Stop();
		}

		model.Unbind();
	}
}
