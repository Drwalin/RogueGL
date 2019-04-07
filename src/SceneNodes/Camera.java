// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package SceneNodes;

import org.lwjgl.input.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

import Game.GameLoop;
import Util.Maths;

public class Camera extends SceneNode
{
	private float fov;
	private float zNear;
	private float zFar;

	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix;

	public Camera( float fov, float zNear, float zFar, Vector3f location, Vector3f rotation, Vector3f scale )
	{
		super( location, rotation, scale );
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.UpdateMatrices();
		Mouse.setClipMouseCoordinatesToWindow( true );
		Mouse.setGrabbed( true );
	}

	public Camera( float fov, float zNear, float zFar, Vector3f location, Vector3f rotation )
	{
		super( location, rotation );
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		this.UpdateMatrices();
		Mouse.setClipMouseCoordinatesToWindow( true );
		Mouse.setGrabbed( true );
	}

	public Camera( float fov, float zNear, float zFar, Vector3f location )
	{
		super( location );
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
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan( Math.toRadians( this.fov / 2f ) )) * aspectRatio);
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
			Vector3f.add( temp, Maths.VEC_FORWARD, temp );
		if( Keyboard.isKeyDown( Keyboard.KEY_A ) )
			Vector3f.add( temp, Maths.VEC_LEFT, temp );
		if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
			Vector3f.add( temp, Maths.VEC_BACKWARD, temp );
		if( Keyboard.isKeyDown( Keyboard.KEY_D ) )
			Vector3f.add( temp, Maths.VEC_RIGHT, temp );

		if( Keyboard.isKeyDown( Keyboard.KEY_LEFT ) )
			this.rotation.y -= GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_RIGHT ) )
			this.rotation.y += GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_UP ) )
			this.rotation.x -= GameLoop.deltaTime * rotationSpeed;
		if( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) )
			this.rotation.x += GameLoop.deltaTime * rotationSpeed;

		Vector2f windowSize = new Vector2f( Display.getWidth(), Display.getHeight() );
		Vector2f mouseOrigin = new Vector2f( windowSize.x * 0.5f, windowSize.y * 0.5f );
		Vector2f mousePosition = new Vector2f( Mouse.getX() - mouseOrigin.x, Mouse.getY() - mouseOrigin.y );

		this.rotation.x -= mousePosition.y * 0.004f;
		this.rotation.y += mousePosition.x * 0.004f;
		Mouse.setCursorPosition( (int) mouseOrigin.x, (int) mouseOrigin.y );

		if( this.rotation.x > Math.PI * 0.5 )
			this.rotation.x = (float) Math.PI * 0.5f;
		else if( this.rotation.x < -Math.PI * 0.5 )
			this.rotation.x = -(float) Math.PI * 0.5f;

		if( temp.lengthSquared() > 0.01f )
		{
			Matrix4f matrix = new Matrix4f();
			Matrix4f.rotate( -this.rotation.y, Maths.VEC_Y, matrix, matrix );
			Matrix4f.rotate( -this.rotation.x, Maths.VEC_X, matrix, matrix );

			Matrix4f.transform( matrix, new Vector4f( temp.x, temp.y, temp.z, 1.0f ), temp4 );

			temp.x = temp4.x;
			temp.y = temp4.y;
			temp.z = temp4.z;
			temp.normalise( temp2 );

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
			this.rotation = new Vector3f( 0.23f, -(float) Math.PI * 1.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_3 ) )
		{
			this.location = new Vector3f( 0, 1.5f, -4 );
			this.rotation = new Vector3f( 0.23f, -(float) Math.PI * 2.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_4 ) )
		{
			this.location = new Vector3f( -4, 1.5f, 0 );
			this.rotation = new Vector3f( 0.23f, -(float) Math.PI * 3.0f / 2.0f, 0 );
		}
		if( Keyboard.isKeyDown( Keyboard.KEY_5 ) )
		{
			this.location = new Vector3f( 0, 2, 0 );
			this.rotation = new Vector3f( (float) Math.PI * 1.0f / 2.0f, 0, 0 );
		}
	}

}
