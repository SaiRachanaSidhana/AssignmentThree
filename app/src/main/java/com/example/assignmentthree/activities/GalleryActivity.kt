package com.example.assignmentthree.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignmentthree.R
import com.example.assignmentthree.adaptors.ImagesAdapter
import com.example.assignmentthree.databinding.ActivityGalleryBinding
import com.example.assignmentthree.databinding.ActivityLoginBinding
import com.example.assignmentthree.databinding.StatusImageBinding
import com.example.assignmentthree.models.User
import com.squareup.picasso.Picasso

class GalleryActivity : BaseActivity() {

    private var userData: User? = null
    private lateinit var binding : ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        binding = ActivityGalleryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        var btnuploadgallery=findViewById<AppCompatButton>(R.id.btn_upload_gallery)
        btnuploadgallery.setOnClickListener {
                Toast.makeText(this,"you can upload image",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, UploadImage::class.java))

        }


    }


    fun getUserDataSuccess(user: User) {
        hideProgressDialog()
        userData =
            user                            //on successfully loading the user details the recyclerview has to be load once for updated data
        setUpRV()
        Log.e("Get User Success  ", userData!!.name)
    }

    private fun setUpRV() {

        if (userData!!.images.size > 0) {
//            checking whether the user has images or not

            binding.rvGallery.layoutManager = GridLayoutManager(this, 3)
//            setting he layout for images in recycler view to GridLayout for storing it in a grid manner in the span of 3
//            so that each line will have 3 images through out the recycler view
            binding.rvGallery.setHasFixedSize(true)

//            set the fixed size of the adapter as true

            val adapter = ImagesAdapter(this, userData!!.images)
//            initialise the adapter here
            binding.rvGallery.adapter = adapter

//            attaching the recycler view's adapter to the user adapter

            adapter.setOnClickListener(object : ImagesAdapter.OnItemListener {
//          adding the onclick functionality to the each item of the recycler view using the interface

                override fun onClick(position: Int, item: String) {
//          Inside the onclick method we get the position and image's url here from the adapter
                    Log.e("Inside OnClick() $position", "Success...")

                    Toast.makeText(this@GalleryActivity, "Onclick at $position", Toast.LENGTH_SHORT)
                        .show()

                    showImageDialog(item)

//  A method which is used to display the image in the dialog

                }

            })

        }

    }

    /*fun showImageDialog(imagePath: String) {
        val dialog = Dialog(this)
        val dialogBinding = StatusImageBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        Picasso.get()
            .load(imagePath)
            .resize(380, 256)
            .into(dialogBinding.imgStatusLayout)

        // Close the dialog when clicked
        dialogBinding.imgStatusLayout.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }*/



}