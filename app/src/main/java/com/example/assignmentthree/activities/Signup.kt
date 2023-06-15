package com.example.assignmentthree.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.assignmentthree.FirestoreClass.FireStoreClass
import com.example.assignmentthree.R
import com.example.assignmentthree.databinding.ActivitySignupBinding
import com.example.assignmentthree.models.User
import com.google.firebase.auth.FirebaseAuth

class Signup : BaseActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
// ...
// Initialize Firebase Auth

//    var btn_login_signup=findViewById<AppCompatButton>(R.id.btn_login_signup)
//    var uname=findViewById<AppCompatEditText>(R.id.uname_signup)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        binding.btnLoginSignup.setOnClickListener{
            val name = binding.unameSignup.text.toString()
            val email = binding.etEmailSignup.text.toString()
            val pass = binding.etPasswordsignup.text.toString()
            val repass=binding.etRepasswordsignup.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()){
                if (pass==repass){
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{

                        if(it.isSuccessful){
                            val fbUser =
                                it.result!!.user                 //getting the registered user here using result
                            Toast.makeText(this, "$name Registered successfully", Toast.LENGTH_SHORT)
                                .show()

                            val user = User(fbUser!!.uid, name, email)

                            showProgressDialog("Please Wait...")
                            FireStoreClass().registerUser(this,user)
                        }
                        else{
                            Toast.makeText(this,it.exception!!.message,Toast.LENGTH_SHORT).show()
                        }

                    }

                }else{
                    Toast.makeText(this,"Password is not matched",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Empty fields are not allowed",Toast.LENGTH_SHORT).show()
            }

        }



    }

    fun registerSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"Registration successfull in fb ",Toast.LENGTH_SHORT).show()

        val intent = Intent(this@Signup, Login::class.java)
        startActivity(intent)
        finish()
    }

}
