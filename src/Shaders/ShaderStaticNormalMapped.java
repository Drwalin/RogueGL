// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import org.lwjgl.opengl.*;

import Materials.Material;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;
import Uniforms.*;

public class ShaderStaticNormalMapped extends ShaderStatic
{
	private static final String SHADER_PACKAGE_NAME = "normalMapped";

	private Uniform1i normalSamplerUniform;
	private int normalSamplerIDtex = 1;

	public ShaderStaticNormalMapped()
	{
		super( SHADER_PACKAGE_NAME );
	}

	@Override
	protected void BindAttributes()
	{
		super.BindAttribute( 3, "tengent" );
	}

	@Override
	protected void LoadUniformLocations()
	{
		super.LoadUniformLocations();
		this.normalSamplerUniform = new Uniform1i( this, "normalSampler" );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		super.SetUniforms( sceneNode, renderer, material );

		GL13.glActiveTexture( GL13.GL_TEXTURE1 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, material.GetTextures().get( 1 ).GetTextureID() );
		this.normalSamplerUniform.Set( normalSamplerIDtex );
	}

}
