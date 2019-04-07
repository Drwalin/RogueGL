// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Materials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import RenderEngine.TextureInstance;
import Shaders.Shader;

public class Material
{
	private Shader shader = null;
	private List<TextureInstance> texture = null;
	private boolean hasTransparency;

	public Material( Shader shader, boolean hasTransparency, List<TextureInstance> texture )
	{
		this.shader = shader;
		this.texture = texture;
		this.hasTransparency = hasTransparency;
	}

	public Material( Shader shader, boolean hasTransparency, TextureInstance... texture )
	{
		this.shader = shader;
		this.texture = new ArrayList<TextureInstance>( Arrays.asList( texture ) );
		this.hasTransparency = hasTransparency;
	}

	public Shader GetShader()
	{
		return this.shader;
	}

	public List<TextureInstance> GetTextures()
	{
		return this.texture;
	}

	public boolean HasTransparency()
	{
		return this.hasTransparency;
	}
}
