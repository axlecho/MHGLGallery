package com.axlecho.library

import android.content.Context
import android.opengl.GLES20


class MHGLShader(context: Context) {
    private val renderProgram = MHRenderProgram(R.raw.tex, R.raw.frame, context)
    private val shadowProgram = MHRenderProgram(R.raw.v_depth_map, R.raw.f_depth_map, context)

    val render: Int get() = renderProgram.program
    val shadow: Int get() = shadowProgram.program

    val textureHandle = GLES20.glGetUniformLocation(render, "uTexture")
    val textureCoordinate = GLES20.glGetAttribLocation(render, "aTextureCoordinate")

    val scene_mvpMatrixUniform = GLES20.glGetUniformLocation(render, "uMVPMatrix")
    val scene_lightPosUniform = GLES20.glGetUniformLocation(render, "uLightPos")
    val scene_shadowProjMatriUniform = GLES20.glGetUniformLocation(render, "uShadowProjMatrix")
    val scene_textureUniform = GLES20.glGetUniformLocation(render, "uShadowTexture")
    val scene_positionAttribute = GLES20.glGetAttribLocation(render, "aPosition")

    val scene_mapStepXUniform = GLES20.glGetUniformLocation(render, "uxPixelOffset")
    val scene_mapStepYUniform = GLES20.glGetUniformLocation(render, "uyPixelOffset")


    val shadow_mvpMatrixUniform = GLES20.glGetUniformLocation(shadow, "uMVPMatrix")
    val shadow_positionAttribute = GLES20.glGetAttribLocation(shadow, "aShadowPosition")
}