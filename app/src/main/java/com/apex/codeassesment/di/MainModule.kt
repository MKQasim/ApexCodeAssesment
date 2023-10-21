package com.apex.codeassesment.di

import android.content.Context
import android.content.SharedPreferences
import com.apex.codeassesment.data.local.PreferencesManager
import dagger.Module
import dagger.Provides

@Module
object MainModule {

  @Provides
  fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("random-user-preferences", Context.MODE_PRIVATE)
  }

  @Provides
  fun providePreferencesManager(context: Context): PreferencesManager {
    return PreferencesManager(context)
  }
}

