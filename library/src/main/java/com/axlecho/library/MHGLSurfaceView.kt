package com.axlecho.library

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent


class MHGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private lateinit var renderer: MHGLRender

    init {
        setEGLContextClientVersion(2)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            renderer.setProgress(event.x)
        }
        return true
    }

    fun setMHRenderer(renderer: MHGLRender) {
        this.renderer = renderer
        setRenderer(renderer)
    }
}