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
	
	private int colorBufferUniform;
	private int colorBufferTextureId = 0;
	
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
		this.colorBufferUniform = super.GetUniformLocation( "colorBuffer" );
		
		super.SetUniform1( this.colorBufferUniform, this.colorBufferTextureId );
	}

	@Override
	public void SetUniforms( DrawableSceneNode sceneNode, Renderer renderer, Material material )
	{
		GL13.glActiveTexture( GL13.GL_TEXTURE0 );
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, renderer.GetCamera().GetFrameBuffer().GetColorTexture( 0 ) );
	}
}
