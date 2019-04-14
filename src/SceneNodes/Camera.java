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
		super( new FrameBuffer( Display.getWidth(), Display.getHeight(), true, 4 ), fov, zNear, zFar, location, rotation, scale );
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location, Quaternionf rotation )
	{
		super( new FrameBuffer( Display.getWidth(), Display.getHeight(), true, 4 ), fov, zNear, zFar, location, rotation );
	}
	
	public Camera( float fov, float zNear, float zFar, Vector3f location )
	{
		super( new FrameBuffer( Display.getWidth(), Display.getHeight(), true, 4 ), fov, zNear, zFar, location );
	}
	
	protected Vector3f eulerAngles = new Vector3f();
	
	public void Move()
	{
		{
			Vector3f temp = new Vector3f( 0.0f, 0.0f, 0.0f );
			float movementSpeed = GameLoop.deltaTime * 10;
			
			if( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
				temp.add( Maths.VEC_UP );
			if( Keyboard.isKeyDown( Keyboard.KEY_LCONTROL ) )
				temp.add( Maths.VEC_DOWN );
			if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
				temp.add( Maths.VEC_FORWARD );
			if( Keyboard.isKeyDown( Keyboard.KEY_A ) )
				temp.add( Maths.VEC_LEFT );
			if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
				temp.add( Maths.VEC_BACKWARD );
			if( Keyboard.isKeyDown( Keyboard.KEY_D ) )
				temp.add( Maths.VEC_RIGHT );
			
			this.SetLocalLocation( new Quaternionf().rotateY( this.eulerAngles.y ).transform( temp.mul( movementSpeed ) ).add( this.GetLocalLocation() ) );
		}
		
		{
			float rotationSpeed = 1.5f;
			
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
			
			if( this.eulerAngles.x > java.lang.Math.PI / 2 )
				this.eulerAngles.x = (float)java.lang.Math.PI / 2;
			else if( this.eulerAngles.x < -java.lang.Math.PI / 2 )
				this.eulerAngles.x = (float)-java.lang.Math.PI / 2;
			
			this.SetLocalRotation( new Quaternionf().rotateY( this.eulerAngles.y ).rotateX( this.eulerAngles.x ) );
			
			Mouse.NullPosition();
		}
	}
}
