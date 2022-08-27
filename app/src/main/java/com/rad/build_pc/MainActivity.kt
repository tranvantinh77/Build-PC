package com.rad.build_pc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rad.build_pc.databinding.ActivityMainBinding
import com.rad.screens_login.LoginActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.btnStart.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnStart -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

        }
    }
}