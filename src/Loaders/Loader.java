// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Loaders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.TextureLoader;

import Models.RawModel;
import RenderEngine.TextureInstance;

/**
 * Handles the loading of geometry data into VAOs. It also keeps track of all
 * the created VAOs and VBOs so that they can all be deleted when the game
 * closes.
 * 
 * @author Karl
 * @edited by Marek Zalewski
 *
 */
public class Loader
{
	private List<RawModel> models = new ArrayList<RawModel>();

	private List<TextureInstance> textures = new ArrayList<TextureInstance>();

	public RawModel LoadOBJ( String fileName )
	{
		RawModel model = LoaderOBJ.Load( "res/models/static/"+fileName+".obj" );
		this.models.add( model );
		return model;
	}

	public RawModel LoadOBJ( String fileName, boolean useTangent )
	{
		RawModel model = LoaderOBJ.Load( "res/models/static/"+fileName+".obj", useTangent );
		this.models.add( model );
		return model;
	}

	public RawModel LoadCOLLADA( String fileName, boolean useTangent ) throws Exception
	{
		NodeXML rootNode = LoaderXML.Load( "res/models/"+fileName+".dae" );
		RawModel model = LoaderCOLLADA.LoadModel( rootNode, useTangent );
		this.models.add( model );
		return model;
	}

	static public int CreateVAO()
	{
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray( vaoID );
		return vaoID;
	}

	public static void UnbindVAO()
	{
		GL30.glBindVertexArray( 0 );
	}

	public static int StoreDataInAttributeList( int attributeNumber, int coordinateSize, float[] data )
	{
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboID );
		FloatBuffer buffer = Loader.StoreDataInFloatBuffer( data );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW );
		GL20.glVertexAttribPointer( attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );
		return vboID;
	}

	public void Destroy()
	{
		for( RawModel model : this.models )
		{
			model.Destroy();
		}
		this.models.clear();
		for( TextureInstance texture : this.textures )
		{
			texture.Destroy();
		}
		this.textures.clear();

	}

	public static int BindIndicesBuffer( int[] indices )
	{
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, vboId );
		IntBuffer buffer = Loader.StoreDataInIntBuffer( indices );
		GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW );
		return vboId;
	}

	private static IntBuffer StoreDataInIntBuffer( int[] data )
	{
		IntBuffer buffer = BufferUtils.createIntBuffer( data.length );
		buffer.put( data );
		buffer.flip();
		return buffer;
	}

	private static FloatBuffer StoreDataInFloatBuffer( float[] data )
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer( data.length );
		buffer.put( data );
		buffer.flip();
		return buffer;
	}

	public TextureInstance LoadTexture( String fileName )
	{
		TextureInstance texture = null;
		try
		{
			texture = new TextureInstance( TextureLoader.getTexture( "PNG", new FileInputStream( "res/textures/" + fileName + ".png" ) ).getTextureID() );
			GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );
			GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR );
			GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.311f );
		} catch( FileNotFoundException e )
		{
			e.printStackTrace();
		} catch( IOException e )
		{
			e.printStackTrace();
		}
		this.textures.add( texture );
		return texture;
	}

}
