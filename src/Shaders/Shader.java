// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import Materials.Material;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;

public abstract class Shader
{
	private int programID;
	private int vertexShaderID;
	private int geometryShaderID;
	private int fragmentShaderID;
	
	private int shadowProgramID;
	private int shadowVertexShaderID;
	private int shadowGeometryShaderID;
	private int shadowFragmentShaderID;
	
	protected FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer( 16 );
	
	protected boolean generatingShadows;
	
	public Shader( String shaderPackageName )
	{
		this.generatingShadows = false;
		this.vertexShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".vs", GL20.GL_VERTEX_SHADER );
		this.geometryShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".gs", GL32.GL_GEOMETRY_SHADER );
		this.fragmentShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".fs", GL20.GL_FRAGMENT_SHADER );
		this.programID = Shader.CreateProgram( this, this.vertexShaderID, this.geometryShaderID, this.fragmentShaderID );
		
		this.generatingShadows = true;
		this.shadowVertexShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".svs", GL20.GL_VERTEX_SHADER );
		this.shadowGeometryShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".sgs", GL32.GL_GEOMETRY_SHADER );
		this.shadowFragmentShaderID = Shader.LoadShader( "res/shaders/" + shaderPackageName + ".sfs", GL20.GL_FRAGMENT_SHADER );
		this.shadowProgramID = Shader.CreateProgram( this, this.shadowVertexShaderID, this.shadowGeometryShaderID, this.shadowFragmentShaderID );
		
		this.generatingShadows = false;
		
		System.out.println( "Shader loaded:" );
		System.out.println( "    " + this.vertexShaderID );
		System.out.println( "    " + this.geometryShaderID );
		System.out.println( "    " + this.fragmentShaderID );
		System.out.println( " += " + this.programID );
		System.out.println( "    " + this.shadowVertexShaderID );
		System.out.println( "    " + this.shadowGeometryShaderID );
		System.out.println( "    " + this.shadowFragmentShaderID );
		System.out.println( " += " + this.shadowProgramID );
		
		this.LoadUniformLocations();
	}
	
	public static int CreateProgram( Shader shader, int vertex, int geometry, int fragment )
	{
		if( vertex < 0 || fragment < 0 )
			return -1;
		GL20.glUseProgram( 0 );
		int programID = GL20.glCreateProgram();
		GL20.glAttachShader( programID, vertex );
		if( geometry >= 0 )
			GL20.glAttachShader( programID, geometry );
		GL20.glAttachShader( programID, fragment );
		shader.BindAttributes();
		GL20.glLinkProgram( programID );
		GL20.glValidateProgram( programID );
		return programID;
	}
	
	public boolean IsGeneratingShadows()
	{
		return this.generatingShadows;
	}
	
	protected abstract void BindAttributes();
	
	public abstract void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material );
	
	public int GetRegularUniformLocation( String name )
	{
		GL20.glUseProgram( this.programID );
		int ret = GL20.glGetUniformLocation( this.programID, name );
		GL20.glUseProgram( 0 );
		return ret;
	}
	
	public int GetShadowUniformLocation( String name )
	{
		GL20.glUseProgram( this.shadowProgramID );
		int ret = GL20.glGetUniformLocation( this.shadowProgramID, name );
		GL20.glUseProgram( 0 );
		return ret;
	}
	
	public void Start( boolean generatingShadows )
	{
		if( this.shadowProgramID < 0 || generatingShadows == false )
			GL20.glUseProgram( this.programID );
		else
			GL20.glUseProgram( this.shadowProgramID );
		this.generatingShadows = generatingShadows;
	}
	
	public void Stop()
	{
		GL20.glUseProgram( 0 );
	}
	
	public void Destroy()
	{
		GL20.glUseProgram( 0 );
		
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
		if( this.generatingShadows )
			GL20.glBindAttribLocation( this.shadowProgramID, location, variableName );
		else
			GL20.glBindAttribLocation( this.programID, location, variableName );
	}
	
	private static int LoadShader( String file, int type )
	{
		if( new File(file).exists() == false )
			return -1;
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
		}catch( IOException e )
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
	
	protected abstract void LoadUniformLocations();
	
}
