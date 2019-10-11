package com.axlecho.library

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class MHRenderProgram(vID: Int, fID: Int, context: Context) {
    companion object {
        private const val TAG = "render"
    }


    private val vertexS = BufferedReader(InputStreamReader(context.resources.openRawResource(vID))).readText()
    private val fragmentS = BufferedReader(InputStreamReader(context.resources.openRawResource(fID))).readText()

    private val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexS)
    private val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentS)
    val program = GLES20.glCreateProgram()


    init {
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, pixelShader)
        GLES20.glLinkProgram(program)

        val status = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            throw RuntimeException("could not link render ${GLES20.glGetProgramInfoLog(program)}")
        }
    }

    private fun loadShader(shaderType: Int, source: String): Int {
        val shader = GLES20.glCreateShader(shaderType)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            Log.v(TAG,source)
            throw RuntimeException("could not compile shader $shaderType")
        }

        return shader
    }
}