package com.example.gymmember.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import com.example.gymmember.controller.MemberController
import com.example.gymmember.view.screens.AddMemberScreen
import com.example.gymmember.view.screens.MemberDetailScreen
import com.example.gymmember.view.screens.MemberListScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun GymMemberApp(
    controller: MemberController,
    modifier: Modifier = Modifier // TAMBAHKAN PARAMETER INI
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "member_list",
        modifier = modifier // GUNAKAN MODIFIER
    ) {
        composable("member_list") {
            MemberListScreen(
                controller = controller,
                onAddMember = { navController.navigate("add_member") },
                onMemberClick = { memberId ->
                    navController.navigate("member_detail/$memberId")
                },
                modifier = Modifier.fillMaxSize() // PASTIKAN ADA
            )
        }

        composable("add_member") {
            AddMemberScreen(
                controller = controller,
                onBack = { navController.popBackStack() },
                onMemberAdded = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize() // PASTIKAN ADA
            )
        }

        composable("member_detail/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: ""
            MemberDetailScreen(
                memberId = memberId,
                controller = controller,
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize() // PASTIKAN ADA
            )
        }
    }
}


//@Composable
//fun GymMemberApp(controller: MemberController, modifier: Modifier) {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "member_list"
//    ) {
//        composable("member_list") {
//            MemberListScreen(
//                controller = controller,
//                onAddMember = { navController.navigate("add_member") },
//                onMemberClick = { memberId ->
//                    navController.navigate("member_detail/$memberId")
//                }
//            )
//        }
//
//        composable("add_member") {
//            AddMemberScreen(
//                controller = controller,
//                onBack = { navController.popBackStack() },
//                onMemberAdded = { navController.popBackStack() }
//            )
//        }
//
//        composable("member_detail/{memberId}") { backStackEntry ->
//            val memberId = backStackEntry.arguments?.getString("memberId") ?: ""
//            MemberDetailScreen(
//                memberId = memberId,
//                controller = controller,
//                onBack = { navController.popBackStack() }
//            )
//        }
//    }
//}


//@Composable
//fun GymMemberApp(controller: MemberController) {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "member_list"
//    ) {
//        composable("member_list") {
//            MemberListScreen(
//                controller = controller,
//                onAddMember = { navController.navigate("add_member") },
//                onMemberClick = { memberId ->
//                    navController.navigate("member_detail/$memberId")
//                }
//            )
//        }
//
//        composable("add_member") {
//            AddMemberScreen(
//                controller = controller,
//                onBack = { navController.popBackStack() },
//                onMemberAdded = { navController.popBackStack() }
//            )
//        }
//
//        composable("member_detail/{memberId}") { backStackEntry ->
//            val memberId = backStackEntry.arguments?.getString("memberId") ?: ""
//            MemberDetailScreen(
//                memberId = memberId,
//                controller = controller,
//                onBack = { navController.popBackStack() }
//            )
//        }
//    }
//}