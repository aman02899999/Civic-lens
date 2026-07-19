package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.ui.components.CivicLensErrorDisplay
import com.example.ui.components.ErrorType
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ErrorDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOfflineError_rendersProperly_andTriggersRetryAndBrowse() {
        var retryClicked = false
        var browseClicked = false

        composeTestRule.setContent {
            CivicLensErrorDisplay(
                errorType = ErrorType.OFFLINE,
                onRetry = { retryClicked = true },
                onBrowseOffline = { browseClicked = true }
            )
        }

        // Verify the component itself exists
        composeTestRule.onNodeWithTag("error_display_component").assertExists()

        // Verify the actions buttons exist and can be clicked
        composeTestRule.onNodeWithTag("retry_button").assertExists().performClick()
        assertTrue(retryClicked)

        composeTestRule.onNodeWithTag("offline_browse_button").assertExists().performClick()
        assertTrue(browseClicked)
    }

    @Test
    fun testConfigError_rendersWithoutActions() {
        composeTestRule.setContent {
            CivicLensErrorDisplay(
                errorType = ErrorType.CONFIG_ERROR,
                onRetry = null,
                onBrowseOffline = null
            )
        }

        composeTestRule.onNodeWithTag("error_display_component").assertExists()
        composeTestRule.onNodeWithTag("retry_button").assertDoesNotExist()
        composeTestRule.onNodeWithTag("offline_browse_button").assertDoesNotExist()
    }
}
