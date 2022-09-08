package com.rad.screens_splash.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.rad.pc_common.pc_common.utils.Utils
import com.rad.screens_login.LoginActivity
import com.rad.screens_splash.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            Utils.startActivityWithFinish(this@SplashActivity,
                LoginActivity::class.java,
                null)
        }, 2000)
    }
}