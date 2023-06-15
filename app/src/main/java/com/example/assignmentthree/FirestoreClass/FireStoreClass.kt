package com.example.assignmentthree.FirestoreClass

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.assignmentthree.activities.*
import com.example.assignmentthree.models.User
import com.example.assignmentthree.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: Signup, user: User) {

        firestore.collection(Constants.USER)
            .document(getCurrentUserID())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {

                activity.registerSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity, "Loading Failed into FStore", Toast.LENGTH_SHORT).show()
            }

    }

    fun loadUser(activity: Login) {
        firestore.collection(Constants.USER)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)!!
                activity.signInSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }

    }

    fun updateUser(activity: Activity, userData: User) {

        val hashMap = HashMap<String, Any>()
        hashMap[Constants.USER_IMAGES] = userData.images
        hashMap[Constants.LATITUDE] = userData.latitude
        hashMap[Constants.LONGITUDE] = userData.longitude
        hashMap[Constants.LOCATION] = userData.location
        hashMap[Constants.FCM_TOKEN] = userData.fcmToken
        if (userData.date > 0)
            hashMap[Constants.DATE] = userData.date

        firestore.collection(Constants.USER)
            .document(userData.id)
            .update(hashMap)
            .addOnSuccessListener {
                if (activity is UploadImage) {
                    activity.userUpdateSuccess(userData)
                }
                if (activity is MapsActivity) {
                    activity.updateUserSuccess(userData)
                }
                if (activity is HomeScreen) {
                    activity.tokenUpdateSuccess()
                }
            }
            .addOnFailureListener {
                if (activity is UploadImage) {
                    activity.hideProgressDialog()

                    activity.showStatusDialog(activity, "Image Failed", userData.fcmToken)

                    Log.e("FS UpdateUser : ", it.message!!)
                }
                if (activity is MapsActivity) {
                    activity.hideProgressDialog()

                    activity.showStatusDialog(activity, "Image Failed", userData.fcmToken)
                    Log.e("FS UpdateUser : ", it.message!!)

                }
                if (activity is HomeScreen) {
                    activity.hideProgressDialog()
                }
            }
    }


    fun getUserDetails(activity: Activity, userId: String) {
        firestore.collection(Constants.USER)
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)!!
                if (activity is HomeScreen) {
                    activity.getUserdataSuccess(user)
                }
                if (activity is GalleryActivity) {
                    activity.getUserDataSuccess(user)
                }
                if (activity is MapsActivity) {
                    activity.getUserSuccess(user)
                }

            }
            .addOnFailureListener {
                if (activity is GalleryActivity) {
                    activity.hideProgressDialog()
                    Log.e("FS GetUser : ", it.message!!)
                }
                if (activity is MapsActivity) {
                    activity.hideProgressDialog()
                    Log.e("FS GetUser : ", it.message!!)
                }
                if (activity is HomeScreen) {
                    activity.hideProgressDialog()
                    Log.e("FS GetUser : ", it.message!!)
                }
            }
    }


    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun updateUserWithHashmap(activity: Activity, userHashMap: HashMap<String, Any>) {

        firestore.collection(Constants.USER)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                if (activity is HomeScreen) {
                    activity.tokenUpdateSuccess()
                }
            }
            .addOnFailureListener {
                if (activity is HomeScreen) {
                    activity.hideProgressDialog()
                    Log.e("Error in Hash Map ", it.message!!)
                }
            }

    }





}