
package Loaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads an XML file and stores all the data in {@link XmlNode} objects,
 * allowing for easy access to the data contained in the XML file.
 * 
 * @author Karl
 * @modified by Marek Zalewski
 *
 */

public class LoaderXML
{
	
	private static final Pattern DATA = Pattern.compile( ">(.+?)<" );
	private static final Pattern START_TAG = Pattern.compile( "<(.+?)>" );
	private static final Pattern ATTR_NAME = Pattern.compile( "(.+?)=" );
	private static final Pattern ATTR_VAL = Pattern.compile( "\"(.+?)\"" );
	private static final Pattern CLOSED = Pattern.compile( "(</|/>)" );
	
	/**
	 * Reads an XML file and stores all the data in {@link XmlNode} objects,
	 * allowing for easy access to the data contained in the XML file.
	 * 
	 * @param file - the XML file
	 * @return The root node of the XML structure.
	 */
	
	public static NodeXML Load( String file )
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader( new FileReader( file ) );
		}catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}
		
		try
		{
			reader.readLine();
			NodeXML node = LoaderXML.LoadNode( reader );
			reader.close();
			return node;
		}catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 0 );
			return null;
		}
	}
	
	private static NodeXML LoadNode( BufferedReader reader ) throws Exception
	{
		String line = reader.readLine().trim();
		if( line.startsWith( "</" ) )
		{
			return null;
		}
		String[] startTagParts = LoaderXML.GetStartTag( line ).split( " " );
		NodeXML node = new NodeXML( startTagParts[0].replace( "/", "" ) );
		LoaderXML.AddAttributes( startTagParts, node );
		LoaderXML.AddData( line, node );
		if( LoaderXML.CLOSED.matcher( line ).find() )
		{
			return node;
		}
		NodeXML child = null;
		while( (child = LoaderXML.LoadNode( reader )) != null )
		{
			node.AddChild( child );
		}
		return node;
	}
	
	private static void AddData( String line, NodeXML node )
	{
		Matcher matcher = LoaderXML.DATA.matcher( line );
		if( matcher.find() )
		{
			node.SetData( matcher.group( 1 ) );
		}
	}
	
	private static void AddAttributes( String[] titleParts, NodeXML node )
	{
		for( int i = 1; i < titleParts.length; i++ )
		{
			if( titleParts[i].contains( "=" ) )
			{
				LoaderXML.AddAttribute( titleParts[i], node );
			}
		}
	}
	
	private static void AddAttribute( String attributeLine, NodeXML node )
	{
		Matcher nameMatch = LoaderXML.ATTR_NAME.matcher( attributeLine );
		nameMatch.find();
		Matcher valMatch = LoaderXML.ATTR_VAL.matcher( attributeLine );
		valMatch.find();
		node.AddAttribute( nameMatch.group( 1 ), valMatch.group( 1 ) );
	}
	
	private static String GetStartTag( String line )
	{
		Matcher match = LoaderXML.START_TAG.matcher( line );
		match.find();
		return match.group( 1 );
	}
	
}
