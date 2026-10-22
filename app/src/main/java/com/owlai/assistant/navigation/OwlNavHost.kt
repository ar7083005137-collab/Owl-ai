package com.owlai.assistant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.owlai.assistant.R
import com.owlai.assistant.data.repository.ChatRepository
import com.owlai.assistant.data.repository.ConfigRepository
import com.owlai.assistant.data.repository.CreationRepository
import com.owlai.assistant.data.repository.MathRepository
import com.owlai.assistant.data.repository.UserRepository
import com.owlai.assistant.screens.AdminPanelScreen
import com.owlai.assistant.screens.ChatScreen
import com.owlai.assistant.screens.HomeScreen
import com.owlai.assistant.screens.ImageCreatorScreen
import com.owlai.assistant.screens.MathSolverScreen
import com.owlai.assistant.screens.PhotoMathScreen
import com.owlai.assistant.screens.ProfileScreen
import com.owlai.assistant.screens.SplashScreen
import com.owlai.assistant.screens.VideoCreatorScreen
import com.owlai.assistant.screens.WelcomeAuthScreen
import com.owlai.assistant.viewmodel.AdminViewModel
import com.owlai.assistant.viewmodel.ChatViewModel
import com.owlai.assistant.viewmodel.CreationViewModel
import com.owlai.assistant.viewmodel.MainViewModel
import com.owlai.assistant.viewmodel.MathViewModel

@Composable
fun OwlNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val databaseId = context.getString(R.string.firestore_database_id)
    val db = FirebaseFirestore.getInstance(databaseId)

    val userRepository = UserRepository(db)
    val configRepository = ConfigRepository(db)
    val chatRepository = ChatRepository(db)
    val mathRepository = MathRepository(db)
    val creationRepository = CreationRepository(db)

    val mainViewModel: MainViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                MainViewModel(userRepository, configRepository)
            }
        }
    )

    val currentUser by mainViewModel.currentUser.collectAsState()
    val userProfile by mainViewModel.userProfile.collectAsState()
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isAdmin by mainViewModel.isAdmin.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            WelcomeAuthScreen(
                onAuthSuccess = {
                    mainViewModel.refreshProfile()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            if (currentUser == null) {
                WelcomeAuthScreen(
                    onAuthSuccess = { mainViewModel.refreshProfile() }
                )
            } else {
                HomeScreen(
                    userProfile = userProfile,
                    appConfig = appConfig,
                    isAdmin = isAdmin,
                    onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                    onNavigateToMath = { navController.navigate(Screen.MathSolver.route) },
                    onNavigateToPhotoMath = { navController.navigate(Screen.PhotoMath.route) },
                    onNavigateToImageGen = { navController.navigate(Screen.ImageCreator.route) },
                    onNavigateToVideoGen = { navController.navigate(Screen.VideoCreator.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.Admin.route) }
                )
            }
        }

        composable(Screen.Chat.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val chatViewModel: ChatViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            ChatViewModel(chatRepository, userRepository, user.uid)
                        }
                    }
                )
                ChatScreen(
                    chatViewModel = chatViewModel,
                    chatModel = appConfig.chatModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.MathSolver.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val mathViewModel: MathViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            MathViewModel(mathRepository, userRepository, user.uid)
                        }
                    }
                )
                MathSolverScreen(
                    mathViewModel = mathViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.PhotoMath.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val mathViewModel: MathViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            MathViewModel(mathRepository, userRepository, user.uid)
                        }
                    }
                )
                PhotoMathScreen(
                    mathViewModel = mathViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.ImageCreator.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val creationViewModel: CreationViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            CreationViewModel(creationRepository, userRepository, user.uid)
                        }
                    }
                )
                ImageCreatorScreen(
                    creationViewModel = creationViewModel,
                    imageProvider = appConfig.activeImageProvider,
                    imageModel = appConfig.imageModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.VideoCreator.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val creationViewModel: CreationViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            CreationViewModel(creationRepository, userRepository, user.uid)
                        }
                    }
                )
                VideoCreatorScreen(
                    creationViewModel = creationViewModel,
                    videoProvider = appConfig.activeVideoProvider,
                    videoModel = appConfig.videoModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                userProfile = userProfile,
                isAdmin = isAdmin,
                onBack = { navController.popBackStack() },
                onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                onLoggedOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Admin.route) {
            val user = currentUser
            if (user == null) {
                WelcomeAuthScreen(onAuthSuccess = { mainViewModel.refreshProfile() })
            } else {
                val adminViewModel: AdminViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            AdminViewModel(configRepository, userRepository, user.uid, user.email ?: "")
                        }
                    }
                )
                AdminPanelScreen(
                    adminViewModel = adminViewModel,
                    isAdminAuthorized = isAdmin,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
