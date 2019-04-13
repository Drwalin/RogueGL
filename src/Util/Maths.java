// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Util;

import org.lwjgl.util.vector.*;

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

	public static Matrix4f CreateTransformMatrix( Vector3f transform, Vector3f rotation, Vector3f scale )
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate( transform, matrix, matrix );
		Matrix4f.scale( scale, matrix, matrix );
		Matrix4f.rotate( rotation.y, Maths.VEC_Y, matrix, matrix );
		Matrix4f.rotate( rotation.x, Maths.VEC_X, matrix, matrix );
		Matrix4f.rotate( rotation.z, Maths.VEC_Z, matrix, matrix );
		return matrix;
	}

}
