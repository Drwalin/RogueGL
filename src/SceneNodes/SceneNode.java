// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import java.util.HashSet;
import java.util.Set;

import org.joml.*;

public abstract class SceneNode
{
	protected Vector3f location = new Vector3f();
	protected Quaternionf rotation = new Quaternionf();
	protected Vector3f scale = new Vector3f(1,1,1);
	
	protected Vector3f worldLocation = new Vector3f();
	protected Quaternionf worldRotation = new Quaternionf();
	
	protected Matrix4f worldTransformationMatrix = new Matrix4f();
	
	private boolean enabled;
	
	protected SceneNode parentNode = null;
	protected Set<SceneNode> childNodes = new HashSet<SceneNode>();
	
	public SceneNode( Vector3f location, Quaternionf rotation, Vector3f scale )
	{
		this.location.set( location );
		this.rotation.set( rotation );
		this.scale.set( scale );
		this.enabled = true;
		this.worldLocation.set( this.location );
		this.worldRotation.set( this.rotation );
	}
	
	public SceneNode( Vector3f location, Quaternionf rotation )
	{
		this.location.set( location );
		this.rotation.set( rotation );
		this.enabled = true;
		this.worldLocation.set( this.location );
		this.worldRotation.set( this.rotation );
	}
	
	public SceneNode( Vector3f location )
	{
		this.location.set( location );
		this.enabled = true;
		this.worldLocation.set( this.location );
	}
	
	public Vector3f GetLocalLocation()
	{
		return this.location;
	}
	
	public void SetLocalLocation( Vector3f newLocation )
	{
		if( this.parentNode == null )
		{
			this.location.set( newLocation );
			this.worldLocation.set( newLocation );
		}
		else
		{
			Vector3f deltaLocation = new Vector3f().set( newLocation ).sub( this.location );
			this.parentNode.worldRotation.transform( deltaLocation );
			this.worldLocation.add( deltaLocation );
		}
	}
	
	public Vector3f GetWorldLocation()
	{
		return this.worldLocation;
	}
	
	public void SetWorldLocation( Vector3f newLocation )
	{
		if( this.parentNode == null )
		{
			this.location.set( newLocation );
			this.worldLocation.set( newLocation );
		}else
		{
			Vector3f deltaLocation = new Vector3f().set( newLocation ).sub( this.worldLocation );
			Quaternionf dest = new Quaternionf();
			this.worldRotation.invert( dest );
			dest.transform( deltaLocation );
			this.parentNode.worldRotation.transform( deltaLocation ); // ?????????????
			this.location.add( deltaLocation );
			this.worldLocation.set( newLocation );
		}
	}
	
	public Quaternionf GetLocalRotation()
	{
		return this.rotation;
	}
	
	public void SetLocalRotation( Quaternionf newRotation )
	{
		this.rotation.set( newRotation );
		this.worldRotation.set( newRotation );
		if( this.parentNode != null )
		{
			this.worldRotation.set( newRotation ).mul( this.parentNode.worldRotation );
		}
	}
	
	public Quaternionf GetWorldRotation()
	{
		return this.worldRotation;
	}
	
	public void SetWorldRotation( Quaternionf newRotation )
	{
		if( this.parentNode == null )
		{
			this.rotation.set( newRotation );
			this.worldRotation.set( newRotation );
		}else
		{
			// ??????????????????
			this.worldRotation.difference( newRotation );
			this.rotation.mul( this.worldRotation );
			this.worldRotation.set( newRotation );
		}
	}
	
	public Vector3f GetScale()
	{
		return this.scale;
	}
	
	public void SetScale( Vector3f scale )
	{
		this.scale.set( scale );
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
	
	public SceneNode GetRootNode()
	{
		SceneNode rootChild = this;
		SceneNode root = this.parentNode;
		while( root != null )
		{
			rootChild = root;
			root = root.parentNode;
		}
		return rootChild;
	}
	
	public void UpdateRootTransformationState( boolean updateMatrices )
	{
		this.GetRootNode().UpdateTransformationState( updateMatrices );
	}
	
	public void UpdateTransformationState( boolean updateMatrices )
	{
		if( this.parentNode != null )
		{
			this.worldRotation.set( this.parentNode.worldRotation ).mul( this.rotation );
			
			this.worldLocation.set( this.location );
			this.parentNode.worldRotation.transform( this.worldLocation );
			this.worldLocation.add( this.parentNode.worldLocation );
		}else
		{
			this.worldLocation.set( this.location );
			this.worldRotation.set( this.rotation );
		}
		
		if( updateMatrices )
			this.worldTransformationMatrix.identity().translationRotateScale( this.worldLocation, this.worldRotation, this.scale );
		
		for( SceneNode child : this.childNodes )
			child.UpdateTransformationState( updateMatrices );
	}
	
	public boolean UseGlobalUpdate()
	{
		return this.parentNode == null;
	}
	
	public void AddChild( SceneNode child )
	{
		this.childNodes.add( child );
		child.parentNode = this;
		this.UpdateTransformationState( false );
	}
	
	public void RemoveChild( SceneNode child )
	{
		this.childNodes.remove( child );
		child.parentNode = null;
		child.SetWorldLocation( child.worldLocation );
		child.SetWorldRotation( child.worldRotation );
		child.UpdateTransformationState( false );
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
