// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Util;

import org.joml.*;

public class Maths
{
	public static final Vector3f VEC_X = new Vector3f( 1, 0, 0 );
	public static final Vector3f VEC_Y = new Vector3f( 0, 1, 0 );
	public static final Vector3f VEC_Z = new Vector3f( 0, 0, 1 );
	public static final Vector3f VEC_RIGHT = VEC_X;
	public static final Vector3f VEC_UP = VEC_Y;
	public static final Vector3f VEC_FORWARD = new Vector3f( 0, 0, -1 );
	public static final Vector3f VEC_LEFT = new Vector3f( -1, 0, 0 );
	public static final Vector3f VEC_DOWN = new Vector3f( 0, -1, 0 );
	public static final Vector3f VEC_BACKWARD = VEC_Z;
	
	public static void CreateTransformMatrix( Vector3f transform, Vector3f rotation, Vector3f scale, Matrix4f matrix )
	{
		matrix.identity();
		matrix.translate( transform );
		matrix.scale( scale );
		matrix.rotate( rotation.y, Maths.VEC_Y );
		matrix.rotate( rotation.x, Maths.VEC_X );
		matrix.rotate( rotation.z, Maths.VEC_Z );
	}

	public static Matrix4f CreateTransformMatrix( Vector3f transform, Vector3f rotation, Vector3f scale )
	{
		Matrix4f matrix = new Matrix4f();
		Maths.CreateTransformMatrix( transform, rotation, scale, matrix );
		return matrix;
	}
	
	public static void GetEulerAngles( Matrix4f m, Vector3f euler )
	{
		euler.x = (float)java.lang.Math.atan2( (double)m.m12(), (double)m.m22() );
		double c2 = java.lang.Math.sqrt( (double)(m.m00()*m.m00() + m.m01()*m.m01()) );
		euler.y = (float)java.lang.Math.atan2( -(double)m.m02(), c2 );
		double s1 = java.lang.Math.sin( euler.x );
		double c1 = java.lang.Math.cos( euler.x );
		euler.z = (float)java.lang.Math.atan2( s1*(double)m.m20() - c1*(double)m.m10(), c1*(double)m.m11() - s1*(double)m.m21() );
	}

	public static Vector3f GetEulerAngles( Matrix4f m )
	{
		Vector3f euler = new Vector3f();
		Maths.GetEulerAngles( m, euler );
		return euler;
	}
}
