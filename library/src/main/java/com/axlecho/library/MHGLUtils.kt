package com.axlecho.library

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils


class MHGLUtils {
    companion object {
        fun loadShader(type: Int, shaderCode: String): Int {
            // 根据type创建顶点着色器或者片元着色器
            val shader = GLES20.glCreateShader(type)
            //将资源加入到着色器中，并编译
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)


            val compiled = IntArray(1)
            // 检查是否编译成功
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] != GLES20.GL_TRUE) {
                MHGLLog.e("[load shader] failed " + compiled[0])
                GLES20.glDeleteShader(shader)
                return -1
            }
            return shader
        }


        fun loadTexture(bitmap: Bitmap): Int {
            if (bitmap.isRecycled) {
                return 0
            }

            val texture = IntArray(1)
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //激活第0个纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //设置环绕和过滤方式
            //环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
            return texture[0]
        }
    }
}