// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import org.joml.*;

import Game.GameLoop;
import RenderEngine.Display;
import RenderEngine.FrameBuffer;
import Util.Keyboard;
import Util.Maths;
import Util.Mouse;

public class Camera extends CameraBase
{
	public Camera( float fov, float zNear, float zFar, Vector3f location, Quaternionf rotation, Vector3f scale )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location, rotation, scale );		
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location, Quaternionf rotation )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location, rotation );		
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location );		
	}
	
	protected Vector3f eulerAngles = new Vector3f();

	public void Move()
	{
		Vector3f temp = new Vector3f( 0.0f, 0.0f, 0.0f );
		Vector3f temp2 = new Vector3f();
		Vector4f temp4 = new Vector4f();

		float movementSpeed = GameLoop.deltaTime * 10;
		float rotationSpeed = 1.5f;

		if( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
			this.location.y += movementSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_LCONTROL ) )
			this.location.y -= movementSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
			temp.add( Maths.VEC_FORWARD );
		if( Keyboard.isKeyDown( Keyboard.KEY_A ) )
			temp.add( Maths.VEC_LEFT );
		if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
			temp.add( Maths.VEC_BACKWARD );
		if( Keyboard.isKeyDown( Keyboard.KEY_D ) )
			temp.add( Maths.VEC_RIGHT );
		
		if( Keyboard.isKeyDown( Keyboard.KEY_LEFT ) )
			eulerAngles.y += GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) )
			eulerAngles.y -= GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_UP ) )
			eulerAngles.x += GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) )
			eulerAngles.x -= GameLoop.deltaTime * rotationSpeed;
		
		this.eulerAngles.x -= Mouse.GetDY() * 0.002f;
		this.eulerAngles.y -= Mouse.GetDX() * 0.002f;

		if( this.eulerAngles.x > java.lang.Math.PI/2 )
			this.eulerAngles.x = (float) java.lang.Math.PI/2;
		else if( this.eulerAngles.x < -java.lang.Math.PI/2 )
			this.eulerAngles.x = (float) -java.lang.Math.PI/2;
		
		this.rotation.identity().rotateY( this.eulerAngles.y ).rotateX( this.eulerAngles.x );
		Mouse.NullPosition();

		this.location.add( this.rotation.transform( temp.mul( movementSpeed ) ) );
		
		/*
		if( Keyboard.isKeyDown( Keyboard.KEY_1 ) )
		{
			this.location = new Vector3f( 0, 1.5f, 4 );
			this.rotation = new Vector3f( 0.23f, 0, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_2 ) )
		{
			this.location = new Vector3f( 4, 1.5f, 0 );
			this.rotation = new Vector3f( 0.23f, -(float) java.lang.Math.PI * 1.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_3 ) )
		{
			this.location = new Vector3f( 0, 1.5f, -4 );
			this.rotation = new Vector3f( 0.23f, -(float) java.lang.Math.PI * 2.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_4 ) )
		{
			this.location = new Vector3f( -4, 1.5f, 0 );
			this.rotation = new Vector3f( 0.23f, -(float) java.lang.Math.PI * 3.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_5 ) )
		{
			this.location = new Vector3f( 0, 2, 0 );
			this.rotation = new Vector3f( (float) java.lang.Math.PI * 1.0f / 2.0f, 0, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_6 ) )
		{
			this.location = new Vector3f( GameLoop.LIGHT.GetWorldLocation() );
			this.rotation = new Vector3f( GameLoop.LIGHT.GetRotation() );
		}
		*/
	}
}
