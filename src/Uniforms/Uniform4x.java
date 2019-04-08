// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import Shaders.Shader;

public class Uniform4x extends Uniform
{
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer( 16 );
	
	public Uniform4x( Shader shader, String name )
	{
		super( shader, name );
	}
	
	@Override
	public void Set( Object value ) throws ClassCastException
	{
		Matrix4f dvalue = (Matrix4f)value;
		if( value == null )
			throw new ClassCastException( "Cannot cast 'value' to Vector4f while setting OpenGL uniform variable" );
		dvalue.store( this.matrixBuffer );
		this.matrixBuffer.flip();
		GL20.glUniformMatrix4( this.GetLocation(), false, this.matrixBuffer );
	}
	
}
