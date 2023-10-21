package com.apex.codeassesment.ui.location

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
import com.apex.codeassesment.databinding.ActivityLocationBinding


// TODO (Optional Bonus 8 points): Calculate distance between 2 coordinates using phone's location
class LocationActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityLocationBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val latitudeRandomUser = intent.getStringExtra("user-latitude-key")
    val longitudeRandomUser = intent.getStringExtra("user-longitude-key")

    binding.locationRandomUser.text = getString(R.string.location_random_user, latitudeRandomUser, longitudeRandomUser)
    binding.locationCalculateButton.setOnClickListener {
      Toast.makeText(this, "TODO (8): Bonus - Calculate distance between 2 coordinates using phone's location", Toast.LENGTH_SHORT).show()
    }
  }
}
