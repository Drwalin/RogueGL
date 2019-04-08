// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 texCoord;
out vec3 surfaceNormal;

uniform mat4 worldTransform;
uniform mat4 viewTransform;
uniform mat4 projectionTransform;
uniform mat4 combinedTransform;

void main( void )
{
	gl_Position = combinedTransform * vec4(position,1);
	
	texCoord = textureCoords;
	
	surfaceNormal = (worldTransform * vec4(normal,0)).xyz;
}
