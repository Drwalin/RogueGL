// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Materials;

import java.util.List;

import RenderEngine.TextureInstance;
import Shaders.Shader;

public class MaterialShineable extends Material
{
	private float shineDamper;
	private float reflectivity;

	public MaterialShineable( Shader shader, boolean hasTransparency, float shineDamper, float reflectivity, List<TextureInstance> textures2 )
	{
		super( shader, hasTransparency, textures2 );
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}

	public MaterialShineable( Shader shader, boolean hasTransparency, float shineDamper, float reflectivity, TextureInstance... textures2 )
	{
		super( shader, hasTransparency, textures2 );
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}

	public float GetShineDamper()
	{
		return this.shineDamper;
	}

	public float GetReflectivity()
	{
		return this.reflectivity;
	}
}
