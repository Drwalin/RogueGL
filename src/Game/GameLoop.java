// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Game;

import org.joml.*;

import Loaders.Loader;
import Materials.*;
import Models.*;
import RenderEngine.*;
import SceneNodes.Camera;
import SceneNodes.DrawableSceneNode;
import SceneNodes.Light;
import Shaders.*;
import Util.Keyboard;

public class GameLoop
{
	public static Light LIGHT;
	
	public static float deltaTime;
	public static long ORIGIN_MILLIS = System.currentTimeMillis();
	
	public static float GetTime()
	{
		return ((float)(System.currentTimeMillis() - GameLoop.ORIGIN_MILLIS)) / 1000.0f;
	}
	
	static boolean wasPressedF = false;
	
	public static void main( String[] args )
	{
		try
		{
			float beginTime, endTime;
			
			Display.CreateDisplay( "RogueLWJGL3" );
			Loader loader = new Loader();
			MasterRenderer renderer = new MasterRenderer();
			ShaderStatic shader = new ShaderStatic();
			ShaderStaticNormalMapped shaderNormalMapped = new ShaderStaticNormalMapped();
			
			renderer.SetAmbientLightColor( new Vector3f( 0.18f, 0.18f, 0.12f ) );
			
			TexturedModel pilarModel = null;
			TexturedModel crateModel = null;
			TexturedModel palmModel = null;
			TexturedModel barrelModel = null;
			TexturedModel multiMatModel = null;
			TexturedModel boulderModel = null;
			TexturedModel citadelModel = null;
			TexturedModel cameraModel = null;
			
			TextureInstance texturePalm = loader.LoadTexture( "fern" );
			TextureInstance textureCrate = loader.LoadTexture( "crate" );
			TextureInstance textureCrateNormal = loader.LoadTexture( "crateNormal" );
			TextureInstance textureBoulder = loader.LoadTexture( "boulder" );
			TextureInstance textureBoulderNormal = loader.LoadTexture( "boulderNormal" );
			TextureInstance textureBarrel = loader.LoadTexture( "barrelTexture" );
			TextureInstance textureBarrelNormal = loader.LoadTexture( "barrelNormal" );
			TextureInstance texturePilar = loader.LoadTexture( "pilarMarbleLightPink" );
			TextureInstance texturePilarNormal = loader.LoadTexture( "pilarNormal" );
			TextureInstance textureRustyMetal = loader.LoadTexture( "Rusty Metal-063" );
			
			TextureInstance textureCameraScreen = loader.LoadTexture( "camera/screen" );
			TextureInstance textureCameraBox = loader.LoadTexture( "camera/box" );
			
			{
				RawModel model = loader.LoadCOLLADA( "CameraTripod", false );
				Material matScreen = new MaterialShineable( shader, true, 4.0f, 0.3f, textureCameraScreen );
				Material matBox = new MaterialShineable( shader, true, 1.0f, 0.03f, textureCameraBox );
				cameraModel = new TexturedModel( model, matBox, matBox, matBox, matBox, matBox, matScreen );
			}
			
			{
				RawModel model = loader.LoadOBJ( "fern" );
				palmModel = new TexturedModel( model, new MaterialShineable( shader, true, 1.0f, 0.03f, texturePalm ) );
			}
			{
				RawModel model = loader.LoadOBJ( "crate", true );
				crateModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 9.0f, .8f, textureCrate, textureCrateNormal ) );
			}
			{
				RawModel model = loader.LoadOBJ( "boulder", true );
				boulderModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 3.0f, .4f, textureBoulder, textureBoulderNormal ) );
			}
			{
				RawModel model = loader.LoadOBJ( "Barrel", true );
				barrelModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 2.f, .7f, textureBarrel, textureBarrelNormal ) );
			}
			{
				RawModel model = loader.LoadCOLLADA( "static/TechDemoMap", true );
				multiMatModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 12.0f, 0.93f, textureCrate, textureCrateNormal ), new MaterialShineable( shaderNormalMapped, false, 11.0f, 0.93f, textureBarrel, textureBarrelNormal ) );
			}
			{
				RawModel model = loader.LoadOBJ( "pilar", true );
				pilarModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 2.f, .7f, texturePilar, texturePilarNormal ) );
			}
			{
				RawModel model = loader.LoadCOLLADA( "Citadel-Tower", true );
				citadelModel = new TexturedModel( model, new MaterialShineable( shader, false, 2.f, .7f, textureRustyMetal ), new MaterialShineable( shader, false, 2.f, .7f, textureCrate ) );
			}
			
			DrawableSceneNode palm1 = new DrawableSceneNode( renderer, palmModel, new Vector3f( 0, 2, 10 ), new Quaternionf(), new Vector3f( 0.2f, 0.2f, 0.2f ) );
			DrawableSceneNode palm2 = new DrawableSceneNode( renderer, palmModel, new Vector3f( 0, 2, 17 ), new Quaternionf(), new Vector3f( 0.2f, 0.2f, 0.2f ) );
			DrawableSceneNode sceneNode = new DrawableSceneNode( renderer, crateModel, new Vector3f( -6, 3, -2 ), new Quaternionf(), new Vector3f( 0.01f, 0.01f, 0.01f ) );
			DrawableSceneNode barrelNode = new DrawableSceneNode( renderer, barrelModel, new Vector3f( 4, 2, 0 ), new Quaternionf(), new Vector3f( 0.1f, 0.1f, 0.1f ) );
			DrawableSceneNode multiMatNode = new DrawableSceneNode( renderer, multiMatModel, new Vector3f( 0, 0, 30 ), new Quaternionf(), new Vector3f( 1, 1, 1 ) );
			DrawableSceneNode pilarNode = new DrawableSceneNode( renderer, pilarModel, new Vector3f( 0, 2, 5 ), new Quaternionf(), new Vector3f( 1, 1, 1 ) );
			DrawableSceneNode boulderNode = new DrawableSceneNode( renderer, boulderModel, new Vector3f( 0, 3, 7 ), new Quaternionf(), new Vector3f( .2f, .2f, .2f ) );
			DrawableSceneNode citadelNode = new DrawableSceneNode( renderer, citadelModel, new Vector3f( -20, 0, 45 ), new Quaternionf().rotateX( -(float)java.lang.Math.PI / 2 ), new Vector3f( .1f, .1f, .1f ) );
			DrawableSceneNode cameraNode = new DrawableSceneNode( renderer, cameraModel, new Vector3f( -10, 2, 25 ), new Quaternionf().rotateX( -(float)java.lang.Math.PI / 2 ), new Vector3f( 1, 1, 1 ) );
			
			renderer.AddSceneNode( palm1 );
			renderer.AddSceneNode( palm2 );
			renderer.AddSceneNode( sceneNode );
			renderer.AddSceneNode( barrelNode );
			renderer.AddSceneNode( multiMatNode );
			renderer.AddSceneNode( pilarNode );
			renderer.AddSceneNode( boulderNode );
			renderer.AddSceneNode( citadelNode );
			renderer.AddSceneNode( cameraNode );
			
			for( int i = 0; i < 100; ++i )
			{
				DrawableSceneNode node = new DrawableSceneNode( renderer, citadelModel, new Vector3f( -20 + (float)java.lang.Math.random() * 800 - 400, 0, 45 + (float)java.lang.Math.random() * 800 - 400 ), new Quaternionf().rotateY( (float)java.lang.Math.random() * (float)java.lang.Math.PI * 2 ).rotateX( -(float)java.lang.Math.PI / 2 ), new Vector3f( (float)java.lang.Math.random() * 0.05f + 0.05f, (float)java.lang.Math.random() * 0.05f + 0.05f, (float)java.lang.Math.random() * 0.05f + 0.05f ) );
				renderer.AddSceneNode( node );
			}
			
			Light light = new Light( 70, 0.1f, 300, new Vector3f( 0, 2.3f, 30 ), new Quaternionf().rotateX( -0.1f ), new Vector3f( 1, 1, 1 ), new Vector3f( 1, .7f, .4f ), new Vector3f( .2f, .001f, .001f ), 10 );
			renderer.AddLight( light );
			GameLoop.LIGHT = light;
			Light light2 = new Light( 80, 0.1f, 300, new Vector3f( 35, 4.3f, 50 ), new Quaternionf().rotateX( -0.2f ), new Vector3f( 1, 1, 1 ), new Vector3f( .4f, .7f, 1 ), new Vector3f( .2f, .001f, .001f ), 0 );
			renderer.AddLight( light2 );
			Light light3 = new Light( 90, 0.1f, 300, new Vector3f( -20,30, 45 ), new Quaternionf().rotateX( -0.8f ), new Vector3f( 1, 1, 1 ), new Vector3f( 1, .4f, .1f ), new Vector3f( .2f, .001f, .001f ), 40 );
			renderer.AddLight( light3 );
			Light light4 = new Light( 80, 0.1f, 300, new Vector3f( 0, -0.3f, 0 ), new Quaternionf(), new Vector3f( 1, 1, 1 ), new Vector3f( 1, .7f, .4f ), new Vector3f( .2f, .001f, .001f ), 10 );
			renderer.AddLight( light4 );
			light4.Disable();
			
			Camera camera = new Camera( 70, 0.1f, 400, new Vector3f( 0, 1, 1 ) );
			renderer.AddCamera( camera );
			renderer.SetMainCamera( camera );
			
			while( !Display.isCloseRequested() )
			{
				beginTime = GameLoop.GetTime();
				
				light3.GetLocalRotation().rotateLocalY( GameLoop.deltaTime * 1.2f );
				light2.GetLocalRotation().rotateLocalY( GameLoop.deltaTime );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_N ) )
					light.SetInnerSpotAngle( light.GetInnerSpotAngle() - 20 * GameLoop.deltaTime );
				if( Keyboard.isKeyDown( Keyboard.KEY_M ) )
					light.SetInnerSpotAngle( light.GetInnerSpotAngle() + 20 * GameLoop.deltaTime );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) )
					break;
				
				if( Keyboard.isKeyDown( Keyboard.KEY_F ) && GameLoop.wasPressedF == false )
				{
					if( light4.GetParent() == null )
					{
						light4.SetLocalLocation( new Vector3f( 0, -1, 0 ) );
						light4.SetLocalRotation( new Quaternionf() );
						camera.AddChild( light4 );
						light4.Enable();
					}
					else
						camera.RemoveChild( light4 );
				}
				GameLoop.wasPressedF = Keyboard.isKeyDown( Keyboard.KEY_F );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_O ) )
					multiMatNode.GetScale().mul( 1.1f );
				if( Keyboard.isKeyDown( Keyboard.KEY_P ) )
					multiMatNode.GetScale().mul( 1 / 1.1f );
				if( multiMatNode.GetScale().length() < 0.01 )
					multiMatNode.GetScale().set( 0.01f, 0.01f, 0.01f );
				
				sceneNode.GetLocalRotation().rotateXYZ( GameLoop.deltaTime, GameLoop.deltaTime*0.5f, GameLoop.deltaTime*0.3f );
				
				camera.Move();
				
				renderer.Render();
				
				endTime = GameLoop.GetTime();
				GameLoop.deltaTime = endTime - beginTime;
				
			}
			
			System.out.println( "Game loop ended" );
			
			shaderNormalMapped.Destroy();
			shader.Destroy();
			loader.Destroy();
			Display.Destroy();
		}catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
