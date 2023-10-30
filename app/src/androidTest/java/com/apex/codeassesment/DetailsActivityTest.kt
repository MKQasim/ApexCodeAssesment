package com.apex.codeassesment
import android.provider.ContactsContract
import org.junit.Assert.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.apex.codeassesment.ui.details.DetailsActivity
import com.apex.codeassesment.ui.location.LocationActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */



@RunWith(AndroidJUnit4::class)
class DetailsActivityTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<DetailsActivity> = ActivityScenarioRule(DetailsActivity::class.java)

    @Test
    fun checkDetailsDisplay() {
        // Verify that the user details are displayed correctly
        onView(withId(R.id.details_name)).check(matches(isDisplayed()))
        onView(withId(R.id.details_email)).check(matches(isDisplayed()))
        onView(withId(R.id.details_age)).check(matches(isDisplayed()))
        onView(withId(R.id.details_location)).check(matches(isDisplayed()))
    }


    @Test
    fun clickLocationButton() {
        // Click the location button and capture the intent
        // Verify that the correct intent is sent
        Intents.init()
        onView(withId(R.id.details_location_button)).perform(click())
        // Verify that the correct intent is sent
        intended(IntentMatchers.hasComponent(LocationActivity::class.java.name))
        Intents.release()
    }


}


