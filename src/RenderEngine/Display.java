// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.sun.prism.impl.BufferUtil;

import Util.Mouse;
import Util.Keyboard;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.Callback;

import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class Display
{
	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	@SuppressWarnings("unused")
	private static final int FPS_CAP = 60;
	
	public static long window = -1;
	
	public static boolean closeRequest = false;
	
	private static GLCapabilities caps;
	private static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cpCallback;
	private static GLFWMouseButtonCallback mbCallback;
	private static GLFWFramebufferSizeCallback fbCallback;
	private static GLFWWindowSizeCallback wsCallback;
	private static Callback debugProc;
	
	public static boolean isCloseRequested()
	{
		return Display.closeRequest;
	}
	
	public static int getWidth()
	{
		return Display.WIDTH;
	}
	
	public static int getHeight()
	{
		return Display.HEIGHT;
	}
	
	public static void UpdateScreen()
	{
		if( glfwWindowShouldClose( window ) )
			Display.closeRequest = true;
		GL11.glViewport( 0, 0, Display.WIDTH, Display.HEIGHT );
		glfwSwapBuffers( window );
		glfwPollEvents();
	}
	
	public static void Destroy()
	{
		System.out.println( "Destroing window" );
		if( Display.debugProc != null )
			Display.debugProc.free();
		Display.keyCallback.free();
		Display.cpCallback.free();
		Display.mbCallback.free();
		Display.fbCallback.free();
		Display.wsCallback.free();
		GLFW.glfwDestroyWindow( window );
		GLFW.glfwTerminate();
	}
	
	public static void CreateDisplay( String windowName )
	{
		if( !glfwInit() )
			throw new IllegalStateException( "Unable to initialize GLFW" );
		
		glfwDefaultWindowHints();
		glfwWindowHint( GLFW_VISIBLE, GLFW_TRUE );
		glfwWindowHint( GLFW_RESIZABLE, GLFW_FALSE );
		glfwWindowHint( GLFW_SAMPLES, 0 );
		
		/*
		 * long monitor = glfwGetPrimaryMonitor(); GLFWVidMode vidmode =
		 * glfwGetVideoMode( monitor ); if( !windowed ) { width = vidmode.width();
		 * height = vidmode.height(); fbWidth = width; fbHeight = height; }
		 */
		
		glfwWindowHint( GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE );
		
		ByteBuffer byteBuffer = BufferUtil.newByteBuffer( windowName.length() + 1 );
		byteBuffer.put( windowName.getBytes() );
		byteBuffer.put( (byte)0 );
		byteBuffer.flip();
		window = glfwCreateWindow( Display.WIDTH, Display.HEIGHT, byteBuffer, 0L, NULL );
		if( window == NULL )
		{
			throw new AssertionError( "Failed to create the GLFW window" );
		}
		glfwSetCursor( window, glfwCreateStandardCursor( GLFW_CROSSHAIR_CURSOR ) );
		glfwSetInputMode( window, GLFW_CURSOR, GLFW_CURSOR_DISABLED );
		
		glfwSetFramebufferSizeCallback( window, fbCallback = new GLFWFramebufferSizeCallback()
		{
			public void invoke( long window, int width, int height )
			{
				// resize code here
				Display.WIDTH = width;
				Display.HEIGHT = height;
			}
		} );
		glfwSetWindowSizeCallback( window, wsCallback = new GLFWWindowSizeCallback()
		{
			public void invoke( long window, int width, int height )
			{
				Display.WIDTH = width;
				Display.HEIGHT = height;
			}
		} );
		glfwSetKeyCallback( window, null );
		glfwSetCursorPosCallback( window, null );
		
		glfwSetKeyCallback( window, keyCallback = new GLFWKeyCallback()
		{
			public void invoke( long window, int key, int scancode, int action, int mods )
			{
				if( key == GLFW_KEY_UNKNOWN )
					return;
				if( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				{
					glfwSetWindowShouldClose( window, true );
				}
				if( action == GLFW_PRESS )
				{
					Keyboard.keyDown[key] = true;
				}else if( action == GLFW_RELEASE )
				{
					Keyboard.keyDown[key] = false;
				}
			}
		} );
		glfwSetCursorPosCallback( window, cpCallback = new GLFWCursorPosCallback()
		{
			public void invoke( long window, double xpos, double ypos )
			{
				Mouse.Update( xpos, ypos );
			}
		} );
		glfwSetMouseButtonCallback( window, mbCallback = new GLFWMouseButtonCallback()
		{
			public void invoke( long window, int button, int action, int mods )
			{
				Mouse.Update( button, action );
			}
		} );
		
		glfwMakeContextCurrent( window );
		glfwSwapInterval( 1 );
		glfwShowWindow( window );
		
		IntBuffer framebufferSize = BufferUtils.createIntBuffer( 2 );
		nglfwGetFramebufferSize( window, memAddress( framebufferSize ), memAddress( framebufferSize ) + 4 );
		Display.WIDTH = framebufferSize.get( 0 );
		Display.HEIGHT = framebufferSize.get( 1 );
		caps = GL.createCapabilities();
		if( !caps.OpenGL40 )
		{
			throw new AssertionError( "This program requires OpenGL 4.0" );
		}
		// debugProc = GLUtil.setupDebugMessageCallback();
		
		/* Create all needed GL resources */
		
		//GL11.glEnableClientState( GL11.GL_VERTEX_ARRAY );
	}
}
