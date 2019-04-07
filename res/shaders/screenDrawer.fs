
#version 400 core

in vec2 texCoord;
in vec2 screenCoord;

uniform sampler2D cameraColorBuffer;
uniform sampler2D cameraDepthBuffer;

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
}
