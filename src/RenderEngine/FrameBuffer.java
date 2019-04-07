// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class FrameBuffer
{
	private int width;
	private int height;

	private int frameBufferId;
	private int depthTexture;
	private int[] colorsBufferTexture;

	public FrameBuffer( int width, int height, int colorAttachmentCount )
	{
		this.width = width;
		this.height = height;
		this.frameBufferId = FrameBuffer.GenerateFrameBuffer( this.width, this.height, colorAttachmentCount );
		this.depthTexture = FrameBuffer.GenerateDepthBufferTexture( this.width, this.height );
		this.colorsBufferTexture = new int[colorAttachmentCount];
		for( int i = 0; i < this.colorsBufferTexture.length; ++i )
			this.colorsBufferTexture[i] = FrameBuffer.GenerateTexture( this.width, this.height, i );
		this.Unbind();
	}

	public int GetColorTexture( int id )
	{
		return this.colorsBufferTexture[id];
	}

	public int GetDepthTexture()
	{
		return this.depthTexture;
	}

	public void Bind()
	{
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		GL30.glBindFramebuffer( GL30.GL_FRAMEBUFFER, this.frameBufferId );
		GL11.glViewport( 0, 0, this.width, this.height );
	}

	public void Unbind()
	{
		GL30.glBindFramebuffer( GL30.GL_FRAMEBUFFER, 0 );
	}

	public void Destroy()
	{
		GL30.glDeleteFramebuffers( this.frameBufferId );
		for( int id : this.colorsBufferTexture )
			GL11.glDeleteTextures( id );
		GL11.glDeleteTextures( this.depthTexture );
	}

	private static int GenerateFrameBuffer( int width, int height, int colorAttachmentCount )
	{
		int frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer( GL30.GL_FRAMEBUFFER, frameBuffer );
		IntBuffer attachmentBuffer = BufferUtils.createIntBuffer( colorAttachmentCount );
		for( int i = 0; i < colorAttachmentCount; ++i )
			attachmentBuffer.put( i, GL30.GL_COLOR_ATTACHMENT0 + i );
		GL20.glDrawBuffers( attachmentBuffer );
		return frameBuffer;
	}

	private static int GenerateDepthBufferTexture( int width, int height )
	{
		int texture = GL11.glGenTextures();
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture );
		GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
		GL32.glFramebufferTexture( GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0 );
		return texture;
	}

	private static int GenerateTexture( int width, int height, int colorAttachmentId )
	{
		int texture = GL11.glGenTextures();
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, texture );
		GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
		GL32.glFramebufferTexture( GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + colorAttachmentId, texture, 0 );
		return texture;
	}
}
