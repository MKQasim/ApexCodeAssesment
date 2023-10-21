package com.apex.codeassesment

import android.app.Application
import com.apex.codeassesment.di.DaggerMainComponent
import com.apex.codeassesment.di.MainComponent

class RandomUserApplication : Application(), MainComponent.Injector {

  override val mainComponent = DaggerMainComponent.factory().create(this)
}