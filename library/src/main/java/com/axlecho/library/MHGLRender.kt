package com.axlecho.library

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MHGLRender(private val bitmap: Bitmap) : GLSurfaceView.Renderer {
    private val mViewMatrix = FloatArray(16)
    private val mProjectMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)


    private lateinit var shader: MHGLShader
    private val page = MHPage()
    private val bg = MHBackground()

    override fun onDrawFrame(gl: GL10?) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(shader.program)


        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(shader.positionHandle)
        GLES20.glEnableVertexAttribArray(shader.textureCoordinate)

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(shader.matrixHandler, 1, false, mMVPMatrix, 0)

        page.draw(bitmap, shader.positionHandle, shader.textureHandle, shader.textureCoordinate)
        bg.draw(bitmap, shader.positionHandle, shader.textureHandle, shader.textureCoordinate)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        // val ratio = width.toFloat() / height.toFloat()
         // GLES20.glViewport(0, 0, width, height)
        val ratio = 1319f / 1868f
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 3f, 50f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 4f, 0.0f, 0.0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        shader = MHGLShader()
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }
}