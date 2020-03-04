package com.axlecho.mhglgallery

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.axlecho.library.MHGLRender
import com.axlecho.library.MHGLSurfaceView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = MHGLSurfaceView(this)
        val glRenderer = MHGLRender(this,BitmapFactory.decodeStream(resources.assets.open("timg.jpg")))
        glView.setMHRenderer(glRenderer)
        setContentView(glView)
    }
}
