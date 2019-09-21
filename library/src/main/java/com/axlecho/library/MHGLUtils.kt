package com.axlecho.library

import android.opengl.GLES20

class MHGLUtils {
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {
            //根据type创建顶点着色器或者片元着色器
            val shader = GLES20.glCreateShader(type)
            //将资源加入到着色器中，并编译
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
    }
}