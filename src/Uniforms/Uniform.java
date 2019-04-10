// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import Shaders.Shader;

public abstract class Uniform
{
	protected Shader shader;
	protected String name;
	protected int regularLocation;
	protected int shadowLocation;
	
	public Uniform( Shader shader, String name )
	{
		this.shader = shader;
		this.name = name;
		this.regularLocation = this.shader.GetRegularUniformLocation( name );
		this.shadowLocation = this.shader.GetShadowUniformLocation( name );
	}
	
	public void Debug()
	{
		System.out.println( "Uniform of " + this.shader + " at location: [ " + this.name + ", " + this.regularLocation + ":" + this.shadowLocation + "]" );
	}
	
	public int GetLocation()
	{
		if( this.shader.IsGeneratingShadows() )
			return this.shadowLocation;
		return this.regularLocation;
	}
	
	@Override
	public void finalize()
	{
		this.shader = null;
	}
	
	public abstract void Set( Object value ) throws ClassCastException;
}
