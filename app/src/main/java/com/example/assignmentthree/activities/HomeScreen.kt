package com.example.assignmentthree.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import com.example.assignmentthree.FirestoreClass.FireStoreClass
import com.example.assignmentthree.R
import com.example.assignmentthree.databinding.ActivityHomeScreenBinding
import com.example.assignmentthree.models.User
import com.example.assignmentthree.utils.Constants
import com.google.firebase.messaging.FirebaseMessaging

class HomeScreen : BaseActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    private var userId: String? = null                      //to get the user id from login activity
    private var userData: User? = null                      //to store the logged in user details
    private lateinit var mSharedPreferences: SharedPreferences      //for storing whether the user has token or not for notifications


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_screen)

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        var btn_location_home=findViewById<AppCompatButton>(R.id.btn_location_home)
        btn_location_home.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }


        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        //getting the shared preferences in private mode so it can be used in this project only

        userId = getCurrentUserID()
        //getting the current userId from the authentication and from base class so that it can be helpful in many situations

        val checkTokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        //checking whether the user has the updated token or not


        if (checkTokenUpdated) {
            Log.e("Token @home ", "checkToken is already updated ....")

            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getUserDetails(this, userId!!)
            //get the user details using the userId from firebase

        } else {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
//          If the user does not have the updated token then get it from the  FirebaseMessaging service
                if (result != null) {
                    updateFCMTokenToFb(result)
//                    updating the token into user in firebase
                }
            }
        }

        Log.e("User @HomeScreen: ", userId!!)


        binding.btnGalleryHome.setOnClickListener {

            val intent = Intent(this, GalleryActivity::class.java)
            intent.putExtra(Constants.USER_ID_INTENT, userId)
            startActivity(intent)

            //transferring the user from home screen to Gallery where all the images will be stored when clicked

        }

        binding.btnLocationHome.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(Constants.USER_ID_INTENT, userId)
            startActivity(intent)

//            transferring to map activity
        }

    }
        fun getUserdataSuccess(user: User) {
            hideProgressDialog()
//        after successfully loading the user details from the database it will be stored in the global variable for future purpose
            userData = user
            Log.e("GetUser @Home", "getting user data success....${userData!!.name}")

        }

    private fun updateFCMTokenToFb(token: String) {

        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] =
            token
        //  if the user does not have the updated token then update it in firebase

        Log.e("update fcm ", "Going to update fcm token in fb $token")

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUserWithHashmap(this@HomeScreen, userHashMap)

//        update the user with token in firebase
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
//        it will be invoked when the token has stored in the firebase successfully

        Log.e("Update success @Home", "token update success")

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this, userId!!)

//        after storing into firebase again load the usr for updated contents

        val editor: SharedPreferences.Editor =
            mSharedPreferences.edit()        //going to edit the shared preferences
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)    //make changes here
        editor.apply()  //apply the changes made

//        here updating the shared preferences such that the user has the updated token

    }





}