package com.example.assignmentthree.activities


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentthree.databinding.ActivityMapsBinding
import com.example.assignmentthree.R
import com.example.assignmentthree.FirestoreClass.FireStoreClass
import com.example.assignmentthree.models.User
import com.example.assignmentthree.utils.Constants
import com.example.assignmentthree.utils.GetAddressFromPosition
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private var userData: User? = null
//  To store user details data

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap

    private lateinit var mfusedLocationClient: FusedLocationProviderClient
    //  A variable used to get the current location of the user

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_map)
        binding = ActivityMapsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val id = intent.getStringExtra(
            Constants.USER_ID_INTENT)!!

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this, id)

//  get the user details from the firebase using Id

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//  Initialising the variable using Location Services for getting the current location
        Handler().postDelayed(
            {
//  here using handler to wait until it loads the data from the fire base.
                val supportMapFragment: SupportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//  Creating the variable for the map fragment and using it to load the map
                supportMapFragment.getMapAsync { googleMap ->
                    Log.e("Latitude : ", "${userData!!.latitude}")
                    Log.e("Longitude : ", "${userData!!.longitude}")

//  Here map has been loaded with the user data latitude and longitude

                    val position = LatLng(userData!!.latitude, userData!!.longitude)
                    googleMap.addMarker(
                        MarkerOptions().position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_pin))
                    )
//  Adding the marker on the google map with the position and using the user's icon by converting it into the bitmap from the image

                    val zoomLocation = CameraUpdateFactory.newLatLngZoom(position, 15f)
//  The map will be zoomed to the marked location using camera update factory with 15f as the zoom level.
                    googleMap.animateCamera(zoomLocation)
//  Linking that updated camera settings to the google map

                }
            }, 1000
        )

        binding.etDate.setOnClickListener {
            showDatePicker()
//  show the date picker dialog for selecting the date...
        }


        binding.uploadbutton.setOnClickListener {

            checkLocationPermissions()
//  When we click on the button  then we have to check the location permissions

        }


    }

    private fun showDatePicker() {


        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dpg = DatePickerDialog(
            this, DatePickerDialog.OnDateSetListener { _, year, month, sday ->

                val selectedDate = "$sday/${month + 1}/$year"
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)
                Log.e("The Date @Map ", "${theDate!!.time}")

                userData!!.date = theDate.time
                val reqDate = getDateFromMilliSeconds(theDate.time)
                binding.etDate.setText(reqDate)

            },
            year,
            month,
            day
        )
        dpg.datePicker.maxDate = System.currentTimeMillis() - 86400000
        dpg.show()

    }

    private fun getDateFromMilliSeconds(milliSeconds: Long): String {

        val format = SimpleDateFormat("dd MMM yyyy")
        val dateString = format.format(milliSeconds)
        Log.e("dateString : ", dateString)
        binding.etDate.setText(dateString)
        return dateString

    }

    private fun checkLocationPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION          //checking the permissions using Dexer
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        requestNewLocation()            //if permissions are granted then request for latest current location
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
//      If permissions were not allowed then show the dialog
                }

            }).onSameThread().check()
//  run the dexter on the same thread
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {

        if (isLocationOn()) {
//  Checking whether the location is enabled or not for accessing the current location

            showProgressDialog(resources.getString(R.string.getting_location))

            val locationReq = LocationRequest()
//  Creating the location request object
            locationReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationReq.interval = 1000
            locationReq.numUpdates = 1

//  Setting all the necessary information to the request object

            mfusedLocationClient.requestLocationUpdates(locationReq, locCallBack, Looper.myLooper())
//requesting for the current location using FusedLocationClient object

        } else {
            Toast.makeText(this, "Please Turn On Location", Toast.LENGTH_LONG).show()
//  If Location is not On then notify the user to turn it on using a Toast
        }
    }


    private val locCallBack = object : LocationCallback() {
        //  a variable which contains the latest location method
        override fun onLocationResult(location: LocationResult) {
            super.onLocationResult(location)
            val mLastLoc: Location = location.lastLocation!!
//  accessing the latest location of the user

            val mLatitude = mLastLoc.latitude
            val mLongitude = mLastLoc.longitude
//  extracting the latitude and longitude from it to store them in firebase

            hideProgressDialog()
//            getAddressFromLanLng(mLatitude, mLongitude)

// after getting the lat and lon we need the address to display it.
        }
    }


    private fun isLocationOn(): Boolean {
        val locMananager: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//  a variable to get access the Location manager settings

        return locMananager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locMananager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//  return true if gps or location is enabled
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("You have Cancelled the permissions, You can Allow them in Application Settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }



    fun getUserSuccess(user: User) {
        hideProgressDialog()
//       userData = user
////  after Loading the user details assign it to variable
//        Log.e("Map @Activity ", "Load User Success")
//
////  setting all the required fields with the user data which is loaded from the firebase
//        et_address.setText(userData!!.location)
//        if (userData!!.date > 0) {
//            val reqDate = getDateFromMilliSeconds(userData!!.date)
////  get the date format from the milliseconds
//            et_date.setText(reqDate)
//
////  set the date only if it is valid date
    }

    private fun getAddressFromLanLng(latitude: Double, longitude: Double) {
//  a method which takes lat and lon as parameter and store it in the firebase

        val addressClass = GetAddressFromPosition(this@MapsActivity, latitude, longitude)
// I have created an AsyncTask for getting the address from latlng

//  Set the address listener interface object and get the data from the async task and implement it here
        addressClass.setAddressListener(object : GetAddressFromPosition.AddressListener {

            override fun onAddressFound(address: String?) {
//  On successfully getting the address it will be stored in the firebase
                Log.e("locationAddress", "$address")
                binding.etAddress.setText(address)

                updateUserDetailsToFirebase(latitude, longitude, address!!)
//  A method which takes care of updating the details into the firebase

            }

            override fun onError() {
//  If it unable to load the address then we will log the error

                Toast.makeText(this@MapsActivity, "ERROR", Toast.LENGTH_SHORT).show()
                Log.e("Address : ", "Something went wrong ;;;")
            }

        })
        addressClass.getAddress()
//get the address by executing the Async Task.

    }


    private fun updateUserDetailsToFirebase(latitude: Double, longitude: Double, address: String) {

        val updateUser: User = userData!!
        updateUser.latitude = latitude
        updateUser.longitude = longitude
        updateUser.location = address

        Log.e("Map @updateUser", "Going to update in firebase latlng ${updateUser.name}")
        Log.e("Map @updateUser loc", address)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().updateUser(this@MapsActivity, updateUser)
//  Update the user details latlng and address to the firebase...

    }

    fun updateUserSuccess(user: User) {

        hideProgressDialog()

        showStatusDialog(this, "Location Successfully Updated", userData!!.fcmToken)

// after successfully updating the details show the dialog as success
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}