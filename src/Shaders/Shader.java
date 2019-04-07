// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Materials.Material;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;

public abstract class Shader
{
	private int programID;
	private int vertexShaderID;
	private int geometryShaderID;
	private int fragmentShaderID;

	private int drawingShadowUniform;

	protected FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer( 16 );

	public Shader( String vertexFile, String fragmentFile )
	{
		this.vertexShaderID = Shader.LoadShader( vertexFile, GL20.GL_VERTEX_SHADER );
		this.fragmentShaderID = Shader.LoadShader( fragmentFile, GL20.GL_FRAGMENT_SHADER );
		this.programID = GL20.glCreateProgram();
		GL20.glAttachShader( this.programID, this.vertexShaderID );
		GL20.glAttachShader( this.programID, this.fragmentShaderID );
		this.BindAttributes();
		GL20.glLinkProgram( this.programID );
		GL20.glValidateProgram( this.programID );
		this.geometryShaderID = -1;
		this.LoadUniformLocations();
	}

	public Shader( String vertexFile, String geometryFile, String fragmentFile )
	{
		this.vertexShaderID = Shader.LoadShader( vertexFile, GL20.GL_VERTEX_SHADER );
		this.geometryShaderID = Shader.LoadShader( geometryFile, GL32.GL_GEOMETRY_SHADER );
		this.fragmentShaderID = Shader.LoadShader( fragmentFile, GL20.GL_FRAGMENT_SHADER );
		this.programID = GL20.glCreateProgram();
		GL20.glAttachShader( this.programID, this.vertexShaderID );
		GL20.glAttachShader( this.programID, this.geometryShaderID );
		GL20.glAttachShader( this.programID, this.fragmentShaderID );
		this.BindAttributes();
		GL20.glLinkProgram( this.programID );
		GL20.glValidateProgram( this.programID );
		this.LoadUniformLocations();
	}

	protected abstract void BindAttributes();

	protected void LoadUniformLocations()
	{
		this.drawingShadowUniform = this.GetUniformLocation( "drawingShadow" );
	}

	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		this.SetUniform1( this.drawingShadowUniform, (int) (renderer.DrawingShadowBuffer() ? 1 : 0) );
	}

	protected int GetUniformLocation( String name )
	{
		GL20.glUseProgram( this.programID );
		int ret = GL20.glGetUniformLocation( this.programID, name );
		GL20.glUseProgram( 0 );
		return ret;
	}

	public void Start()
	{
		GL20.glUseProgram( this.programID );
	}

	public void Stop()
	{
		GL20.glUseProgram( 0 );
	}

	public void Destroy()
	{
		this.Stop();

		GL20.glDetachShader( this.programID, this.vertexShaderID );
		if( this.geometryShaderID > 0 )
			GL20.glDetachShader( this.programID, this.geometryShaderID );
		GL20.glDetachShader( this.programID, this.fragmentShaderID );

		GL20.glDeleteShader( this.vertexShaderID );
		if( this.geometryShaderID > 0 )
			GL20.glDeleteShader( this.geometryShaderID );
		GL20.glDeleteShader( this.fragmentShaderID );

		GL20.glDeleteProgram( this.programID );
	}

	protected void BindAttribute( int location, String variableName )
	{
		GL20.glBindAttribLocation( this.programID, location, variableName );
	}

	private static int LoadShader( String file, int type )
	{
		StringBuilder shaderSource = new StringBuilder();
		try
		{
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			String line;
			while( (line = reader.readLine()) != null )
			{
				shaderSource.append( line ).append( "//\n" );
			}
			reader.close();
		} catch( IOException e )
		{
			e.printStackTrace();
			System.exit( -1 );
		}
		int shaderID = GL20.glCreateShader( type );
		GL20.glShaderSource( shaderID, shaderSource );
		GL20.glCompileShader( shaderID );
		if( GL20.glGetShaderi( shaderID, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE )
		{
			System.out.println( GL20.glGetShaderInfoLog( shaderID, 500 ) );
			System.err.println( "Could not compile shader!" );
			System.exit( -1 );
		}
		return shaderID;
	}

	protected int GetProgramID()
	{
		return this.programID;
	}

	protected void SetUniform1( int location, float value )
	{
		GL20.glUniform1f( location, value );
	}

	protected void SetUniform1( int location, int value )
	{
		GL20.glUniform1i( location, value );
	}

	protected void SetUniform1( int location, IntBuffer value )
	{
		GL20.glUniform1( location, value );
	}

	protected void SetUniform1( int location, FloatBuffer value )
	{
		GL20.glUniform1( location, value );
	}

	protected void SetUniform3( int location, Vector3f value )
	{
		GL20.glUniform3f( location, value.x, value.y, value.z );
	}

	protected void SetUniform4x4( int location, Matrix4f value )
	{
		value.store( this.matrixBuffer );
		this.matrixBuffer.flip();
		GL20.glUniformMatrix4( location, false, this.matrixBuffer );
	}

}
