package com.axlecho.library

import android.util.Log

class MHGLLog {
    companion object {
        private val TAG = "MHGLGallery"

        fun v(msg: String, t: Throwable? = null) {
            Log.v(TAG, msg, t)
        }

        fun e(msg: String, t: Throwable? = null) {
            Log.e(TAG, msg, t)
        }
    }
}