// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Uniforms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import Shaders.Shader;

public class UniformArray
{
	private final Uniform[] uniforms;
	private final Class<? extends Uniform> type;
	
	public void Debug()
	{
		for( Uniform uni : this.uniforms )
		{
			uni.Debug();
		}
	}
	
	public UniformArray( Class<? extends Uniform> type, Shader shader, String name, int count )
	{
		this.uniforms = new Uniform[count];
		this.type = type;
		try
		{
			Constructor<? extends Uniform> constructor = this.type.getDeclaredConstructor( Shader.class, String.class );
			for( int i = 0; i < count; ++i )
				this.uniforms[i] = constructor.newInstance( shader, name + "[" + i + "]" );
		}catch( InstantiationException e )
		{
			e.printStackTrace();
		}catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}catch( InvocationTargetException e )
		{
			e.printStackTrace();
		}catch( NoSuchMethodException e )
		{
			e.printStackTrace();
		}catch( SecurityException e )
		{
			e.printStackTrace();
		}
	}
	
	public void Set( int id, Object value ) throws ClassCastException
	{
		this.uniforms[id].Set( value );
	}
	
	public void Set( Object value ) throws ClassCastException
	{
		if( value instanceof List )
		{
			@SuppressWarnings("unchecked")
			List<Object> avalue = (List<Object>)value;
			for( int i = 0; i < avalue.size() && i < this.uniforms.length; ++i )
			{
				this.uniforms[i].Set( avalue.get( i ) );
			}
		}
		else
			throw new ClassCastException( "UniformArray.Set(List<Object> avalue) with primitive void UniformArray.Set(Object value) couldn't cast (Object)value to (List<Object>)avalue" );
	}
	
	public void Set( Object[] value ) throws ClassCastException
	{
		for( int i = 0; i < value.length && i < this.uniforms.length; ++i )
		{
			this.uniforms[i].Set( value[i] );
		}
	}
	
}
