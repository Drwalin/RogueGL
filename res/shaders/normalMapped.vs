// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;

out vec2 texCoord;
out vec3 norm_;
out vec3 tang_;

uniform mat4 worldTransform;
uniform mat4 combinedTransform;

uniform vec3 lightPossition;

void main( void )
{
	gl_Position = combinedTransform * vec4(position,1);
	
	texCoord = textureCoords;
	
	norm_ = (worldTransform * vec4(normal,0)).xyz;
	tang_ = (worldTransform * vec4(tangent, 0)).xyz;
}
