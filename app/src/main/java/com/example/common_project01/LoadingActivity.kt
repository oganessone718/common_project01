package com.example.common_project01

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        supportActionBar?.hide()

        // Setting a background image.
        val layout = findViewById<RelativeLayout>(R.id.background_layout)
        layout.setBackgroundResource(R.drawable.loading)

        // Start loading screen.
        loadingStart()
    }

    private fun loadingStart() {
        Handler().postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1800)
    }
}