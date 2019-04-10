// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.*;

import Materials.Material;
import Materials.MaterialShineable;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;
import Uniforms.*;

public class ShaderStatic extends Shader
{
	private static final String SHADER_PACKAGE_NAME = "shading";

	private Uniform4x worldTransformUniform;
	private Uniform4x viewTransformUniform;
	private Uniform4x projectionTransformUniform;
	private Uniform4x combinedTransformUniform;

	private Uniform1i textureSamplerUniform;
	private int textureSamplerIDtex = 0;
	
	private Uniform1f shineDamperUniform;
	private Uniform1f reflectivityUniform;

	private Matrix4f fullMatrixTransform = new Matrix4f();

	public ShaderStatic()
	{
		super( ShaderStatic.SHADER_PACKAGE_NAME );
	}

	public ShaderStatic( String shaderPackageName )
	{
		super( shaderPackageName );
	}

	@Override
	protected void BindAttributes()
	{
		super.BindAttribute( 0, "position" );
		super.BindAttribute( 1, "textureCoords" );
		super.BindAttribute( 2, "normal" );
	}
	
	@Override
	protected void LoadUniformLocations()
	{
		this.worldTransformUniform = new Uniform4x( this, "worldTransform" );
		this.viewTransformUniform = new Uniform4x( this, "viewTransform" );
		this.projectionTransformUniform = new Uniform4x( this, "projectionTransform" );
		this.combinedTransformUniform = new Uniform4x( this, "combinedTransform" );

		this.textureSamplerUniform = new Uniform1i( this, "textureSampler" );

		this.shineDamperUniform = new Uniform1f( this, "shineDamper" );
		this.reflectivityUniform = new Uniform1f( this, "reflectivity" );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		this.worldTransformUniform.Set( sceneNode.GetTransformationMatrix() );
		Matrix4f.mul( renderer.GetCombinedMatrix(), sceneNode.GetTransformationMatrix(), this.fullMatrixTransform );
		this.viewTransformUniform.Set( renderer.GetViewMatrix() );
		this.projectionTransformUniform.Set( renderer.GetProjectionMatrix() );
		this.combinedTransformUniform.Set( this.fullMatrixTransform );

		GL13.glActiveTexture( GL13.GL_TEXTURE0 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, material.GetTextures().get( 0 ).GetTextureID() );
		this.textureSamplerUniform.Set( this.textureSamplerIDtex );

		if( material instanceof MaterialShineable )
		{
			MaterialShineable materialShineable = (MaterialShineable) material;
			this.shineDamperUniform.Set( materialShineable.GetShineDamper() );
			this.reflectivityUniform.Set( materialShineable.GetReflectivity() );
		}
	}

}
