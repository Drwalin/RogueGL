// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Util;

import org.lwjgl.glfw.GLFW;

public class Mouse
{
	public static float x = 0;
	public static float y = 0;
	public static float z = 0;
	public static float dx = 0;
	public static float dy = 0;
	public static float dz = 0;
	
	public static boolean leftMouse = false;
	public static boolean rightMouse = false;
	public static boolean middleMouse = false;
	
	public static void Update( double x, double y )
	{
		Mouse.dx = (float)x;
		Mouse.dy = (float)y;
	}
	
	public static float GetDX()
	{
		return Mouse.dx - Mouse.x;
	}
	
	public static float GetDY()
	{
		return Mouse.dy - Mouse.y;
	}
	
	public static void NullPosition()
	{
		Mouse.x = Mouse.dx;
		Mouse.y = Mouse.dy;
	}
	
	public static void Update( int action, int button )
	{
		boolean newState = false;
		if( action == GLFW.GLFW_PRESS )
			newState = true;
		switch( action )
		{
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			Mouse.leftMouse = newState;
			break;
		case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
			Mouse.rightMouse = newState;
			break;
		case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
			Mouse.middleMouse = newState;
			break;
		}
	}
}
