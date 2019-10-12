uniform mat4 uMVPMatrix;

// the shadow projection matrix
uniform mat4 uShadowProjMatrix;

// position and normal of the vertices
attribute vec4 aPosition;

// to pass on
varying vec4 vShadowCoord;

// 贴图坐标
varying vec2 vTextureCoordinate;
attribute vec2 aTextureCoordinate;

void main() {
     vShadowCoord = uShadowProjMatrix * aPosition;

    vTextureCoordinate=aTextureCoordinate;
    gl_Position = uMVPMatrix * aPosition;
}