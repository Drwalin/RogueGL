// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import org.lwjgl.opengl.GL20;
import org.joml.*;

import Shaders.Shader;

public class Uniform4x extends Uniform
{
	private float[] matrixBuffer = new float[16];
	
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
		dvalue.get( this.matrixBuffer );
		GL20.glUniformMatrix4fv( this.GetLocation(), false, this.matrixBuffer );
	}
	
}
