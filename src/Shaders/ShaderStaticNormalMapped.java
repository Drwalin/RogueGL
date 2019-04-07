// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import Materials.Material;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;

public class ShaderStaticNormalMapped extends ShaderStatic
{
	private static final String VERTEX_FILE = "res/shaders/normalMapped.vs";
	private static final String FRAGMENT_FILE = "res/shaders/normalMapped.fs";

	private int normalSamplerUniform;
	private int normalSamplerIDtex = 1;
	private int lightAttenuationUniform;

	public ShaderStaticNormalMapped()
	{
		super( ShaderStaticNormalMapped.VERTEX_FILE, ShaderStaticNormalMapped.FRAGMENT_FILE );
	}

	@Override
	protected void BindAttributes()
	{
		super.BindAttributes();
		super.BindAttribute( 3, "tengent" );
	}

	@Override
	protected void LoadUniformLocations()
	{
		super.LoadUniformLocations();
		this.normalSamplerUniform = super.GetUniformLocation( "normalSampler" );
		this.lightAttenuationUniform = super.GetUniformLocation( "lightAttenuation" );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		super.SetUniforms( sceneNode, renderer, material );

		GL13.glActiveTexture( GL13.GL_TEXTURE1 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, material.GetTextures().get( 1 ).GetTextureID() );
		super.SetUniform1( this.normalSamplerUniform, normalSamplerIDtex );

		super.SetUniform3( this.lightAttenuationUniform, renderer.GetLights().get( 0 ).GetAttenuation() );
	}

}
