// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.joml.*;

import SceneNodes.CameraBase;
import SceneNodes.Light;

public class Renderer
{
	protected Matrix4f projectionMatrix = new Matrix4f();
	protected Matrix4f viewMatrix = new Matrix4f();
	protected Matrix4f combinedMatrix = new Matrix4f();
	protected Matrix4f fullCombinedMatrix = new Matrix4f();
	
	protected boolean drawingShadow;
	
	protected Vector3f ambientLightColor = new Vector3f( .2f, .2f, .2f );
	protected List<CameraBase> cameras = new ArrayList<CameraBase>();
	
	protected CameraBase currentCamera;
	
	public CameraBase GetCurrentCamera()
	{
		return this.currentCamera;
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
		this.cameras.add( light );
	}
	
	public void AddCamera( CameraBase camera )
	{
		this.cameras.add( camera );
	}
	
	public List<CameraBase> GetCameras()
	{
		return this.cameras;
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
		return this.currentCamera.GetWorldLocation();
	}
	
	public boolean DrawingShadowBuffer()
	{
		return this.drawingShadow;
	}
	
	protected void Prepare( CameraBase camera )
	{
		this.currentCamera = camera;
		camera.GetFrameBuffer().Bind();
		
		GL11.glEnable( GL11.GL_ALPHA );
		GL11.glEnable( GL11.GL_DEPTH );
		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glBlendFunc( GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA );
		
		if( camera instanceof Light )
		{
			this.drawingShadow = true;
			
			GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
			GL11.glDisable( GL11.GL_CULL_FACE );
		}else
		{
			this.drawingShadow = false;
			
			GL11.glClearColor( 0.3f, 0.3f, 0.3f, 1 );
			GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
			GL11.glCullFace( GL11.GL_BACK );
			GL11.glEnable( GL11.GL_CULL_FACE );
		}
		
		this.projectionMatrix.set( camera.GetProjectionMatrix() );
		this.viewMatrix.set( camera.GetViewMatrix() );
		this.combinedMatrix.set( this.projectionMatrix ).mul( this.viewMatrix );
	}
}
