package com.example.gymmember.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymmember.controller.MemberController
import com.example.gymmember.model.Member
import com.example.gymmember.view.components.MemberCard
import com.example.gymmember.view.components.SearchBar
import com.example.gymmember.view.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    controller: MemberController,
    onAddMember: () -> Unit,
    onMemberClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val members by controller.members.collectAsState()
    val statistics = controller.getStatistics()

    val filteredMembers = remember(members, searchQuery) {
        if (searchQuery.isEmpty()) {
            members
        } else {
            controller.searchMembers(searchQuery)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gym Members",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Statistics - RESPONSIVE GRID LAYOUT
            item {
                // OPSI 1: ROW DENGAN WEIGHT (RECOMMENDED)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Member",
                        value = statistics["total"].toString(),
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f) // MENGGUNAKAN WEIGHT
                    )
                    StatCard(
                        title = "Aktif",
                        value = statistics["active"].toString(),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f) // MENGGUNAKAN WEIGHT
                    )
                    StatCard(
                        title = "VIP",
                        value = statistics["vip"].toString(),
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f) // MENGGUNAKAN WEIGHT
                    )
                    StatCard(
                        title = "Inactive",
                        value = statistics["inactive"].toString(),
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.weight(1f) // MENGGUNAKAN WEIGHT
                    )
                }

                // OPSI 2: ALTERNATIF DENGAN LAZYGRID (JIKA INGIN LEBIH BANYAK STATS)

//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(3),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    modifier = Modifier.fillMaxWidth().height(100.dp)
//                ) {
//                    item {
//                        StatCard(
//                            title = "Total Member",
//                            value = statistics["total"].toString(),
//                            color = Color(0xFF2196F3),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                    item {
//                        StatCard(
//                            title = "Aktif",
//                            value = statistics["active"].toString(),
//                            color = Color(0xFF4CAF50),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                    item {
//                        StatCard(
//                            title = "VIP",
//                            value = statistics["vip"].toString(),
//                            color = Color(0xFFFF9800),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                    item {
//                        StatCard(
//                            title = "Inactive",
//                            value = statistics["inactive"].toString(),
//                            color = Color(0xFF9E9E9E),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }

            }

            // Search bar
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Cari member..."
                )
            }

            // Members list
            items(filteredMembers) { member ->
                MemberCard(
                    member = member,
                    onCardClick = { onMemberClick(member.id) },
                    onToggleStatus = { controller.toggleMemberStatus(member.id) },
                    onDelete = { controller.deleteMember(member.id) }
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddMember,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Member")
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MemberListScreen(
//    controller: MemberController,
//    onAddMember: () -> Unit,
//    onMemberClick: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    val members by controller.members.collectAsState()
//    val statistics = controller.getStatistics()
//
//    val filteredMembers = remember(members, searchQuery) {
//        if (searchQuery.isEmpty()) {
//            members
//        } else {
//            controller.searchMembers(searchQuery)
//        }
//    }
//
//    // GUNAKAN BOX + LAZYLCOLUMN UNTUK PROPER LAYOUT
//    Box(modifier = modifier.fillMaxSize()) {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Header
//            item {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Gym Members",
//                        style = MaterialTheme.typography.headlineMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            // Statistics
//            item {
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    item {
//                        StatCard(
//                            title = "Total Member",
//                            value = statistics["total"].toString(),
//                            color = Color(0xFF2196F3),
//                            modifier = Modifier.width(120.dp)
//                        )
//                    }
//                    item {
//                        StatCard(
//                            title = "Aktif",
//                            value = statistics["active"].toString(),
//                            color = Color(0xFF4CAF50),
//                            modifier = Modifier.width(120.dp)
//                        )
//                    }
//                    item {
//                        StatCard(
//                            title = "VIP",
//                            value = statistics["vip"].toString(),
//                            color = Color(0xFFFF9800),
//                            modifier = Modifier.width(120.dp)
//                        )
//                    }
//                }
//            }
//
//            // Search bar
//            item {
//                SearchBar(
//                    query = searchQuery,
//                    onQueryChange = { searchQuery = it },
//                    placeholder = "Cari member..."
//                )
//            }
//
//            // Members list
//            items(filteredMembers) { member ->
//                MemberCard(
//                    member = member,
//                    onCardClick = { onMemberClick(member.id) },
//                    onToggleStatus = { controller.toggleMemberStatus(member.id) },
//                    onDelete = { controller.deleteMember(member.id) }
//                )
//            }
//        }
//
//        // FAB
//        FloatingActionButton(
//            onClick = onAddMember,
//            containerColor = MaterialTheme.colorScheme.primary,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Tambah Member")
//        }
//    }
//}



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MemberListScreen(
//    controller: MemberController,
//    onAddMember: () -> Unit,
//    onMemberClick: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    val members by controller.members.collectAsState()
//    val statistics = controller.getStatistics()
//
//    val filteredMembers = remember(members, searchQuery) {
//        if (searchQuery.isEmpty()) {
//            members
//        } else {
//            controller.searchMembers(searchQuery)
//        }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Header
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Gym Members",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold
//            )
//
//            FloatingActionButton(
//                onClick = onAddMember,
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Tambah Member")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Statistics
//        LazyRow(
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            item {
//                StatCard(
//                    title = "Total Member",
//                    value = statistics["total"].toString(),
//                    color = Color(0xFF2196F3),
//                    modifier = Modifier.width(120.dp)
//                )
//            }
//            item {
//                StatCard(
//                    title = "Aktif",
//                    value = statistics["active"].toString(),
//                    color = Color(0xFF4CAF50),
//                    modifier = Modifier.width(120.dp)
//                )
//            }
//            item {
//                StatCard(
//                    title = "VIP",
//                    value = statistics["vip"].toString(),
//                    color = Color(0xFFFF9800),
//                    modifier = Modifier.width(120.dp)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Search bar
//        SearchBar(
//            query = searchQuery,
//            onQueryChange = { searchQuery = it },
//            placeholder = "Cari member..."
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Members list
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(filteredMembers) { member ->
//                MemberCard(
//                    member = member,
//                    onCardClick = { onMemberClick(member.id) },
//                    onToggleStatus = { controller.toggleMemberStatus(member.id) },
//                    onDelete = { controller.deleteMember(member.id) }
//                )
//            }
//        }
//    }
//}
