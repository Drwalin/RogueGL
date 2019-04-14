// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Loaders;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import Models.RawModel;
import RenderEngine.TextureInstance;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.sun.prism.impl.BufferUtil;

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
		RawModel model = LoaderOBJ.Load( "res/models/static/" + fileName + ".obj" );
		this.models.add( model );
		return model;
	}
	
	public RawModel LoadOBJ( String fileName, boolean useTangent )
	{
		RawModel model = LoaderOBJ.Load( "res/models/static/" + fileName + ".obj", useTangent );
		this.models.add( model );
		return model;
	}
	
	public RawModel LoadCOLLADA( String fileName, boolean useTangent, boolean loadBoneWeights ) throws Exception
	{
		NodeXML rootNode = LoaderXML.Load( "res/models/" + fileName + ".dae" );
		RawModel model = LoaderCOLLADA.LoadModel( rootNode, useTangent, loadBoneWeights );
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
	
	public static int StoreFloatDataInAttributeList( int attributeNumber, int coordinateSize, float[] data )
	{
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboID );
		FloatBuffer buffer = Loader.StoreDataInFloatBuffer( data );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW );
		GL20.glVertexAttribPointer( attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0 );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );
		return vboID;
	}
	
	public static int StoreByteDataInAttributeList( int attributeNumber, int coordinateSize, byte[] data )
	{
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vboID );
		ByteBuffer buffer = Loader.StoreDataInByteBuffer( data );
		GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW );
		GL20.glVertexAttribPointer( attributeNumber, coordinateSize, GL11.GL_BYTE, false, 0, 0 );
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
	private static ByteBuffer StoreDataInByteBuffer( byte[] data )
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer( data.length );
		buffer.put( data );
		buffer.flip();
		return buffer;
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
		String path = "res/textures/" + fileName + ".png";
		try
		{
			InputStream in = new FileInputStream(path);
			BufferedImage image = ImageIO.read(in);
			AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
			transform.translate(0, -image.getHeight());
			AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = operation.filter(image, null);
			int width = image.getWidth();
			int height = image.getHeight();
			int[] pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
			ByteBuffer buffer = BufferUtil.newByteBuffer( width * height * 4 );//stack.malloc(width * height * 4);

			for (int y = 0; y < height; y++) {
			    for (int x = 0; x < width; x++) {
			        /* Pixel as RGBA: 0xAARRGGBB */
			        int pixel = pixels[y * width + x];
			        /* Red component 0xAARRGGBB >> (4 * 4) = 0x0000AARR */
			        buffer.put((byte) ((pixel >> 16) & 0xFF));
			        /* Green component 0xAARRGGBB >> (2 * 4) = 0x00AARRGG */
			        buffer.put((byte) ((pixel >> 8) & 0xFF));
			        /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
			        buffer.put((byte) (pixel & 0xFF));
			        /* Alpha component 0xAARRGGBB >> (6 * 4) = 0x000000AA */
			        buffer.put((byte) ((pixel >> 24) & 0xFF));
			    }
			}
			buffer.flip();
			
			texture = new TextureInstance( GL11.glGenTextures() );
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture.GetTextureID() );
			GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
			GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
			GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
			
			GL30.glGenerateMipmap( GL11.GL_TEXTURE_2D );
			GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR );
			GL11.glTexParameterf( GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.311f );
		}catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}catch( IOException e )
		{
			e.printStackTrace();
		}
		this.textures.add( texture );
		return texture;
	}
	
}
