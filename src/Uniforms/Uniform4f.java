// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

import Shaders.Shader;

public class Uniform4f extends Uniform
{
	public Uniform4f( Shader shader, String name )
	{
		super( shader, name );
	}
	
	@Override
	public void Set( Object value ) throws ClassCastException
	{
		Vector4f dvalue = (Vector4f)value;
		if( value == null )
			throw new ClassCastException( "Cannot cast 'value' to Vector4f while setting OpenGL uniform variable" );
		GL20.glUniform4f( this.GetLocation(), dvalue.x, dvalue.y, dvalue.z, dvalue.z );
	}
	
}
