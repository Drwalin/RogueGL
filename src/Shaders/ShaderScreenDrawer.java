package Shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import Materials.Material;
import RenderEngine.MasterRenderer;
import RenderEngine.Renderer;
import SceneNodes.DrawableSceneNode;

public class ShaderScreenDrawer extends Shader
{
	private static final String VERTEX_FILE = "res/shaders/screenDrawer.vs";
	private static final String FRAGMENT_FILE = "res/shaders/screenDrawer.fs";
	
	private int cameraColorBufferUniform;
	private final int cameraColorBufferTextureId = 0;
	private int cameraDepthBufferUniform;
	private final int cameraDepthBufferTextureId = 1;
	
	public ShaderScreenDrawer()
	{
		super( VERTEX_FILE, FRAGMENT_FILE );
	}

	@Override
	protected void BindAttributes()
	{
		super.BindAttribute( 0, "position" );
	}

	@Override
	protected void LoadUniformLocations()
	{
		this.cameraColorBufferUniform = super.GetUniformLocation( "cameraColorBuffer" );
		this.cameraDepthBufferUniform = super.GetUniformLocation( "cameraDepthBuffer" );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraColorBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetColorTexture( 0 ) );
		super.SetUniform1( this.cameraColorBufferUniform, this.cameraColorBufferTextureId );

		GL13.glActiveTexture( GL13.GL_TEXTURE0 + this.cameraDepthBufferTextureId );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetDepthTexture() );
		super.SetUniform1( this.cameraDepthBufferUniform, this.cameraDepthBufferTextureId );
	}
}
