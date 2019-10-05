package com.axlecho.library

import android.content.Context
import android.opengl.GLES20


class MHGLShader(context: Context) {
    private val renderProgram = MHRenderProgram(R.raw.tex, R.raw.frame, context)
    val program: Int get() = renderProgram.program

    val matrixHandler = GLES20.glGetUniformLocation(program, "vMatrix")
    val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
    // val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
    val textureHandle = GLES20.glGetUniformLocation(program, "vTexture")
    val textureCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate")
}