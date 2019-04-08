// This file is part of RogueGL game project
// Copyright (C) 2019 Marek Zalewski aka Drwalin aka DrwalinPCF

#version 400 core

in vec2 texCoord;
in vec3 norm_;
in vec3 tang_;

out vec4 fragColor;
out vec4 fragNormal;
out vec4 fragMaterial;

uniform sampler2D textureSampler;
uniform sampler2D normalSampler;

uniform float shineDamper;
uniform float reflectivity;

vec4 GetNormalVectorFromMap( vec2 coord )
{
	return texture( normalSampler, vec2(coord.x,1-coord.y), -3.11 ) * 2 - 1;
}

void main( void )
{
	vec4 textureColorPoint = texture( textureSampler, vec2(texCoord.x,1-texCoord.y) );
	if( textureColorPoint.a < 0.3 )
		discard;
	
	vec3 tang = normalize( tang_ );
	vec3 norm = normalize( norm_ );
	vec3 bitang = normalize( cross(norm,tang) );
	
	mat3 toTangentSpace = mat3(
		tang.x, tang.y, tang.z,
		bitang.x, bitang.y, bitang.z,
		norm.x, norm.y, norm.z
	);
	
	vec3 unitNormal = normalize( toTangentSpace * GetNormalVectorFromMap(texCoord).xyz );
	
	fragColor = textureColorPoint;
	fragNormal = vec4( unitNormal*0.5 + 0.5, 1 );
	fragMaterial = vec4( shineDamper/32.0, reflectivity/4.0, 0, 1 ); 
}
