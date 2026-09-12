package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Event
import com.example.ui.components.EventCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleEvent = Event(
      id = 1,
      title = "Namma Bengaluru Midnight Marathon 2026",
      category = "Marathon",
      neighborhood = "Cubbon Park",
      venueName = "Sri Kanteerava Stadium",
      venueAddress = "Kasturba Rd, Sampangi Rama Nagara, Bengaluru",
      dateStr = "Oct 24, 2026",
      timeStr = "11:00 PM IST",
      price = 899.0,
      totalCapacity = 1000,
      registeredCount = 420,
      description = "Bengaluru's iconic night-time running festival!",
      perks = "Medal, T-Shirt, RFID Bib",
      rules = "Must be 18+",
      organizerName = "Bengaluru Runners Collective",
      organizerContact = "contact@blrrunners.org",
      bannerType = "marathon"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        EventCard(
          event = sampleEvent,
          onEventClick = {},
          onRegisterClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
