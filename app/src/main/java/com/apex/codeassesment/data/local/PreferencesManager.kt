package com.apex.codeassesment.data.local

import android.content.Context
import com.apex.codeassesment.ui.main.MainActivity

// TODO (2 point): Add tests
class PreferencesManager(context: Context) {

  private val sharedPreferences = context.getSharedPreferences("random-user-preferences", Context.MODE_PRIVATE)

  fun saveUser(user: String) {
    sharedPreferences.edit().putString("saved-user", user).apply()
  }

  fun loadUser(): String? {
    return sharedPreferences.getString("saved-user", null)
  }
}


