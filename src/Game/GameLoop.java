// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Game;

import org.lwjgl.input.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Loaders.Loader;
import Loaders.LoaderXML;
import Loaders.NodeXML;
import Materials.*;
import Models.*;
import RenderEngine.*;
import SceneNodes.Camera;
import SceneNodes.DrawableSceneNode;
import SceneNodes.Light;
import Shaders.*;

public class GameLoop
{
	public static Light LIGHT;
	
	public static float deltaTime;
	public static long ORIGIN_MILLIS = System.currentTimeMillis();
	
	public static float GetTime()
	{
		return ((float)(System.currentTimeMillis() - GameLoop.ORIGIN_MILLIS)) / 1000.0f;
	}
	
	public static void main( String[] args )
	{
		try
		{
			float beginTime, endTime;
			
			DisplayManager.CreateDisplay();
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
			
			TextureInstance texturePalm = loader.LoadTexture( "fern" );
			TextureInstance textureCrate = loader.LoadTexture( "crate" );
			TextureInstance textureCrateNormal = loader.LoadTexture( "crateNormal" );
			TextureInstance textureBoulder = loader.LoadTexture( "boulder" );
			TextureInstance textureBoulderNormal = loader.LoadTexture( "boulderNormal" );
			TextureInstance textureBarrel = loader.LoadTexture( "barrelTexture" );
			TextureInstance textureBarrelNormal = loader.LoadTexture( "barrelNormal" );
			TextureInstance texturePilar = loader.LoadTexture( "pilarMarbleLightPink" );
			TextureInstance texturePilarNormal = loader.LoadTexture( "pilarNormal" );
			
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
				//RawModel model = loader.LoadOBJ( "TechDemoMap", true );
				RawModel model = loader.LoadCOLLADA( "static/TechDemoMap", true );
				multiMatModel = new TexturedModel( model, new MaterialShineable( shader, false, 7.0f, 0.33f, textureCrate ), new MaterialShineable( shader, false, 8.0f, 0.43f, textureBarrelNormal ) );
			}
			{
				RawModel model = loader.LoadOBJ( "pilar", true );
				pilarModel = new TexturedModel( model, new MaterialShineable( shaderNormalMapped, false, 2.f, .7f, texturePilar, texturePilarNormal ) );
			}
			
			DrawableSceneNode palm1 = new DrawableSceneNode( renderer, palmModel, new Vector3f( 0, 2, 10 ), new Vector3f( 0, 0, 0 ), new Vector3f( 0.2f, 0.2f, 0.2f ) );
			DrawableSceneNode palm2 = new DrawableSceneNode( renderer, palmModel, new Vector3f( 0, 2, 17 ), new Vector3f( 0, 3.14159f, 0 ), new Vector3f( 0.2f, 0.2f, 0.2f ) );
			DrawableSceneNode sceneNode = new DrawableSceneNode( renderer, crateModel, new Vector3f( 0, 0, -10 ), new Vector3f( 0.2f, 0.3f, 0.5f ), new Vector3f( 0.01f, 0.01f, 0.01f ) );
			DrawableSceneNode barrelNode = new DrawableSceneNode( renderer, barrelModel, new Vector3f( 4, 2, 0 ), new Vector3f( 0, 0, 0 ), new Vector3f( 0.1f, 0.1f, 0.1f ) );
			DrawableSceneNode multiMatNode = new DrawableSceneNode( renderer, multiMatModel, new Vector3f( 0, 0, 30 ), new Vector3f( 0, 0, 0 ), new Vector3f( 1, 1, 1 ) );
			DrawableSceneNode pilarNode = new DrawableSceneNode( renderer, pilarModel, new Vector3f( 0, 2, 5 ), new Vector3f( 0, 0, 0 ), new Vector3f( 1, 1, 1 ) );
			DrawableSceneNode boulderNode = new DrawableSceneNode( renderer, boulderModel, new Vector3f( 0, 3, 7 ), new Vector3f( 0, 0, 0 ), new Vector3f( .2f, .2f, .2f ) );
			
			renderer.AddSceneNode( palm1 );
			renderer.AddSceneNode( palm2 );
			renderer.AddSceneNode( sceneNode );
			renderer.AddSceneNode( barrelNode );
			renderer.AddSceneNode( multiMatNode );
			renderer.AddSceneNode( pilarNode );
			renderer.AddSceneNode( boulderNode );
			
			Light light = new Light( 30, 0.1f, 300, new Vector3f( 0, 2.3f, 30 ), new Vector3f( 0.1f, 0, 0 ), new Vector3f( 1, 1, 1 ), new Vector3f( 1, .7f, .4f ), new Vector3f( .2f, .001f, .001f ), 10 );
			renderer.AddLight( light );
			Light light2 = new Light( 80, 0.1f, 300, new Vector3f( 35, 4.3f, 50 ), new Vector3f( 0.2f, 0, 0 ), new Vector3f( 1, 1, 1 ), new Vector3f( .4f, .7f, 1 ), new Vector3f( .2f, .001f, .001f ), 0 );
			renderer.AddLight( light2 );
			GameLoop.LIGHT = light2;
			
			Camera camera = new Camera( 70, 0.1f, 200, new Vector3f( 0, 0, 1 ) );
			
			while( !Display.isCloseRequested() )
			{
				beginTime = GameLoop.GetTime();
				
				Vector3f.add( light2.GetRotation(), new Vector3f(0,GameLoop.deltaTime,0), light2.GetRotation() );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_N ))
					light.SetInnerSpotAngle( light.GetInnerSpotAngle() - 20 * GameLoop.deltaTime );
				if( Keyboard.isKeyDown( Keyboard.KEY_M ))
					light.SetInnerSpotAngle( light.GetInnerSpotAngle() + 20 * GameLoop.deltaTime );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) )
					break;
				
				if( Keyboard.isKeyDown( Keyboard.KEY_O ) )
					multiMatNode.GetScale().scale( 1.1f );
				if( Keyboard.isKeyDown( Keyboard.KEY_P ) )
					multiMatNode.GetScale().scale( 1 / 1.1f );
				if( multiMatNode.GetScale().length() < 0.01 )
					multiMatNode.GetScale().set( 0.01f, 0.01f, 0.01f );
				
				if( Keyboard.isKeyDown( Keyboard.KEY_R ) )
					light.GetRotation().y += GameLoop.deltaTime;
				sceneNode.GetRotation().y += GameLoop.deltaTime;
				sceneNode.GetRotation().x += GameLoop.deltaTime * 0.5f;
				sceneNode.GetRotation().z += GameLoop.deltaTime * 0.3f;
				
				camera.Move();
				
				renderer.Render( camera );
				
				endTime = GameLoop.GetTime();
				GameLoop.deltaTime = endTime - beginTime;
			}
			
			shaderNormalMapped.Destroy();
			shader.Destroy();
			loader.Destroy();
			DisplayManager.Destroy();
		} catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
