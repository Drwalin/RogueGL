// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

import Shaders.Shader;

public class Uniform3f extends Uniform
{
	public Uniform3f( Shader shader, String name )
	{
		super( shader, name );
	}
	
	@Override
	public void Set( Object value ) throws ClassCastException
	{
		Vector3f dvalue = (Vector3f)value;
		if( value == null )
			throw new ClassCastException( "Cannot cast 'value' to Vector3f while setting OpenGL uniform variable" );
		GL20.glUniform3f( this.GetLocation(), dvalue.x, dvalue.y, dvalue.z );
	}
	
}
