package com.axlecho.library

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class MHGLVertex {
    var vertexBuffer: FloatBuffer? = null
    private val vertexData = floatArrayOf(
        -1.0f, 1.0f, 1.0f, //正面左上0
        -1.0f, -1.0f, 1.0f, //正面左下1
        1.0f, -1.0f, 1.0f, //正面右下2
        1.0f, 1.0f, 1.0f, //正面右上3
        -1.0f, 1.0f, -1.0f, //反面左上4
        -1.0f, -1.0f, -1.0f, //反面左下5
        1.0f, -1.0f, -1.0f, //反面右下6
        1.0f, 1.0f, -1.0f//反面右上7
    )

    init {
        val bb = ByteBuffer.allocateDirect(vertexData.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer?.put(vertexData)
        vertexBuffer?.position(0)
    }
}


class MHGLIndex {
    var indexBuffer: ShortBuffer? = null
    val index = shortArrayOf(
        6, 7, 4, 6, 4, 5, //后面
        6, 3, 7, 6, 2, 3, //右面
        6, 5, 1, 6, 1, 2, //下面
        0, 3, 2, 0, 2, 1, //正面
        0, 1, 5, 0, 5, 4, //左面
        0, 7, 3, 0, 4, 7//上面
    )

    init {
        val cc = ByteBuffer.allocateDirect(index.size * 2)
        cc.order(ByteOrder.nativeOrder())
        indexBuffer = cc.asShortBuffer()
        indexBuffer?.put(index)
        indexBuffer?.position(0)
    }
}

class MHGLColor {
    var colorBuffer: FloatBuffer? = null
    var color = floatArrayOf(
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f
    )

    init {
        val cc = ByteBuffer.allocateDirect(color.size * 4)
        cc.order(ByteOrder.nativeOrder())
        colorBuffer = cc.asFloatBuffer()
        colorBuffer?.put(color)
        colorBuffer?.position(0)
    }
}
