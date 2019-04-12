// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.*;

import Models.RawModel;

public class LoaderOBJ
{

	public static RawModel Load( String fileName )
	{
		return LoaderOBJ.Load( fileName, false );
	}

	public static RawModel Load( String fileName, boolean useTangent )
	{
		FileReader file = null;
		try
		{
			file = new FileReader( new File( fileName ) );
		} catch( FileNotFoundException e )
		{
			System.out.println( "File not found!" );
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader( file );

		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> uvs = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		float[] verticesArray = null;
		float[] uvsArray = null;
		float[] normalsArray = null;
		float[] tangentsArray = null;
		int[] indicesArray = null;
		int[] materialOfsets = null;

		int combinedVerticesNumber = 0;

		Map<String, Integer> combinedVerticesMap = new HashMap<String, Integer>();
		Map<String, List<Integer>> materialIndices = new HashMap<String, List<Integer>>();
		Map<String, Integer> materialIds = new HashMap<String, Integer>();

		try
		{
			int verticesCount = 0;
			int combinedVerticesCounter = 0;
			String currentMaterial = "_NONE_MATERIAL_INITIAL_";
			line = "a";
			while( line != null )
			{
				line = reader.readLine();
				if( line == null )
					break;
				String[] currentLine = line.split( " " );
				if( currentLine[0].equals( "v" ) )
				{
					Vector3f vertex = new Vector3f( Float.parseFloat( currentLine[1] ), Float.parseFloat( currentLine[2] ), Float.parseFloat( currentLine[3] ) );
					vertices.add( vertex );
				} else if( currentLine[0].equals( "vn" ) )
				{
					Vector3f vertex = new Vector3f( Float.parseFloat( currentLine[1] ), Float.parseFloat( currentLine[2] ), Float.parseFloat( currentLine[3] ) );
					normals.add( vertex );
				} else if( currentLine[0].equals( "vt" ) )
				{
					Vector2f vertex = new Vector2f( Float.parseFloat( currentLine[1] ), Float.parseFloat( currentLine[2] ) );
					uvs.add( vertex );
				} else if( currentLine[0].equals( "usemtl" ) )
				{
					currentMaterial = currentLine[1];
					if( materialIndices.containsKey( currentMaterial ) == false )
					{
						materialIndices.put( currentMaterial, new ArrayList<Integer>() );
					}
					if( materialIds.containsKey( currentMaterial ) == false )
					{
						int s = materialIds.size();
						materialIds.put( currentMaterial, s );
					}
				} else if( currentLine[0].equals( "f" ) )
				{
					for( int i = 1; i < currentLine.length; ++i )
					{
						if( combinedVerticesMap.containsKey( currentLine[i] ) == false )
						{
							combinedVerticesMap.put( currentLine[i], combinedVerticesCounter );
							++combinedVerticesCounter;
						}
						if( i >= 3 )
						{
							materialIndices.get( currentMaterial ).add( combinedVerticesMap.get( currentLine[1] ) );
							materialIndices.get( currentMaterial ).add( combinedVerticesMap.get( currentLine[i - 1] ) );
							materialIndices.get( currentMaterial ).add( combinedVerticesMap.get( currentLine[i] ) );
							verticesCount += 3;
						}
					}
				}
			}

			combinedVerticesNumber = combinedVerticesCounter;

			verticesArray = new float[combinedVerticesNumber * 3];
			uvsArray = new float[combinedVerticesNumber * 2];
			normalsArray = new float[combinedVerticesNumber * 3];
			indicesArray = new int[verticesCount];
			materialOfsets = new int[materialIndices.size()];

			Iterator<Entry<String, Integer>> it = combinedVerticesMap.entrySet().iterator();
			while( it.hasNext() )
			{
				Vector3f t3;
				Vector2f t2;

				Entry<String, Integer> pair = it.next();
				String[] idString = pair.getKey().split( "/" );
				int id = pair.getValue();

				t3 = vertices.get( Integer.parseInt( idString[0] ) - 1 );
				verticesArray[id * 3] = t3.x;
				verticesArray[id * 3 + 1] = t3.y;
				verticesArray[id * 3 + 2] = t3.z;

				t2 = uvs.get( Integer.parseInt( idString[1] ) - 1 );
				uvsArray[id * 2] = t2.x;
				uvsArray[id * 2 + 1] = t2.y;

				t3 = normals.get( Integer.parseInt( idString[2] ) - 1 );
				t3.normalise();
				normalsArray[id * 3] = t3.x;
				normalsArray[id * 3 + 1] = t3.y;
				normalsArray[id * 3 + 2] = t3.z;

				it.remove();
			}

			Map<Integer, String> materialIdToName = new HashMap<Integer, String>();

			it = materialIds.entrySet().iterator();
			while( it.hasNext() )
			{
				Entry<String, Integer> pair = it.next();
				materialIdToName.put( pair.getValue(), pair.getKey() );
				it.remove();
			}

			int lastAdded = 0;
			for( int matID = 0; matID < materialIdToName.size(); ++matID )
			{
				materialOfsets[matID] = lastAdded;
				List<Integer> list = materialIndices.get( materialIdToName.get( matID ) );
				for( int i = 0; i < list.size(); ++i, ++lastAdded )
				{
					indicesArray[lastAdded] = list.get( i );
				}
			}

			if( useTangent )
			{
				tangentsArray = new float[combinedVerticesNumber * 3];

				Vector3f[] tangent = new Vector3f[combinedVerticesNumber];

				for( int i = 0; i < tangent.length; ++i )
					tangent[i] = new Vector3f( 0, 0, 0 );

				Vector3f p1 = new Vector3f(), p2 = new Vector3f(), p3 = new Vector3f();
				Vector2f uv1 = new Vector2f(), uv2 = new Vector2f(), uv3 = new Vector2f();
				Vector2f uvA = new Vector2f(), uvB = new Vector2f();
				Vector3f pA = new Vector3f(), pB = new Vector3f();

				for( int triangleID = 0; triangleID < indicesArray.length; triangleID += 3 )
				{
					int v1i = indicesArray[triangleID];
					int v2i = indicesArray[triangleID + 1];
					int v3i = indicesArray[triangleID + 2];

					p1.x = verticesArray[v1i * 3];
					p1.y = verticesArray[v1i * 3 + 1];
					p1.z = verticesArray[v1i * 3 + 2];
					p2.x = verticesArray[v2i * 3];
					p2.y = verticesArray[v2i * 3 + 1];
					p2.z = verticesArray[v2i * 3 + 2];
					p3.x = verticesArray[v3i * 3];
					p3.y = verticesArray[v3i * 3 + 1];
					p3.z = verticesArray[v3i * 3 + 2];

					uv1.x = uvsArray[v1i * 2];
					uv1.y = uvsArray[v1i * 2 + 1];
					uv2.x = uvsArray[v2i * 2];
					uv2.y = uvsArray[v2i * 2 + 1];
					uv3.x = uvsArray[v3i * 2];
					uv3.y = uvsArray[v3i * 2 + 1];

					Vector3f.sub( p2, p1, pA );
					Vector3f.sub( p3, p1, pB );

					Vector2f.sub( uv2, uv1, uvA );
					Vector2f.sub( uv3, uv1, uvB );

					float r = 1.0F / (uvA.x * uvB.y - uvB.x * uvA.y);
					pA.scale( uvB.y );
					pB.scale( uvA.y );
					Vector3f tang = Vector3f.sub( pA, pB, null );
					tang.scale( r );

					Vector3f.add( tangent[v1i], tang, tangent[v1i] );
					Vector3f.add( tangent[v2i], tang, tangent[v2i] );
					Vector3f.add( tangent[v3i], tang, tangent[v3i] );
				}

				for( int i = 0; i < combinedVerticesNumber; ++i )
				{
					tangent[i].normalise();

					tangentsArray[i * 3] = tangent[i].x;
					tangentsArray[i * 3 + 1] = tangent[i].y;
					tangentsArray[i * 3 + 2] = tangent[i].z;
				}
			}

		} catch( Exception e )
		{
			e.printStackTrace();
		}

		int vaoID = Loader.CreateVAO();
		int[] vboID = new int[4];

		if( useTangent )
			vboID = new int[5];

		vboID[0] = Loader.BindIndicesBuffer( indicesArray );
		vboID[1] = Loader.StoreDataInAttributeList( 0, 3, verticesArray );
		vboID[2] = Loader.StoreDataInAttributeList( 1, 2, uvsArray );
		vboID[3] = Loader.StoreDataInAttributeList( 2, 3, normalsArray );
		if( useTangent )
			vboID[4] = Loader.StoreDataInAttributeList( 3, 3, tangentsArray );
		Loader.UnbindVAO();

		return new RawModel( vaoID, vboID, materialOfsets, indicesArray.length );
	}
}
