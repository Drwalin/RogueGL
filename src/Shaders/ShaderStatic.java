// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.*;

import Materials.Material;
import Materials.MaterialShineable;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;
import SceneNodes.Light;

public class ShaderStatic extends Shader
{
	private static final String VERTEX_FILE = "res/shaders/shading.vs";
	private static final String FRAGMENT_FILE = "res/shaders/shading.fs";

	private int worldTransformUniform;
	private int viewTransformUniform;
	private int projectionTransformUniform;
	private int combinedTransformUniform;

	private int textureSamplerUniform;
	private int textureSamplerIDtex = 0;

	private int lightPositionUniform;
	private int lightColorUniform;
	private int shineDamperUniform;
	private int reflectivityUniform;
	private int isTwoSidedUniform;
	private int useFakeLightingUniform;

	private int ambientLightColorUniform;

	private int cameraPossitionUniform;

	private Matrix4f fullMatrixTransform = new Matrix4f();

	public ShaderStatic()
	{
		super( ShaderStatic.VERTEX_FILE, ShaderStatic.FRAGMENT_FILE );
	}

	public ShaderStatic( String vertex, String fragment )
	{
		super( vertex, fragment );
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
		this.worldTransformUniform = super.GetUniformLocation( "worldTransform" );
		this.viewTransformUniform = super.GetUniformLocation( "viewTransform" );
		this.projectionTransformUniform = super.GetUniformLocation( "projectionTransform" );
		this.combinedTransformUniform = super.GetUniformLocation( "combinedTransform" );
		this.lightPositionUniform = super.GetUniformLocation( "lightPossition" );
		this.lightColorUniform = super.GetUniformLocation( "lightColor" );
		this.shineDamperUniform = super.GetUniformLocation( "shineDamper" );
		this.reflectivityUniform = super.GetUniformLocation( "reflectivity" );
		this.ambientLightColorUniform = super.GetUniformLocation( "ambientLightColor" );
		this.cameraPossitionUniform = super.GetUniformLocation( "cameraPossition" );
		this.isTwoSidedUniform = super.GetUniformLocation( "isTwoSided" );
		this.useFakeLightingUniform = super.GetUniformLocation( "useFakeLighting" );
		this.textureSamplerUniform = super.GetUniformLocation( "textureSampler" );
		
		super.SetUniform1( this.textureSamplerUniform, this.textureSamplerIDtex );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		super.SetUniform4x4( this.worldTransformUniform, sceneNode.GetTransformationMatrix() );
		Matrix4f.mul( renderer.GetCombinedMatrix(), sceneNode.GetTransformationMatrix(), this.fullMatrixTransform );
		super.SetUniform4x4( this.viewTransformUniform, renderer.GetViewMatrix() );
		super.SetUniform4x4( this.projectionTransformUniform, renderer.GetProjectionMatrix() );
		super.SetUniform4x4( this.combinedTransformUniform, this.fullMatrixTransform );

		this.SetUniform3( this.ambientLightColorUniform, renderer.GetAmbientLightColor() );

		GL13.glActiveTexture( GL13.GL_TEXTURE0 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, material.GetTextures().get( 0 ).GetTextureID() );

		List<Light> lights = renderer.GetLights();
		if( lights.size() > 0 )
		{
			super.SetUniform3( this.lightPositionUniform, lights.get( 0 ).GetLocation() );
			super.SetUniform3( this.lightColorUniform, lights.get( 0 ).GetColor() );
		}

		if( material instanceof MaterialShineable )
		{
			MaterialShineable materialShineable = (MaterialShineable) material;
			super.SetUniform1( this.shineDamperUniform, materialShineable.GetShineDamper() );
			super.SetUniform1( this.reflectivityUniform, materialShineable.GetReflectivity() );
		}

		super.SetUniform3( this.cameraPossitionUniform, renderer.GetCameraLocation() );

		super.SetUniform1( this.isTwoSidedUniform, material.HasTransparency() ? 1 : 0 );
		super.SetUniform1( this.useFakeLightingUniform, material.HasTransparency() ? 1 : 0 );
	}

}
