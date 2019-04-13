// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Util.Maths;

public abstract class SceneNode
{
	protected Vector3f location;
	protected Vector3f rotation;
	protected Vector3f scale;

	protected Matrix4f worldTransformationMatrix;

	private boolean enabled;

	public SceneNode( Vector3f location, Vector3f rotation, Vector3f scale )
	{
		this.location = location;
		this.rotation = rotation;
		this.scale = scale;
		this.enabled = true;
	}

	public SceneNode( Vector3f location, Vector3f rotation )
	{
		this.location = location;
		this.rotation = rotation;
		this.scale = new Vector3f( 1, 1, 1 );
		this.enabled = true;
	}

	public SceneNode( Vector3f location )
	{
		this.location = location;
		this.rotation = new Vector3f( 0, 0, 0 );
		this.scale = new Vector3f( 1, 1, 1 );
		this.enabled = true;
	}

	public Vector3f GetLocation()
	{
		return this.location;
	}

	public void SetLocation( Vector3f location )
	{
		this.location = location;
	}

	public Vector3f GetRotation()
	{
		return this.rotation;
	}

	public void SetRotation( Vector3f rotation )
	{
		this.rotation = rotation;
	}

	public Vector3f GetScale()
	{
		return this.scale;
	}

	public void SetScale( Vector3f scale )
	{
		this.scale = scale;
	}

	public boolean IsEnabled()
	{
		return this.enabled;
	}

	public void Enable()
	{
		this.enabled = true;
	}

	public void Disable()
	{
		this.enabled = false;
	}

	public Matrix4f GetTransformationMatrix()
	{
		return this.worldTransformationMatrix;
	}

	public void UpdateRenderTick()
	{
		this.worldTransformationMatrix = Maths.CreateTransformMatrix( this.location, this.rotation, this.scale );
	}
}
