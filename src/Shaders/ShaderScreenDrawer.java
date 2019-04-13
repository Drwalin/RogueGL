// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Shaders;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import Materials.Material;
import RenderEngine.MasterRenderer;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;
import Uniforms.*;

public class ShaderScreenDrawer extends Shader
{
	private static final String SHADER_PACKAGE_NAME = "screenDrawer";
	
	public static final int MAX_LIGHT_SOURCES = 16;
	
	private Uniform1i cameraColorBufferUniform;
	private final int cameraColorBufferTextureId = 0;
	private Uniform1i cameraNormalBufferUniform;
	private final int cameraNormalBufferTextureId = 1;
	private Uniform1i cameraFlatNormalBufferUniform;
	private final int cameraFlatNormalBufferTextureId = 2;
	private Uniform1i cameraMaterialBufferUniform;
	private final int cameraMaterialBufferTextureId = 3;
	private Uniform1i cameraDepthBufferUniform;
	private final int cameraDepthBufferTextureId = 4;
	
	private Uniform3f ambientLightUniform;
	
	private Uniform3f cameraPositionUniform;
	private Uniform4x cameraMatrixUniform; // invert( projectionMaterix * viewMatrix )
	
	private UniformArray lightsPositionUniform;
	private UniformArray lightsMatrixUniform; // [i] = projectionMaterix * viewMatrix
	private UniformArray lightsColorUniform;
	private UniformArray lightsDirectionUniform;
	private UniformArray lightsAttenuationUniform;
	private UniformArray lightsDepthBufferUniform;
	
	private Uniform1i currentlyUsedLightSorcesUniform;
	
	private Uniform3f cameraNearFarFovUniform;
	
	public ShaderScreenDrawer()
	{
		super( ShaderScreenDrawer.SHADER_PACKAGE_NAME );
	}
	
	@Override
	protected void BindAttributes()
	{
		super.BindAttribute( 0, "position" );
	}
	
	@Override
	protected void LoadUniformLocations()
	{
		this.cameraColorBufferUniform = new Uniform1i( this, "cameraColorBuffer" );
		this.cameraDepthBufferUniform = new Uniform1i( this, "cameraDepthBuffer" );
		this.cameraNormalBufferUniform = new Uniform1i( this, "cameraNormalBuffer" );
		this.cameraMaterialBufferUniform = new Uniform1i( this, "cameraMaterialBuffer" );
		this.cameraFlatNormalBufferUniform = new Uniform1i( this, "cameraFlatNormalBuffer" );
		
		this.ambientLightUniform = new Uniform3f( this, "ambientLight" );
		
		this.cameraPositionUniform = new Uniform3f( this, "cameraPosition" );
		this.cameraMatrixUniform = new Uniform4x( this, "cameraMatrix" );
		
		this.lightsPositionUniform = new UniformArray( Uniform4f.class, this, "lightsPosition", 16 );
		this.lightsMatrixUniform = new UniformArray( Uniform4x.class, this, "lightsMatrix", 16 );
		this.lightsColorUniform = new UniformArray( Uniform3f.class, this, "lightsColor", 16 );
		this.lightsDirectionUniform = new UniformArray( Uniform3f.class, this, "lightsDirection", 16 );
		this.lightsAttenuationUniform = new UniformArray( Uniform4f.class, this, "lightsAttenuation", 16 );
		this.lightsDepthBufferUniform = new UniformArray( Uniform1i.class, this, "lightsDepthBuffer", 16 );
		this.currentlyUsedLightSorcesUniform = new Uniform1i( this, "currentlyUsedLightSorces" );
		
		this.cameraNearFarFovUniform = new Uniform3f( this, "cameraNearFarFov" );
	}
	
	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer_, Material material )
	{
		MasterRenderer renderer = (MasterRenderer)renderer_;
		
		// Set textures from camera:
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraColorBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCurrentCamera().GetFrameBuffer().GetColorTexture( this.cameraColorBufferTextureId ) );
		this.cameraColorBufferUniform.Set( this.cameraColorBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraNormalBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCurrentCamera().GetFrameBuffer().GetColorTexture( this.cameraNormalBufferTextureId ) );
		this.cameraNormalBufferUniform.Set( this.cameraNormalBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraFlatNormalBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCurrentCamera().GetFrameBuffer().GetColorTexture( this.cameraFlatNormalBufferTextureId ) );
		this.cameraFlatNormalBufferUniform.Set( this.cameraFlatNormalBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraMaterialBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCurrentCamera().GetFrameBuffer().GetColorTexture( this.cameraMaterialBufferTextureId ) );
		this.cameraMaterialBufferUniform.Set( this.cameraMaterialBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraDepthBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCurrentCamera().GetFrameBuffer().GetDepthTexture() );
		this.cameraDepthBufferUniform.Set( this.cameraDepthBufferTextureId );
		
		assert ShaderScreenDrawer.MAX_LIGHT_SOURCES < renderer.GetLightsTransformation().size();
		
		// Set camera data:
		this.cameraPositionUniform.Set( renderer.GetCameraLocation() );
		Matrix4f camMatrix = Matrix4f.mul( (Matrix4f)renderer.GetCurrentCamera().GetViewMatrix().invert(), (Matrix4f)renderer.GetCurrentCamera().GetProjectionMatrix().invert(), null );
		//camMatrix.invert();
		this.cameraMatrixUniform.Set( camMatrix );
		
		// Set lights data:
		this.lightsMatrixUniform.Set( renderer.GetLightsTransformation() );
		this.lightsPositionUniform.Set( renderer.GetLightsPosition() );
		this.lightsColorUniform.Set( renderer.GetLightsColor() );
		this.lightsDirectionUniform.Set(  renderer.GetLightsDirection() );
		this.lightsAttenuationUniform.Set( renderer.GetLightsAttenuation() );
		for( int i = 0; i < renderer.GetLightsDepthBuffers().size(); ++i )
		{
			int depthBuffertextureID = 5 + i;
			GL13.glActiveTexture( GL13.GL_TEXTURE0 + depthBuffertextureID );
			GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetLightsDepthBuffers().get( i ) );
			this.lightsDepthBufferUniform.Set( i, depthBuffertextureID );
		}
		this.currentlyUsedLightSorcesUniform.Set( renderer.GetLightsTransformation().size() );
		
		this.cameraNearFarFovUniform.Set( new Vector3f( renderer.GetCurrentCamera().GetzNear(), renderer.GetCurrentCamera().GetzFar(), renderer.GetCurrentCamera().GetFov() ) );
		this.ambientLightUniform.Set( renderer.GetAmbientLightColor() );
	}
}
