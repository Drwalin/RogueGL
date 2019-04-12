
package Loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Animations.AnimationSet;
import Animations.Armature;
import Materials.Material;
import Models.RawModel;

public class LoaderCOLLADA
{
	
	static public class TypeData
	{
		public String str;
		public int id;
		public int stride;
		public float[] array;
		
		public TypeData( String str, int id, float[] array, int stride )
		{
			this.str = str;
			this.id = id;
			this.array = array;
			this.stride = stride;
		}
	}
	
	public static RawModel LoadModel( NodeXML rootNode ) throws Exception
	{
		return LoaderCOLLADA.LoadModel( rootNode, false );
	}
	
	public static RawModel LoadModel( NodeXML rootNode, boolean calculateTangents ) throws Exception
	{
		Map<String, List<Float>> vertexData = new HashMap<String, List<Float>>();
		List<Integer> vertexIndices = new ArrayList<Integer>();
		List<Integer> materialOffset = new ArrayList<Integer>();
		
		NodeXML library_geometries = rootNode.GetChild( "library_geometries" );
		if( library_geometries != null )
		{
			List<NodeXML> geometries = library_geometries.GetChildren( "geometry" );
			for( NodeXML geometry : geometries )
			{
				List<NodeXML> meshes = geometry.GetChildren( "mesh" );
				for( NodeXML mesh : meshes )
				{
					NodeXML triangles = mesh.GetChild( "triangles" );
					
					Map<Integer, TypeData> sources = new HashMap<Integer, TypeData>();
					for( NodeXML input : triangles.GetChildren( "input" ) )
					{
						Integer offset = Integer.parseInt( input.GetAttribute( "offset" ) );
						String vertexDataType = input.GetAttribute( "semantic" );
						String sourceName = new String( input.GetAttribute( "source" ).getBytes(), 1, input.GetAttribute( "source" ).getBytes().length - 1 );
						
						NodeXML source = null;
						{
							String sourceName___ = null;
							NodeXML n1 = mesh.GetChildWithAttribute( "vertices", "id", sourceName );
							if( n1 == null )
							{
								source = mesh.GetChildWithAttribute( "source", "id", sourceName );
							}else
							{
								NodeXML n2 = n1.GetChildWithAttribute( "input", "semantic", "POSITION" );
								sourceName___ = n2.GetAttribute( "source" );
								source = mesh.GetChildWithAttribute( "source", "id", new String( sourceName___.getBytes(), 1, sourceName___.getBytes().length - 1 ) );
							}
						}
						NodeXML source_float_array = source.GetChild( "float_array" );
						
						float[] floatArray = new float[Integer.parseInt( source_float_array.GetAttribute( "count" ) )];
						LoaderCOLLADA.Convert( source_float_array.GetData(), floatArray );
						
						Integer stride = Integer.parseInt( source.GetChild( "technique_common" ).GetChild( "accessor" ).GetAttribute( "stride" ) );
						
						sources.put( offset, new TypeData( vertexDataType, offset, floatArray, stride ) );
						
						if( vertexData.containsKey( vertexDataType ) == false )
							vertexData.put( vertexDataType, new ArrayList<Float>() );
					}
					
					int vertexArgumentNumber = sources.size();
					
					int[] trianglesList = new int[Integer.parseInt( triangles.GetAttribute( "count" ) ) * 3 * vertexArgumentNumber]; // 3 - vertices in triangle
					LoaderCOLLADA.Convert( triangles.GetChild( "p" ).GetData(), trianglesList );
					
					materialOffset.add( vertexIndices.size() );
					
					// store vertices data
					for( int i = 0; i * vertexArgumentNumber < trianglesList.length; ++i )
					{
						int id = vertexIndices.size();
						for( Map.Entry<Integer, TypeData> entry : sources.entrySet() )
						{
							List<Float> dst = vertexData.get( entry.getValue().str );
							int stride = entry.getValue().stride;
							int inTriangleId = (i * vertexArgumentNumber) + entry.getValue().id;
							int idsStrmultListtri = trianglesList[inTriangleId] * stride;
							for( int is = 0; is < stride; ++is )
								dst.add( entry.getValue().array[idsStrmultListtri + is] );
						}
						
						// store indices
						vertexIndices.add( id );
					}
				}
			}
		}
		
		// load to VBO and VAO
		{
			int[] indices = new int[vertexIndices.size()];
			int[] vboID = new int[vertexData.size() + 1];
			int vaoID = Loader.CreateVAO();
			for( int i = 0; i < vertexIndices.size(); ++i )
				indices[i] = vertexIndices.get( i );
			vboID[0] = Loader.BindIndicesBuffer( indices );
			
			int universalVertexDataTypes = 0;
			for( Map.Entry<String, List<Float>> entry : vertexData.entrySet() )
			{
				if( entry.getKey().equals( "VERTEX" ) )
					++universalVertexDataTypes;
				if( entry.getKey().equals( "NORMAL" ) )
					++universalVertexDataTypes;
				if( entry.getKey().equals( "TEXCOORD" ) )
					++universalVertexDataTypes;
				if( entry.getKey().equals( "TANGENT" ) )
					++universalVertexDataTypes;
			}
			
			int lastUsed = universalVertexDataTypes - 1;
			for( Map.Entry<String, List<Float>> entry : vertexData.entrySet() )
			{
				int id;
				if( entry.getKey().equals( "VERTEX" ) )
					id = 0;
				else if( entry.getKey().equals( "TEXCOORD" ) )
					id = 1;
				else if( entry.getKey().equals( "NORMAL" ) )
					id = 2;
				else if( entry.getKey().equals( "TANGENT" ) )
					id = 3;
				else
				{
					++lastUsed;
					id = lastUsed;
				}
				float[] arr = new float[entry.getValue().size()];
				for( int i = 0; i < arr.length; ++i )
					arr[i] = entry.getValue().get( i );
				vboID[id + 1] = Loader.StoreDataInAttributeList( id, entry.getKey().equals( "TEXCOORD" ) ? 2 : 3, arr );
			}
			Loader.UnbindVAO();
			
			int[] arr = new int[materialOffset.size()];
			for( int i = 0; i < arr.length; ++i )
				arr[i] = materialOffset.get( i );
			return new RawModel( vaoID, vboID, arr, indices.length );
		}
	}
	
	public static Armature LoadArmature( NodeXML rootNode )
	{
		return null;
	}
	
	public static AnimationSet LoadAnimationSet( NodeXML rootNode )
	{
		return null;
	}
	
	public static List<Material> LoadDefaultMaterialSet( NodeXML rootNode )
	{
		return null;
	}
	
	public static void Convert( String src, int[] dstArray ) throws Exception
	{
		String[] elem = src.split( " " );
		for( int i = 0; i < dstArray.length; ++i )
		{
			dstArray[i] = Integer.parseInt( elem[i] );
		}
	}
	
	public static void Convert( String src, float[] dstArray )
	{
		String[] elem = src.split( " " );
		for( int i = 0; i < dstArray.length; ++i )
		{
			dstArray[i] = Float.parseFloat( elem[i] );
		}
	}
}
