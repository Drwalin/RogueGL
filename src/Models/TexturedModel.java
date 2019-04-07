// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

package Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Materials.Material;

public class TexturedModel
{
	private RawModel rawModel;
	private List<Material> materialSet;

	public TexturedModel( RawModel model, List<Material> materialSet )
	{
		this.materialSet = materialSet;
		this.rawModel = model;
	}

	public TexturedModel( RawModel model, Material... materialSet )
	{
		this.materialSet = new ArrayList<Material>( Arrays.asList( materialSet ) );
		this.rawModel = model;
	}

	public RawModel GetRawModel()
	{
		return this.rawModel;
	}

	public List<Material> GetMaterialSet()
	{
		return this.materialSet;
	}
}
