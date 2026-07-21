package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.local.CivicLensDatabase
import com.example.data.repository.CivicLensRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CivicLensViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup Local Room DB and Constructor Injection DI
        val database = Room.databaseBuilder(
            applicationContext,
            CivicLensDatabase::class.java,
            "civic_lens_database"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
        
        val dao = database.civicLensDao()
        val repository = CivicLensRepository(dao)
        val viewModel = CivicLensViewModel(application, repository)

        setContent {
            val currentTemplate by viewModel.currentTemplate.collectAsState()
            MyApplicationTheme(templateName = currentTemplate) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        enterTransition = {
                            fadeIn(animationSpec = tween(220)) +
                                slideInHorizontally(animationSpec = tween(220)) { it / 6 }
                        },
                        exitTransition = {
                            fadeOut(animationSpec = tween(180)) +
                                slideOutHorizontally(animationSpec = tween(180)) { -it / 6 }
                        },
                        popEnterTransition = {
                            fadeIn(animationSpec = tween(220)) +
                                slideInHorizontally(animationSpec = tween(220)) { -it / 6 }
                        },
                        popExitTransition = {
                            fadeOut(animationSpec = tween(180)) +
                                slideOutHorizontally(animationSpec = tween(180)) { it / 6 }
                        }
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToAssistant = { navController.navigate("assistant") },
                                onNavigateToCompare = { navController.navigate("compare") },
                                onNavigateToSchemes = { navController.navigate("schemes") },
                                onNavigateToConstituency = { navController.navigate("constituency") },
                                onNavigateToNews = { navController.navigate("news") },
                                onNavigateToResearch = { navController.navigate("research") },
                                onNavigateToBookmarks = { navController.navigate("bookmarks") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToSentiment = { navController.navigate("sentiment_chart") },
                                onNavigateToLegal = { navController.navigate("legal_rights") }
                            )
                        }

                        composable("assistant") {
                            AiAssistantScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("compare") {
                            CompareScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAssistant = { navController.navigate("assistant") }
                            )
                        }

                        composable("schemes") {
                            SchemesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAssistant = { navController.navigate("assistant") }
                            )
                        }

                        composable("constituency") {
                            ConstituencyScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAssistant = { navController.navigate("assistant") }
                            )
                        }

                        composable("news") {
                            NewsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAssistant = { navController.navigate("assistant") }
                            )
                        }

                        composable("bookmarks") {
                            BookmarksScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToSchemes = { navController.navigate("schemes") },
                                onNavigateToNews = { navController.navigate("news") },
                                onNavigateToCompare = { navController.navigate("compare") },
                                onNavigateToAssistant = { navController.navigate("assistant") },
                                onNavigateToLegal = { navController.navigate("legal_rights") }
                            )
                        }

                        composable("research") {
                            DeepResearchScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("sentiment_chart") {
                            SentimentChartScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("legal_rights") {
                            LegalRightsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
