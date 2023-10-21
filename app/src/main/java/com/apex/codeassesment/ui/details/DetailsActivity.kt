package com.apex.codeassesment.ui.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.apex.codeassesment.R
import com.apex.codeassesment.data.model.Coordinates
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.databinding.ActivityDetailsBinding
import com.apex.codeassesment.ui.location.LocationActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = intent.getParcelableExtra<User>("saved-user-key")

        // Load the user image with Glide
        if (user?.picture != null) {
            Glide.with(this).load(user?.picture?.large).into(binding.detailsImage)
        }

        val name = user?.name
        binding.detailsName.text = getString(R.string.details_name, name?.first, name?.last)
        binding.detailsEmail.text = getString(R.string.details_email, user?.gender.toString())
        binding.detailsAge.text = user?.dob.toString()
        val coordinates = user?.location?.coordinates
        binding.detailsLocation.text = getString(
            R.string.details_location,
            coordinates?.latitude,
            coordinates?.longitude
        )
        binding.detailsLocationButton.setOnClickListener { navigateLocation(coordinates) }
    }

    private fun navigateLocation(coordinates: Coordinates?) {
        val intent = Intent(this, LocationActivity::class.java)
            .putExtra("user-latitude-key", coordinates?.latitude)
            .putExtra("user-longitude-key", coordinates?.longitude)
        startActivity(intent)
    }
}
