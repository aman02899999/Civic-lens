package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CivicLensDatabase
import com.example.data.repository.CivicLensRepository
import com.example.ui.screens.SentimentChartScreen
import com.example.viewmodel.CivicLensViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SentimentChartTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: CivicLensDatabase
    private lateinit var repository: CivicLensRepository
    private lateinit var viewModel: CivicLensViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, CivicLensDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CivicLensRepository(db.civicLensDao())
        viewModel = CivicLensViewModel(context, repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSentimentChart_rendersAndAllowsFiltering() {
        composeTestRule.setContent {
            SentimentChartScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Verify that initial filter tab "all" exists
        composeTestRule.onNodeWithTag("candidate_filter_tab_all").assertExists()
        // Verify we can tap on other candidate filters
        composeTestRule.onNodeWithTag("candidate_filter_tab_narendra_modi").performClick()
        composeTestRule.onNodeWithTag("candidate_filter_tab_rahul_gandhi").performClick()
        composeTestRule.onNodeWithTag("candidate_filter_tab_arvind_kejriwal").performClick()
    }
}
