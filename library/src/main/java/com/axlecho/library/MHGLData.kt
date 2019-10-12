package com.axlecho.library

import android.graphics.Bitmap
import android.opengl.GLES20
import android.os.SystemClock
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

                vertexData[pos] *= 4.0f
                vertexData[pos + 1] *= 4.0f
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

class MHPage {
    private val vertex = MHGLVertex()
    private val index = MHGLIndex()
    private val color = MHGLColor()
    private val texture = MHGLTexture()


    fun draw(bitmap: Bitmap, positionAttr: Int, textureAttr: Int, textureCoordinate: Int) {
        vertex.curlCirclePosition = 100.0f - (SystemClock.currentThreadTimeMillis().toFloat() / 100.0f)
        vertex.move()
        MHGLLog.v("onDrawFrame move to ${vertex.curlCirclePosition}")
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionAttr, 3, GLES20.GL_FLOAT, false, 0, vertex.vertexBuffer)

        //设置绘制三角形的颜色
        MHGLUtils.loadTexture(bitmap)
        // GLES20.glEnableVertexAttribArray(shader.colorHandle)
        // GLES20.glVertexAttribPointer(shader.colorHandle, 4, GLES20.GL_FLOAT, false, 0, color.colorBuffer)
        // GLES20.glUniform4fv(shader.colorHandle, 1, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f), 0)
        // GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, loadTexture(bitmap))

        GLES20.glUniform1i(textureAttr, 1)
        GLES20.glVertexAttribPointer(textureCoordinate, 2, GLES20.GL_FLOAT, false, 0, texture.textureBuffer)

        // GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.index.size, GLES20.GL_UNSIGNED_SHORT, index.indexBuffer)
    }

    fun drawShadow(positionAttr: Int) {
        vertex.curlCirclePosition = 100.0f - (SystemClock.currentThreadTimeMillis().toFloat() / 100.0f)
        vertex.move()
        MHGLLog.v("onDrawFrame move to ${vertex.curlCirclePosition}")
        GLES20.glVertexAttribPointer(positionAttr, 3, GLES20.GL_FLOAT, false, 0, vertex.vertexBuffer)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.index.size, GLES20.GL_UNSIGNED_SHORT, index.indexBuffer)
    }
}

class MHBackground {
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val textureBuffer: FloatBuffer
    var vertexData = floatArrayOf(
        0f, 4.0f, 0.0f,
        0f, 0f, 0.0f,
        4.0f, 0f, 0.0f,
        4.0f, 4.0f, 0.0f
    )

    var textueData = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f
    )

    var indexData = shortArrayOf(0, 1, 2, 2, 3, 0)

    init {
        val cc = ByteBuffer.allocateDirect(indexData.size * 2)
        cc.order(ByteOrder.nativeOrder())
        indexBuffer = cc.asShortBuffer()
        indexBuffer.put(indexData)
        indexBuffer.position(0)

        val vv = ByteBuffer.allocateDirect(vertexData.size * 4)
        vv.order(ByteOrder.nativeOrder())
        vertexBuffer = vv.asFloatBuffer()
        vertexBuffer.put(vertexData)
        vertexBuffer.position(0)

        val tt = ByteBuffer.allocateDirect(textueData.size * 4)
        tt.order(ByteOrder.nativeOrder())
        textureBuffer = tt.asFloatBuffer()
        textureBuffer.put(textueData)
        textureBuffer.position(0)
    }

    fun draw(bitmap: Bitmap, positionAttr: Int, textureAttr: Int, textureCoordinate: Int) {
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionAttr, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        MHGLUtils.loadTexture(bitmap)

        GLES20.glUniform1i(textureAttr, 1)
        GLES20.glVertexAttribPointer(textureCoordinate, 3, GLES20.GL_FLOAT, false, 0, textureBuffer)


        // GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 2)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
    }

    fun drawShadow(positionAttr: Int) {
        GLES20.glVertexAttribPointer(positionAttr, 3, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
    }
}