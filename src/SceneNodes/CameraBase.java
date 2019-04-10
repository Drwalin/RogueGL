// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;

import RenderEngine.FrameBuffer;
import Util.Maths;

public abstract class CameraBase extends SceneNode
{
	protected FrameBuffer frameBuffer;
	
	protected float fov;
	protected float zNear;
	protected float zFar;
	
	public float GetFov()
	{
		return this.fov;
	}
	
	public float GetzNear()
	{
		return this.zNear;
	}
	
	public float GetzFar()
	{
		return this.zFar;
	}
	
	protected Matrix4f projectionMatrix = new Matrix4f();
	protected Matrix4f viewMatrix;
	
	public CameraBase( FrameBuffer frameBuffer, float fov, float zNear, float zFar, Vector3f location, Vector3f rotation, Vector3f scale )
	{
		super( location, rotation, scale );
		this.frameBuffer = frameBuffer;
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.UpdateMatrices();
		Mouse.setClipMouseCoordinatesToWindow( true );
		Mouse.setGrabbed( true );
	}
	
	public CameraBase( FrameBuffer frameBuffer, float fov, float zNear, float zFar, Vector3f location, Vector3f rotation )
	{
		super( location, rotation );
		this.frameBuffer = frameBuffer;
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.UpdateMatrices();
		Mouse.setClipMouseCoordinatesToWindow( true );
		Mouse.setGrabbed( true );
	}
	
	public CameraBase( FrameBuffer frameBuffer, float fov, float zNear, float zFar, Vector3f location )
	{
		super( location );
		this.frameBuffer = frameBuffer;
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.UpdateMatrices();
		Mouse.setClipMouseCoordinatesToWindow( true );
		Mouse.setGrabbed( true );
	}
	
	public void UpdateMatrices()
	{
		// Update projectionMatrix:
		float aspectRatio = (float)this.frameBuffer.GetWidth() / (float)this.frameBuffer.GetHeight();
		float y_scale = (float)(1f / Math.tan( Math.toRadians( this.fov / 2f ) ));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = this.zFar - this.zNear;
		
		this.projectionMatrix.setIdentity();
		this.projectionMatrix.m00 = x_scale;
		this.projectionMatrix.m11 = y_scale;
		this.projectionMatrix.m22 = -((this.zFar + this.zNear) / frustum_length);
		this.projectionMatrix.m23 = -1;
		this.projectionMatrix.m32 = -((2 * this.zNear * this.zFar) / frustum_length);
		this.projectionMatrix.m33 = 0;
		
		// Update viewMatrix:
		this.viewMatrix = Maths.CreateViewMatrix( this.location, this.rotation );
	}
	
	public Matrix4f GetProjectionMatrix()
	{
		return this.projectionMatrix;
	}
	
	public Matrix4f GetViewMatrix()
	{
		return this.viewMatrix;
	}
	
	public FrameBuffer GetFrameBuffer()
	{
		return this.frameBuffer;
	}
}
