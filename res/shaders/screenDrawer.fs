
#version 400 core

const int MAX_LIGHT_SOURCES = 16; 

in vec2 texCoord;
in vec2 screenCoord;

uniform sampler2D cameraColorBuffer;
uniform sampler2D cameraDepthBuffer;
uniform sampler2D cameraNormalBuffer;
uniform sampler2D cameraMaterialBuffer;

uniform vec3 cameraPosition;
uniform mat4 cameraMatrix;			// invert( projectionMaterix * viewMatrix )

uniform vec3 lightsPosition[MAX_LIGHT_SOURCES]; 
uniform mat4 lightsMatrix[MAX_LIGHT_SOURCES];		// [i] = projectionMaterix * viewMatrix
uniform vec3 lightsColor[MAX_LIGHT_SOURCES];
uniform vec3 lightsAttenuation[MAX_LIGHT_SOURCES];
//uniform sampler2D lightsDepthBuffer[MAX_LIGHT_SOURCES];

uniform int currentlyUsedLightSorces;

out vec4 fragColor;

/*
float LinearizeDepth(vec2 uv)
{
	float n = 0.1; // camera z near
	float f = 1000; // camera z far
	float z = texture(cameraDepthBuffer, uv).x;
	return (2 * n) / (f + n - z * (f - n));
}
*/

void main( void )
{
	/*
	float d;
	if (texCoord.x < 0.5) // left part
		d = LinearizeDepth(texCoord);
	else // right part
		d = texture(cameraDepthBuffer, texCoord).x;
	fragColor = vec4(d, d, d, 1);
	
	//fragColor = texture( cameraColorBuffer, texCoord );
	*/
	fragColor = vec4( texture( cameraColorBuffer, texCoord ).rgb, 1 );
	//fragColor = vec4( texture( cameraNormalBuffer, texCoord ).xyz*2 - 1, 1 );
}
