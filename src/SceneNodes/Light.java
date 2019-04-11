// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import org.lwjgl.util.vector.*;

import RenderEngine.FrameBuffer;

public class Light extends CameraBase
{
	private Vector3f color;
	private Vector3f attenuation;
	private float innerSpotAngle;

	public Light( float fov, float zNear, float zFar, Vector3f location, Vector3f rotation, Vector3f scale, Vector3f color, Vector3f attenuation, float innerSpotAngle )
	{
		super( new FrameBuffer(512,512,true,0), fov, zNear, zFar, location, rotation, scale );
		this.color = color;
		this.attenuation = attenuation;
		this.innerSpotAngle = innerSpotAngle;
		if( this.innerSpotAngle+0.1f >= this.fov )
			this.innerSpotAngle = this.fov - 0.1f;
		if( this.innerSpotAngle < 0 )
			this.innerSpotAngle = 0;
	}
	
	private int redrawFrameCooldown = 0;
	public boolean NeedRedraw()
	{
		if( this.redrawFrameCooldown-- <= 0 )
		{
			this.redrawFrameCooldown = 1;
			return true;
		}
		return false;
	}

	public Vector3f GetColor()
	{
		return this.color;
	}

	public void SetColor( Vector3f color )
	{
		this.color = color;
	}

	public Vector3f GetAttenuation()
	{
		return this.attenuation;
	}
	
	public float GetInnerSpotAngle()
	{
		return this.innerSpotAngle;
	}
	
	public void SetInnerSpotAngle(float value)
	{
		this.innerSpotAngle = value;
		if( this.innerSpotAngle+0.1f >= this.fov )
			this.innerSpotAngle = this.fov - 0.1f;
		if( this.innerSpotAngle < 0 )
			this.innerSpotAngle = 0;
	}
}
