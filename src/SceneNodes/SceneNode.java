// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import java.util.HashSet;
import java.util.Set;

import org.joml.*;

import Util.Maths;

public abstract class SceneNode
{
	protected Vector3f location;
	protected Vector3f rotation;
	protected Vector3f scale;
	
	protected Vector3f worldLocation = new Vector3f();
	protected Vector3f worldRotation = new Vector3f();
	
	protected Matrix4f worldTransformationMatrix;
	
	private boolean enabled;
	
	protected SceneNode parentNode;
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
		System.out.println( "SceneNode.GetLocation()" );
		return this.location;
	}
	
	public void SetLocation( Vector3f location )
	{
		this.location = location;
	}
	
	public Vector3f GetWorldLocation()
	{
		return this.worldLocation;
	}
	
	public Vector3f GetRotation()
	{
		return this.rotation;
	}
	
	public void SetRotation( Vector3f rotation )
	{
		this.rotation = rotation;
	}
	
	public Vector3f GetWorldRotation() throws Exception
	{
		throw new Exception( "SceneNode.GetWorldRotation() is not done yet, no ide how to do it" );
//		return this.worldRotation;
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
	
	public void UpdateDrawState()
	{
		this.worldTransformationMatrix = Maths.CreateTransformMatrix( this.location, this.rotation, this.scale );
		if( this.parentNode != null )
		{
			this.parentNode.worldTransformationMatrix.mul( this.worldTransformationMatrix, this.worldTransformationMatrix );
			//this.worldTransformationMatrix.set( this.parentNode.worldTransformationMatrix ).mul( this.worldTransformationMatrix );
			Vector4f t = new Vector4f( this.location.x, this.location.y, this.location.z, 1 );
			this.worldTransformationMatrix.transform( t );
			this.worldLocation.set( t.x, t.y, t.z );
		}else
		{
			this.worldLocation.set( this.location );
			this.worldRotation.set( this.rotation );
		}
		
		this.UpdateChildNodes();
	}
	
	private void UpdateChildNodes()
	{
		for( SceneNode child : this.childNodes )
			child.UpdateDrawState();
	}
	
	public boolean UseGlobalUpdate()
	{
		return this.parentNode == null;
	}
	
	public void AddChild( SceneNode child )
	{
		this.childNodes.add( child );
		child.parentNode = this;
	}
	
	public void RemoveChild( SceneNode child )
	{
		this.childNodes.remove( child );
		child.parentNode = null;
	}
	
	public Set<SceneNode> GetChildren()
	{
		return this.childNodes;
	}
	
	public SceneNode GetParent()
	{
		return this.parentNode;
	}
}
