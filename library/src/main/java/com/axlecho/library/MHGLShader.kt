package com.axlecho.library

import android.opengl.GLES20
import com.axlecho.library.MHGLUtils.Companion.loadShader


class MHGLShader {

    private val vertexShaderCode =
        "attribute vec4 vPosition;\n" +
                "attribute vec2 vCoordinate;\n" +
                "uniform mat4 vMatrix;\n" +
                "\n" +
                "varying vec2 aCoordinate;\n" +
                "\n" +
                "void main(){\n" +
                "    gl_Position=vMatrix*vPosition;\n" +
                "    aCoordinate=vCoordinate;\n" +
                "}"

    private val fragmentShaderCode = "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D vTexture;\n" +
            "\n" +
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
            "}"

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

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            val info = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Could not link program: $info")
        }
    }

    val matrixHandler = GLES20.glGetUniformLocation(program, "vMatrix")
    val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
    // val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
    val textureHandle = GLES20.glGetUniformLocation(program, "vTexture")
    val textureCoordinate = GLES20.glGetAttribLocation(program, "vCoordinate")


}