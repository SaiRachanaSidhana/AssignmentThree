package com.example.assignmentthree.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.assignmentthree.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn_login=findViewById<AppCompatButton>(R.id.btn_login)
        var btn_signup=findViewById<AppCompatButton>(R.id.btn_signup)

        btn_login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        btn_signup.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }
    }
}