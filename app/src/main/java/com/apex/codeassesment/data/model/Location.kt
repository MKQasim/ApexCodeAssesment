package com.apex.codeassesment.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Location(
    val street: Coordinates? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val postcode: Long? = null,
    val coordinates: Coordinates? = null,
    val timezone: Timezone? = null
): Parcelable