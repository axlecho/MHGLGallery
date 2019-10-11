package com.axlecho.library

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MHGLRender(private val context: Context, private val bitmap: Bitmap) : GLSurfaceView.Renderer {
    private lateinit var shader: MHGLShader
    private val page = MHPage()
    private val bg = MHBackground()

    private var displayWidth = 0
    private var displayHeight = 0
    private var shadowMapWidth = 0
    private var shadowMapHeight = 0
    private val fboId = IntArray(1)
    private val depthTextureId = IntArray(1)
    private val rendererTextureID = IntArray(1)

    private val MVPMatrix = FloatArray(16)
    private val MVMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)


    private val modelMatrix = FloatArray(16)
    private val lightMvpMatrix_staticShapes = FloatArray(16)
    private val lightMvpMatrix_dynamicShapes = FloatArray(16)
    private val lightProjectionMatrix = FloatArray(16)
    private val lightViewMatrix = FloatArray(16)
    private val lightPosInEyeSpace = FloatArray(16)
    private val lightPosModel = floatArrayOf(-5f, 9f, 0f, 1f)
    private val actualLightPosition = FloatArray(16)


    override fun onDrawFrame(gl: GL10?) {


        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(shader.scene_positionAttribute)
        GLES20.glEnableVertexAttribArray(shader.textureCoordinate)

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(shader.scene_mvpMatrixUniform, 1, false, MVPMatrix, 0)

        setLight()

        GLES20.glCullFace(GLES20.GL_FRONT)
        renderShadowMap()

        GLES20.glCullFace(GLES20.GL_BACK)
        renderScene()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        displayWidth = width
        displayHeight = height

        //计算宽高比
        // val ratio = width.toFloat() / height.toFloat()
        GLES20.glViewport(0, 0, width, height)
        generateShadowFBO()
        val ratio = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 100f)
        Matrix.frustumM(lightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio, -1.1f, 1.1f, 1f, 100f)

//        //设置透视投影
//        Matrix.frustumM(projectionMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 3f, 50f)
//        //计算变换矩阵
//        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
//        Matrix.frustumM(lightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio, -1.1f, 1.1f, 1f, 100f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, 4f, 0.0f, 0.0f, 0f, 0f, 1.0f, 0.0f)

        shader = MHGLShader(context)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    private fun generateShadowFBO() {
        shadowMapWidth = displayWidth
        shadowMapHeight = displayHeight

        GLES20.glGenFramebuffers(1, fboId, 0)

        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, shadowMapWidth, shadowMapHeight)

        GLES20.glGenTextures(1, rendererTextureID, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rendererTextureID[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0])

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null)
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, rendererTextureID[0], 0)

        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw  RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO")
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    private fun setLight() {
        val elapsedMilliSec = SystemClock.elapsedRealtime()
        val rotationCounter = elapsedMilliSec % 12000L
        val lightRotationDegree = 360f / 12000f * rotationCounter.toFloat()

        val rotationMatrix = FloatArray(16)
        Matrix.setIdentityM(rotationMatrix, 0)
        // Matrix.rotateM(rotationMatrix, 0, lightRotationDegree, 0f, 1f, 0f)
        Matrix.multiplyMV(actualLightPosition, 0, rotationMatrix, 0, lightPosModel, 0)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setLookAtM(
            lightViewMatrix, 0,
            actualLightPosition[0], actualLightPosition[1], actualLightPosition[2],
            actualLightPosition[0], -actualLightPosition[1], actualLightPosition[2],
            -actualLightPosition[0], 0f, 0f
        )
    }

    private fun renderShadowMap() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0])
        GLES20.glUseProgram(shader.shadow)

        GLES20.glViewport(0, 0, shadowMapWidth, shadowMapHeight)
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        val tempResultMatrix = FloatArray(16)
        Matrix.multiplyMM(lightMvpMatrix_staticShapes, 0, lightViewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(tempResultMatrix, 0, lightProjectionMatrix, 0, lightMvpMatrix_staticShapes, 0)
        System.arraycopy(tempResultMatrix, 0, lightMvpMatrix_staticShapes, 0, 16)
        GLES20.glUniformMatrix4fv(shader.shadow_mvpMatrixUniform, 1, false, lightMvpMatrix_staticShapes, 0)
        page.drawShadow(shader.shadow_positionAttribute)
    }

    private fun renderScene() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(shader.render)
        GLES20.glViewport(0, 0, displayWidth, displayHeight)

        GLES20.glUniform1f(shader.scene_mapStepXUniform, 1f / shadowMapWidth.toFloat())
        GLES20.glUniform1f(shader.scene_mapStepYUniform, 1f / shadowMapHeight.toFloat())

        val tempResultMatrix = FloatArray(16)
        val bias = floatArrayOf(
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
        )

        val depthBiasMVP = FloatArray(16)
        Matrix.multiplyMM(tempResultMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        System.arraycopy(tempResultMatrix, 0, MVMatrix, 0, 16)
        // GLES20.glUniformMatrix4fv(scene_mvMatrixUniform, 1, false, MVMatrix, 0)

        Matrix.multiplyMM(tempResultMatrix, 0, projectionMatrix, 0, MVMatrix, 0)
        System.arraycopy(tempResultMatrix, 0, MVPMatrix, 0, 16)
        GLES20.glUniformMatrix4fv(shader.scene_mvpMatrixUniform, 1, false, MVPMatrix, 0)

        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, actualLightPosition, 0)
        GLES20.glUniform3f(shader.scene_lightPosUniform, lightPosInEyeSpace[0], lightPosInEyeSpace[1], lightPosInEyeSpace[2])

        Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, lightMvpMatrix_staticShapes, 0)
        System.arraycopy(depthBiasMVP, 0, lightMvpMatrix_staticShapes, 0, 16)
        GLES20.glUniformMatrix4fv(shader.scene_shadowProjMatriUniform, 1, false, lightMvpMatrix_staticShapes, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rendererTextureID[0])
        GLES20.glUniform1i(shader.scene_textureUniform, 1)

         page.draw(bitmap, shader.scene_positionAttribute, shader.textureHandle, shader.textureCoordinate)
         bg.draw(bitmap, shader.scene_positionAttribute, shader.textureHandle, shader.textureCoordinate)
    }
}