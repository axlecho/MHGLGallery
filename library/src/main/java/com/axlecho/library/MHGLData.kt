package com.axlecho.library

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

private const val GRID = 100
private const val RADIUS = 0.18f

class MHGLVertex {
    var vertexBuffer: FloatBuffer? = null
    private var vertexData = FloatArray((GRID + 1) * (GRID + 1) * 3)
     var curlCirclePosition = 15.0f

    init {
        move()
    }

     fun move() {
        // 计算每个顶点坐标
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 3 * (row * (GRID + 1) + col)
                // vertices[pos + 2] = depth
                // 横向位移比例
                val perc = 1.0f - curlCirclePosition / GRID.toFloat()
                // 横向位移
                val dx = GRID - curlCirclePosition
                // var calc_r = perc * RADIUS
                // if (calc_r > RADIUS)  calc_r = RADIUS
                // 水波半径
                var calc_r = RADIUS * 1
                var mov_x = 0f

                if (perc < 0.20f) calc_r = RADIUS * perc * 5f
                if (perc > 0.05f) mov_x = perc - 0.05f

                val w_h_ratio = 1 - calc_r

                vertexData[pos] = col.toFloat() / GRID.toFloat() * w_h_ratio - mov_x                                        // x
                vertexData[pos + 1] = row.toFloat() / GRID.toFloat()                                                        // y
                vertexData[pos + 2] = (calc_r * Math.sin(3.14 / (GRID * 0.60f) * (col - dx)) + calc_r * 1.1f).toFloat()     // z  Asin(2pi/wav*x)
            }
        val bb = ByteBuffer.allocateDirect(vertexData.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer?.put(vertexData)
        vertexBuffer?.position(0)
    }
}

class MHGLIndex {
    var indexBuffer: ShortBuffer? = null
    val index = ShortArray(GRID * GRID * 6)

    init {
        for (row in 0 until GRID)
            for (col in 0 until GRID) {
                val pos = 6 * (row * GRID + col)

                index[pos] = (row * (GRID + 1) + col).toShort()
                index[pos + 1] = (row * (GRID + 1) + col + 1).toShort()
                index[pos + 2] = ((row + 1) * (GRID + 1) + col).toShort()

                index[pos + 3] = (row * (GRID + 1) + col + 1).toShort()
                index[pos + 4] = ((row + 1) * (GRID + 1) + col + 1).toShort()
                index[pos + 5] = ((row + 1) * (GRID + 1) + col).toShort()
            }


        val cc = ByteBuffer.allocateDirect(index.size * 2)
        cc.order(ByteOrder.nativeOrder())
        indexBuffer = cc.asShortBuffer()
        indexBuffer?.put(index)
        indexBuffer?.position(0)
    }
}

class MHGLTexture {
    private var texture = FloatArray((GRID + 1) * (GRID + 1) * 2)
    val textureBuffer: FloatBuffer

    init {
        for (row in 0..GRID)
            for (col in 0..GRID) {
                val pos = 2 * (row * (GRID + 1) + col)
                texture[pos] = col / GRID.toFloat()
                texture[pos + 1] = 1 - row / GRID.toFloat()
            }
        val byteBuf = ByteBuffer.allocateDirect(texture.size * 4)
        byteBuf.order(ByteOrder.nativeOrder())
        textureBuffer = byteBuf.asFloatBuffer()
        textureBuffer.put(texture)
        textureBuffer.position(0)
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
