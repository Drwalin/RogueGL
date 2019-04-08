// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import org.lwjgl.opengl.GL20;

import Shaders.Shader;

public class Uniform1f extends Uniform
{
	public Uniform1f( Shader shader, String name )
	{
		super( shader, name );
	}
	
	@Override
	public void Set( Object value ) throws ClassCastException
	{
		Float dvalue = (Float)value;
		if( value == null )
			throw new ClassCastException( "Cannot cast 'value' to Vector4f while setting OpenGL uniform variable" );
		GL20.glUniform1f( this.GetLocation(), dvalue );
	}
	
}
