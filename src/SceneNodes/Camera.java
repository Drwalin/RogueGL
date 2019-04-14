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
	public Camera( float fov, float zNear, float zFar, Vector3f location, Vector3f rotation, Vector3f scale )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location, rotation, scale );		
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location, Vector3f rotation )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location, rotation );		
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location )
	{
		super( new FrameBuffer(Display.getWidth(),Display.getHeight(),true,4), fov, zNear, zFar, location );		
	}


	public void Move()
	{
		Vector3f temp = new Vector3f( 0.0f, 0.0f, 0.0f );
		Vector3f temp2 = new Vector3f();
		Vector4f temp4 = new Vector4f();

		float movementSpeed = 10;
		float rotationSpeed = 1.5f;

		if( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
			this.location.y += GameLoop.deltaTime * movementSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_LCONTROL ) )
			this.location.y -= GameLoop.deltaTime * movementSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
			temp.add( Maths.VEC_FORWARD );
		if( Keyboard.isKeyDown( Keyboard.KEY_A ) )
			temp.add( Maths.VEC_LEFT );
		if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
			temp.add( Maths.VEC_BACKWARD );
		if( Keyboard.isKeyDown( Keyboard.KEY_D ) )
			temp.add( Maths.VEC_RIGHT );

		if( Keyboard.isKeyDown( Keyboard.KEY_LEFT ) )
			this.rotation.y += GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) )
			this.rotation.y -= GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_UP ) )
			this.rotation.x += GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) )
			this.rotation.x -= GameLoop.deltaTime * rotationSpeed;

		this.rotation.x -= Mouse.GetDY() * 0.004f;
		this.rotation.y -= Mouse.GetDX() * 0.004f;
		Mouse.NullPosition();

		if( this.rotation.x > java.lang.Math.PI * 0.5 )
			this.rotation.x = (float) java.lang.Math.PI * 0.5f;
		else if( this.rotation.x < -java.lang.Math.PI * 0.5 )
			this.rotation.x = -(float) java.lang.Math.PI * 0.5f;

		if( temp.lengthSquared() > 0.01f )
		{
			Matrix4f matrix = new Matrix4f();
			matrix.rotate( this.rotation.y, Maths.VEC_Y );
			matrix.rotate( this.rotation.x, Maths.VEC_X );
			temp4.set( temp.x, temp.y, temp.z, 1 );
			matrix.transform( temp4 );

			temp.x = temp4.x;
			temp.y = temp4.y;
			temp.z = temp4.z;
			temp.normalize( temp2 );

			this.location.x += temp2.x * GameLoop.deltaTime * movementSpeed;
			this.location.y += temp2.y * GameLoop.deltaTime * movementSpeed;
			this.location.z += temp2.z * GameLoop.deltaTime * movementSpeed;
		}

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
	}
}
