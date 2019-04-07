// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Models;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class RawModel
{
	private int[] vboID = null;
	private int vaoID = 0;
	private int[] vertexOffset = null;
	private int vertexCount = 0; 

	public RawModel( int vaoID, int[] vboID, int[] vertexOffset, int vertexCount )
	{
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vertexOffset = vertexOffset;
		this.vboID = vboID;
	}

	public int GetVertexCount()
	{
		return vertexCount;
	}

	public void Destroy()
	{
		GL30.glDeleteVertexArrays( this.vaoID );
		for( int vbo : this.vboID )
		{
			GL15.glDeleteBuffers( vbo );
		}
		this.vaoID = 0;
		this.vboID = null;
		this.vertexCount = 0;
		this.vertexOffset = null;
	}

	public void Unbind()
	{
		for( int i = 0; i < this.vboID.length - 1; ++i )
			GL20.glDisableVertexAttribArray( i );
		GL30.glBindVertexArray( 0 );
	}

	public void Bind()
	{
		GL30.glBindVertexArray( this.vaoID );
		for( int i = 0; i < this.vboID.length - 1; ++i )
			GL20.glEnableVertexAttribArray( i );
	}

	public int GetMaterialIndexOffset( int id )
	{
		return this.vertexOffset[id] * 4; // offset is in bytes not in ids
	}

	public int GetMaterialIndexCount( int id )
	{
		int ret = 0;
		if( id == this.vertexOffset.length - 1 )
			ret = this.vertexCount - this.vertexOffset[id];
		else
			ret = this.vertexOffset[id + 1] - this.vertexOffset[id];
		return Math.abs( ret );
	}

	public int GetMaterialsCount()
	{
		return this.vertexOffset.length;
	}

	public int[] GetMaterialsOffsets()
	{
		return this.vertexOffset;
	}

}
