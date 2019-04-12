
package Loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in an XML file. This contains the name of the node, a map
 * of the attributes and their values, any text data between the start and end
 * tag, and a list of all its children nodes.
 * 
 * @author Karl
 * @Modified by Marek Zalewski
 *
 */

public class NodeXML
{
	private String name;
	private Map<String, String> attributes;
	private String data;
	private Map<String, List<NodeXML>> childNodes;
	
	protected NodeXML( String name )
	{
		this.name = name;
	}
	
	public String GetName()
	{
		return this.name;
	}
	
	public String GetData()
	{
		return this.data;
	}
	
	public String GetAttribute( String attr )
	{
		if( this.attributes != null )
		{
			return this.attributes.get( attr );
		}else
		{
			return null;
		}
	}
	
	public NodeXML GetChild( String childName )
	{
		if( this.childNodes != null )
		{
			List<NodeXML> nodes = this.childNodes.get( childName );
			if( nodes != null && !nodes.isEmpty() )
			{
				return nodes.get( 0 );
			}
		}
		return null;
		
	}
	
	public NodeXML GetChildWithAttribute( String childName, String attr, String value )
	{
		List<NodeXML> children = GetChildren( childName );
		if( children == null || children.isEmpty() )
		{
			return null;
		}
		for( NodeXML child : children )
		{
			String val = child.GetAttribute( attr );
			if( value.equals( val ) )
			{
				return child;
			}
		}
		return null;
	}
	
	public List<NodeXML> GetChildren( String name )
	{
		if( this.childNodes != null )
		{
			List<NodeXML> children = this.childNodes.get( name );
			if( children != null )
			{
				return children;
			}
		}
		return new ArrayList<NodeXML>();
	}
	
	protected void AddAttribute( String attr, String value )
	{
		if( this.attributes == null )
		{
			this.attributes = new HashMap<String, String>();
		}
		this.attributes.put( attr, value );
	}
	
	protected void AddChild( NodeXML child )
	{
		if( this.childNodes == null )
		{
			this.childNodes = new HashMap<String, List<NodeXML>>();
		}
		List<NodeXML> list = this.childNodes.get( child.name );
		if( list == null )
		{
			list = new ArrayList<NodeXML>();
			this.childNodes.put( child.name, list );
		}
		list.add( child );
	}
	
	protected void SetData( String data )
	{
		this.data = data;
	}
	
	public void Print( String spaces )
	{
		System.out.println( spaces + this.name + " <"+this.data+"> " + this.attributes );
		if( this.childNodes != null )
		{
			for( Map.Entry<String,List<NodeXML>> l : this.childNodes.entrySet() )
				for( NodeXML node : l.getValue() )
					node.Print( spaces+" " );
		}
	}
}
