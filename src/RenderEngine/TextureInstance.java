// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package RenderEngine;

import org.lwjgl.opengl.GL11;

public class TextureInstance
{
	private int textureID;

	public TextureInstance( int textureID )
	{
		this.textureID = textureID;
	}

	public int GetTextureID()
	{
		return this.textureID;
	}

	public void Destroy()
	{
		GL11.glDeleteTextures( this.textureID );
	}
}
