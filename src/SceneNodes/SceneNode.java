// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.util.vector.*;

import Util.Maths;

public abstract class SceneNode
{
	protected Vector3f location;
	protected Vector3f rotation;
	protected Vector3f scale;

	protected Matrix4f worldTransformationMatrix;

	private boolean enabled;
	
	protected SceneNode parentNode = null;
	protected Set<SceneNode> childNodes = new HashSet<SceneNode>();

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

	public Vector3f GetWorldLocation()
	{
		Vector4f ret = Matrix4f.transform( this.worldTransformationMatrix, new Vector4f( this.location.x, this.location.y, this.location.z, 1 ), null );
		return new Vector3f( ret.x, ret.y, ret.z );
	}

	public void SetLocation( Vector3f location )
	{
		this.location = location;
	}

	public Vector3f GetRotation()
	{
		return this.rotation;
	}

	public Vector3f GetWorldRotation() throws Exception
	{
		//Quaternion rot = new Quaternion();
		//this.worldTransformationMatrix.get( rot );
		throw new Exception("SceneNode.GetWorldRotation is not done yet");
		
		//return this.rotation;
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
		if( this.parentNode != null )
		{
			this.worldTransformationMatrix = Matrix4f.mul( this.parentNode.GetTransformationMatrix(), this.worldTransformationMatrix, null );
		}
		this.UpdateChildNodes();
	}
	
	public Set<SceneNode> GetChildNodes()
	{
		return this.childNodes;
	}
	
	public SceneNode GetParentNode()
	{
		return this.parentNode;
	}
	
	public boolean NeedGlobalUpdate()
	{
		return this.parentNode == null;
	}
	
	private void UpdateChildNodes()
	{
		for( SceneNode child : this.childNodes )
			child.UpdateRenderTick();
	}
	
	public void AddChildNode( SceneNode child )
	{
		this.childNodes.add( child );
		child.parentNode = this;
	}
	
	public void RemoveChildNode( SceneNode child )
	{
		this.childNodes.remove( child );
		child.parentNode = null;
	}
}
