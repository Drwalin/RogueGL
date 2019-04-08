
package Shaders;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

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
	private Uniform1i cameraDepthBufferUniform;
	private final int cameraDepthBufferTextureId = 1;
	private Uniform1i cameraNormalBufferUniform;
	private final int cameraNormalBufferTextureId = 2;
	private Uniform1i cameraMaterialBufferUniform;
	private final int cameraMaterialBufferTextureId = 3;
	
	private Uniform3f cameraPositionUniform;
	private Uniform4x cameraMatrixUniform;		// invert( projectionMaterix * viewMatrix )
	
	private UniformArray lightsPositionUniform;
	private UniformArray lightsMatrixUniform;			// [i] = projectionMaterix * viewMatrix
	private UniformArray lightsColorUniform;
	private UniformArray lightsAttenuationUniform;
	private UniformArray lightsDepthBufferUniform;
	
	private Uniform1i currentlyUsedLightSorcesUniform;
	
	
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
		
		this.cameraPositionUniform = new Uniform3f( this, "cameraPosition" );
		this.cameraMatrixUniform = new Uniform4x( this,  "cameraMatrix" );
		
		this.lightsPositionUniform = new UniformArray( Uniform3f.class, this, "lightsPosition", 16 );
		this.lightsMatrixUniform = new UniformArray( Uniform4x.class, this, "lightsMatrix", 16  );
		this.lightsColorUniform = new UniformArray( Uniform3f.class, this, "cameraMatrix", 16  );
		this.lightsAttenuationUniform = new UniformArray( Uniform3f.class, this, "cameraMatrix", 16  );
		this.lightsDepthBufferUniform = new UniformArray( Uniform1i.class, this, "lightsDepthBuffer", 16  );
		this.currentlyUsedLightSorcesUniform = new Uniform1i( this, "currentlyUsedLightSorces" );
	}
	
	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer_, Material material )
	{
		MasterRenderer renderer = (MasterRenderer)renderer_;
		
		// Set textures from camera:
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraColorBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetColorTexture( 0 ) );
		this.cameraColorBufferUniform.Set( this.cameraColorBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraDepthBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetDepthTexture() );
		this.cameraDepthBufferUniform.Set( this.cameraDepthBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraNormalBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetColorTexture( 1 ) );
		this.cameraNormalBufferUniform.Set( this.cameraNormalBufferTextureId );
		
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraDepthBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetColorTexture( 2 ) );
		this.cameraMaterialBufferUniform.Set( this.cameraMaterialBufferTextureId );
		
		assert ShaderScreenDrawer.MAX_LIGHT_SOURCES < renderer.GetLightsTransformation().size();
		
		// Set camera data:
		this.cameraPositionUniform.Set( renderer.GetCameraLocation() );
		this.cameraMatrixUniform.Set( Matrix4f.mul( renderer.GetCamera().GetProjectionMatrix(), renderer.GetCamera().GetViewMatrix(), null ) );
		
		// Set lights data:
		this.lightsMatrixUniform.Set( renderer.GetLightsTransformation() );
		this.lightsPositionUniform.Set( renderer.GetLightsPosition() );
		this.lightsColorUniform.Set( renderer.GetLightsColor() );
		this.lightsAttenuationUniform.Set( renderer.GetLightsAttenuation() );
		this.lightsDepthBufferUniform.Set( renderer.GetLightsDepthBuffers() );
		this.currentlyUsedLightSorcesUniform.Set( renderer.GetLightsTransformation().size() );
	}
}
