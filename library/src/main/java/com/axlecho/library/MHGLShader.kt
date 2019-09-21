package com.axlecho.library

import android.opengl.GLES20
import com.axlecho.library.MHGLUtils.Companion.loadShader


class MHGLShader {

    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;" +
            "varying  vec4 vColor;" +
            "attribute vec4 aColor;" +
            "void main() {" +
            "  gl_Position = vMatrix*vPosition;" +
            "  vColor=aColor;" +
            "}"

    private val fragmentShaderCode = (
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}")

    val program = GLES20.glCreateProgram()
    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        //创建一个空的OpenGLES程序
        GLES20.glAttachShader(program, vertexShader)
        //将片元着色器加入到程序中
        GLES20.glAttachShader(program, fragmentShader)
        //连接到着色器程序
        GLES20.glLinkProgram(program)
    }

    val matrixHandler = GLES20.glGetUniformLocation(program, "vMatrix")
    val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
    val colorHandle = GLES20.glGetAttribLocation(program, "aColor")

}