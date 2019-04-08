// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import org.lwjgl.opengl.GL20;

import Shaders.Shader;

public class Uniform1i extends Uniform
{
	public Uniform1i( Shader shader, String name )
	{
		super( shader, name );
	}
	
	@Override
	public void Set( Object value ) throws ClassCastException
	{
		Integer dvalue = (Integer)value;
		if( value == null )
			throw new ClassCastException( "Cannot cast 'value' to Vector4f while setting OpenGL uniform variable" );
		GL20.glUniform1i( this.GetLocation(), dvalue );
	}
	
}
