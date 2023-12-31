package com.apex.codeassesment.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class User(
  val gender: String? = null,
  var name: Name? = null,
  var location: Location? = null,
  var email: String? = null,
  val login: Login? = null,
  var dob: Dob? = null,
  val registered: Dob? = null,
  val phone: String? = null,
  val cell: String? = null,
  val id: Id? = null,
  var picture: Picture? = null,
  val nat: String? = null
) : Parcelable {

  // TODO (2 point): Add tests
  companion object {
    fun createRandom(): User {
      return User(
        name = Name(first = randomString(), last = randomString()),
        location = Location(coordinates = Coordinates(randomDouble(), randomDouble())),
        email = randomString() + "@gmail.com",
        dob = Dob(age = 25)
      )
    }

    private fun randomString() = UUID.randomUUID().toString().take(6)
    private fun randomDouble() = Random().nextDouble() * 100
  }
}
