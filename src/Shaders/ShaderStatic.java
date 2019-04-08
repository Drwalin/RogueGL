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

	private Uniform3f lightPositionUniform;
	private Uniform3f lightColorUniform;
	private Uniform1f shineDamperUniform;
	private Uniform1f reflectivityUniform;
	private Uniform1i isTwoSidedUniform;
	private Uniform1i useFakeLightingUniform;

	private Uniform3f ambientLightColorUniform;

	private Uniform3f cameraPossitionUniform;

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

		this.lightPositionUniform = new Uniform3f( this, "lightPossition" );
		this.lightColorUniform = new Uniform3f( this, "lightColor" );
		this.shineDamperUniform = new Uniform1f( this, "shineDamper" );
		this.reflectivityUniform = new Uniform1f( this, "reflectivity" );
		this.useFakeLightingUniform = new Uniform1i( this, "useFakeLighting" );
		this.ambientLightColorUniform = new Uniform3f( this, "ambientLightColor" );

		this.cameraPossitionUniform = new Uniform3f( this, "cameraPossition" );
		this.isTwoSidedUniform = new Uniform1i( this, "isTwoSided" );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		this.worldTransformUniform.Set( sceneNode.GetTransformationMatrix() );
		Matrix4f.mul( renderer.GetCombinedMatrix(), sceneNode.GetTransformationMatrix(), this.fullMatrixTransform );
		this.viewTransformUniform.Set( renderer.GetViewMatrix() );
		this.projectionTransformUniform.Set( renderer.GetProjectionMatrix() );
		this.combinedTransformUniform.Set( this.fullMatrixTransform );

		this.ambientLightColorUniform.Set( renderer.GetAmbientLightColor() );

		GL13.glActiveTexture( GL13.GL_TEXTURE0 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, material.GetTextures().get( 0 ).GetTextureID() );
		this.textureSamplerUniform.Set( this.textureSamplerIDtex );

		List<Light> lights = renderer.GetLights();
		if( lights.size() > 0 )
		{
			this.lightPositionUniform.Set( lights.get( 0 ).GetLocation() );
			this.lightColorUniform.Set( lights.get( 0 ).GetColor() );
		}

		if( material instanceof MaterialShineable )
		{
			MaterialShineable materialShineable = (MaterialShineable) material;
			this.shineDamperUniform.Set( materialShineable.GetShineDamper() );
			this.reflectivityUniform.Set( materialShineable.GetReflectivity() );
		}

		this.cameraPossitionUniform.Set( renderer.GetCameraLocation() );

		this.isTwoSidedUniform.Set( material.HasTransparency() ? 1 : 0 );
		this.useFakeLightingUniform.Set( material.HasTransparency() ? 1 : 0 );
	}

}
