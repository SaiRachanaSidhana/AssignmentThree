package com.example.assignmentthree.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.assignmentthree.FirestoreClass.FireStoreClass
import com.example.assignmentthree.R
import com.example.assignmentthree.databinding.ActivityLoginBinding
import com.example.assignmentthree.databinding.ActivitySignupBinding
import com.example.assignmentthree.models.User
import com.example.assignmentthree.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class Login : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var loginmain = findViewById<AppCompatButton>(R.id.btn_login2)

        loginmain.setOnClickListener {
            signInRegisteredUser()
        }
    }

        private fun signInRegisteredUser() {
            // Here we get the text from editText and trim the space
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            if (checkValues(email, password)) {
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))

                // Sign-In using FirebaseAuth
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Calling the FirestoreClass signInUser function to get the data of user from database.
                            FireStoreClass().loadUser(this@Login)
                        } else {
                            hideProgressDialog()
                            Toast.makeText(
                                this@Login,
                                task.exception!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

         fun checkValues(email: String, password: String): Boolean {
            return when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()
                    false
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> {
                    true
                }
            }


    }

    fun signInSuccess(user: User) {

        hideProgressDialog()
        val intent = Intent(this@Login, HomeScreen::class.java)
        intent.putExtra(Constants.USER_ID_INTENT, user.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()

    }

}