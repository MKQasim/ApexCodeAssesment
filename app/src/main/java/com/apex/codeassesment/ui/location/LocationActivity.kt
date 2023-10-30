package com.apex.codeassesment.ui.location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
import com.apex.codeassesment.databinding.ActivityLocationBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import kotlin.math.*

// TODO (Optional Bonus 8 points): Calculate distance between 2 coordinates using phone's location

class LocationViewModel : ViewModel() {
  private var userLocation: Location? = null

  @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
  fun setUserLocation(location: Location) {
    userLocation = location
  }

  fun calculateDistanceToUser(latitude: Double, longitude: Double): Double {
    userLocation?.let { userLoc ->
      val userLat = userLoc.latitude
      val userLng = userLoc.longitude

      val radius = 6371.0 // Earth radius in kilometers (added a decimal point)

      val latDistance = Math.toRadians(latitude - userLat)
      val lngDistance = Math.toRadians(longitude - userLng)

      val a = sin(latDistance / 2).pow(2) + cos(Math.toRadians(userLat)) * cos(Math.toRadians(latitude)) *
              sin(lngDistance / 2).pow(2)
      val c = 2 * atan2(sqrt(a), sqrt(1 - a))

      return radius * c
    }

    return -1.0 // Return a negative value to indicate an error or no user location
  }
}

class LocationActivity : AppCompatActivity() {
  private lateinit var binding: ActivityLocationBinding
  private val viewModel: LocationViewModel by viewModels()
  private val MY_PERMISSIONS_REQUEST_LOCATION = 1001

  @SuppressLint("StringFormatMatches")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLocationBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val latitudeRandomUser = intent.getDoubleExtra("user-latitude-key", 0.0)
    val longitudeRandomUser = intent.getDoubleExtra("user-longitude-key", 0.0)
//    TODO: Request permissions here (not provided in the code)
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      // Request permissions if they are not granted
      ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        MY_PERMISSIONS_REQUEST_LOCATION
      )
    } else {
      // Permissions are already granted or the user has just granted them; you can proceed
      // Mock user's location
      val userLocation = Location("UserLocation")
      userLocation.latitude = 52.5675 // Example latitude for phone location
      userLocation.longitude = 13.3748 // Example longitude for phone location

      viewModel.setUserLocation(userLocation)
      val locationPhoneText = getString(
        R.string.location_phone,
        userLocation.latitude,
        userLocation.longitude
      )
      binding.locationPhone.text = locationPhoneText
      binding.locationRandomUser.text = getString(
        R.string.location_random_user,
        latitudeRandomUser,
        longitudeRandomUser
      )
      binding.locationCalculateButton.setOnClickListener {
        val distance = viewModel.calculateDistanceToUser(latitudeRandomUser, longitudeRandomUser)
        val distanceText = if (distance >= 0) {
          getString(R.string.location_result_miles, distance)
        } else {
          getString(R.string.location_result_miles, "Unable to calculate")
        }
        binding.locationDistance.text = distanceText
        Toast.makeText(this, "Distance to user: $distanceText km", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permissions granted; you can proceed
        // Mock user's location
        val userLocation = Location("UserLocation")
        userLocation.latitude = 52.5675 // Example latitude
        userLocation.longitude = 13.3748 // Example longitude

        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
          ) != PackageManager.PERMISSION_GRANTED
        ) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return
        }
        viewModel.setUserLocation(userLocation)

        val latitudeRandomUser = intent.getDoubleExtra("user-latitude-key", 0.0)
        val longitudeRandomUser = intent.getDoubleExtra("user-longitude-key", 0.0)

        binding.locationRandomUser.text = getString(
          R.string.location_random_user,
          latitudeRandomUser,
          longitudeRandomUser
        )
      } else {
        // Permissions denied; handle the situation
        // You can inform the user or take appropriate action here
      }
    }
  }
}
