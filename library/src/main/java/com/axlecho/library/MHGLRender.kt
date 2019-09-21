package com.axlecho.library

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MHGLRender : GLSurfaceView.Renderer {
    private val mViewMatrix = FloatArray(16)
    private val mProjectMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private val vertex = MHGLVertex()
    private val index = MHGLIndex()
    private val color = MHGLColor()
    private lateinit var shader: MHGLShader

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(shader.program)

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(shader.matrixHandler, 1, false, mMVPMatrix, 0)
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(shader.positionHandle)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(shader.positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertex.vertexBuffer)
        //设置绘制三角形的颜色
        // GLES20.glEnableVertexAttribArray(shader.colorHandle)
        // GLES20.glVertexAttribPointer(shader.colorHandle, 4, GLES20.GL_FLOAT, false, 0, color.colorBuffer)
        GLES20.glUniform4fv(shader.colorHandle, 1, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f), 0)
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, index.index.size, GLES20.GL_UNSIGNED_SHORT, index.indexBuffer)
        GLES20.glDisableVertexAttribArray(shader.positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height.toFloat()
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        shader = MHGLShader()
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }
}