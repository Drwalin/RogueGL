
package RenderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * This class contains all the methods needed to set-up, maintain, and close a
 * LWJGL display.
 * 
 * @author Karl
 * @edited by Marek Zalewski 2019
 *
 */
public class DisplayManager
{
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FPS_CAP = 60;
	private static final String TITLE = "Our First Display";

	public static void CreateDisplay()
	{
		ContextAttribs attribs = new ContextAttribs( 3, 2 ).withForwardCompatible( true ).withProfileCore( true );
		try
		{
			Display.setDisplayMode( new DisplayMode( DisplayManager.WIDTH, DisplayManager.HEIGHT ) );
			Display.create( new PixelFormat(), attribs );
			Display.setTitle( DisplayManager.TITLE );
			Display.setVSyncEnabled( true );
			Display.sync( DisplayManager.FPS_CAP );
		} catch( LWJGLException e )
		{
			e.printStackTrace();
		}
		GL11.glViewport( 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT );
	}

	public static void Update()
	{
		Display.update();
	}

	public static void Destroy()
	{
		Display.destroy();
	}

}
