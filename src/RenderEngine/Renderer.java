// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Loaders.Loader;
import Materials.Material;
import Models.RawModel;
import SceneNodes.CameraBase;
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

	protected CameraBase camera;
	
	public CameraBase GetCamera()
	{
		return this.camera;
	}

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
		return this.camera.GetLocation();
	}

	public boolean DrawingShadowBuffer()
	{
		return this.drawingShadow;
	}

	protected void Prepare( CameraBase camera )
	{
		this.camera = camera;
		camera.GetFrameBuffer().Bind();
		
		if( camera instanceof Light )
		{
			this.drawingShadow = true;
			
			GL11.glDisable( GL11.GL_ALPHA );
			GL11.glEnable( GL11.GL_DEPTH );
			GL11.glEnable( GL11.GL_DEPTH_TEST );
			GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
			GL11.glCullFace( GL11.GL_FRONT );
			GL11.glEnable( GL11.GL_CULL_FACE );
		} else
		{
			this.drawingShadow = false;
			
			GL11.glEnable( GL11.GL_ALPHA );
			GL11.glEnable( GL11.GL_ALPHA_TEST );
			GL11.glEnable( GL11.GL_DEPTH );
			GL11.glEnable( GL11.GL_DEPTH_TEST );
			GL11.glClearColor( 0.3f, 0.3f, 0.3f, 1 );
			GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
		}

		camera.UpdateMatrices();
		this.projectionMatrix = camera.GetProjectionMatrix();
		this.viewMatrix = camera.GetViewMatrix();
		Matrix4f.mul( this.projectionMatrix, this.viewMatrix, this.combinedMatrix );
	}
}
