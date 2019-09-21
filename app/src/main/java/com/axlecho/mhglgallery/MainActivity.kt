package com.axlecho.mhglgallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.axlecho.library.MHGLSurfaceView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = MHGLSurfaceView(this)
        setContentView(glView)
    }
}
