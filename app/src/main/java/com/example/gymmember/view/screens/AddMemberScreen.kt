package com.example.gymmember.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymmember.controller.MemberController
import com.example.gymmember.model.MembershipType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    controller: MemberController,
    onBack: () -> Unit,
    onMemberAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = Calendar.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var membershipType by remember { mutableStateOf(MembershipType.BASIC) }
    var joinDate by remember { mutableStateOf(dateFormatter.format(today.time)) }
    var expiryDate by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Auto calculate expiry date on first load
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        expiryDate = dateFormatter.format(calendar.time)
    }

    // DatePicker state - diletakkan di luar LazyColumn
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = today.timeInMillis + (30 * 24 * 60 * 60 * 1000L)
    )

    // DatePicker Dialog - MENGGUNAKAN DIALOG BIASA, BUKAN MODAL
    if (showDatePicker) {
        Dialog(
            onDismissRequest = { showDatePicker = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pilih Tanggal Berakhir",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Batal")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    expiryDate = dateFormatter.format(Date(millis))
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Text(
                    text = "Tambah Member Baru",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Form fields
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap *") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Nomor Telepon *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Membership type dropdown
        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = membershipType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipe Membership *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MembershipType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(type.displayName)
                                    Text(
                                        text = "Rp ${type.price.toInt()} / ${type.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            onClick = {
                                membershipType = type
                                expanded = false
                                // Auto calculate expiry date
                                val calendar = Calendar.getInstance()
                                when (type.duration) {
                                    "1 Bulan" -> calendar.add(Calendar.MONTH, 1)
                                    "3 Bulan" -> calendar.add(Calendar.MONTH, 3)
                                    "6 Bulan" -> calendar.add(Calendar.MONTH, 6)
                                    "1 Tahun" -> calendar.add(Calendar.YEAR, 1)
                                }
                                expiryDate = dateFormatter.format(calendar.time)
                                // Update DatePicker state juga
                                datePickerState.selectedDateMillis = calendar.timeInMillis
                            }
                        )
                    }
                }
            }
        }

        // Date fields
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = joinDate,
                    onValueChange = { },
                    label = { Text("Tanggal Bergabung") },
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                    enabled = false
                )

                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { },
                    label = { Text("Tanggal Berakhir *") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { showDatePicker = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pilih Tanggal",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Helper text
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "💡 Tanggal bergabung otomatis hari ini. Tanggal berakhir dihitung otomatis saat pilih membership, atau klik ikon kalender untuk ubah manual.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        item {
            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Kontak Darurat") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Action buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
                            snackbarMessage = "Mohon lengkapi semua field yang wajib diisi"
                            showSnackbar = true
                            return@Button
                        }

                        if (expiryDate.isEmpty()) {
                            snackbarMessage = "Pilih tanggal berakhir membership"
                            showSnackbar = true
                            return@Button
                        }

                        val result = controller.registerMember(
                            name = name,
                            email = email,
                            phoneNumber = phone,
                            membershipType = membershipType,
                            joinDate = joinDate,
                            expiryDate = expiryDate,
                            emergencyContact = emergencyContact,
                            address = address
                        )

                        if (result.isSuccess) {
                            onMemberAdded()
                        } else {
                            snackbarMessage = result.exceptionOrNull()?.message ?: "Terjadi kesalahan"
                            showSnackbar = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan Member")
                }
            }
        }

        // Extra spacing untuk bottom
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Snackbar untuk error messages
    if (showSnackbar) {
        LaunchedEffect(snackbarMessage) {
            kotlinx.coroutines.delay(3000)
            showSnackbar = false
        }
    }
}



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddMemberScreen(
//    controller: MemberController,
//    onBack: () -> Unit,
//    onMemberAdded: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    val today = Calendar.getInstance()
//
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var membershipType by remember { mutableStateOf(MembershipType.BASIC) }
//    var joinDate by remember { mutableStateOf(dateFormatter.format(today.time)) }
//    var expiryDate by remember { mutableStateOf("") }
//    var emergencyContact by remember { mutableStateOf("") }
//    var address by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) }
//
//    // Auto calculate expiry date on first load
//    LaunchedEffect(Unit) {
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.MONTH, 1)
//        expiryDate = dateFormatter.format(calendar.time)
//    }
//
//    LazyColumn(
//        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Header
//        item {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
//                }
//                Text(
//                    text = "Tambah Member Baru",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//
//        // Form fields
//        item {
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama Lengkap *") },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = phone,
//                onValueChange = { phone = it },
//                label = { Text("Nomor Telepon *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // Membership type dropdown
//        item {
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                OutlinedTextField(
//                    value = membershipType.displayName,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Tipe Membership *") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                    modifier = Modifier.menuAnchor().fillMaxWidth()
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    MembershipType.values().forEach { type ->
//                        DropdownMenuItem(
//                            text = {
//                                Column {
//                                    Text(type.displayName)
//                                    Text(
//                                        text = "Rp ${type.price.toInt()} / ${type.duration}",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                    )
//                                }
//                            },
//                            onClick = {
//                                membershipType = type
//                                expanded = false
//                                // Auto calculate expiry date
//                                val calendar = Calendar.getInstance()
//                                when (type.duration) {
//                                    "1 Bulan" -> calendar.add(Calendar.MONTH, 1)
//                                    "3 Bulan" -> calendar.add(Calendar.MONTH, 3)
//                                    "6 Bulan" -> calendar.add(Calendar.MONTH, 6)
//                                    "1 Tahun" -> calendar.add(Calendar.YEAR, 1)
//                                }
//                                expiryDate = dateFormatter.format(calendar.time)
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        // Date fields
//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                OutlinedTextField(
//                    value = joinDate,
//                    onValueChange = { },
//                    label = { Text("Tanggal Bergabung") },
//                    readOnly = true,
//                    modifier = Modifier.weight(1f),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        disabledBorderColor = MaterialTheme.colorScheme.outline,
//                    ),
//                    enabled = false
//                )
//
//                OutlinedTextField(
//                    value = expiryDate,
//                    onValueChange = { expiryDate = it },
//                    label = { Text("Tanggal Berakhir *") },
//                    placeholder = { Text("DD/MM/YYYY") },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//
//        // Helper text
//        item {
//            Text(
//                text = "* Tanggal bergabung otomatis hari ini, tanggal berakhir dihitung otomatis",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = emergencyContact,
//                onValueChange = { emergencyContact = it },
//                label = { Text("Kontak Darurat") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = address,
//                onValueChange = { address = it },
//                label = { Text("Alamat") },
//                maxLines = 3,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // Action buttons
//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                OutlinedButton(
//                    onClick = onBack,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Batal")
//                }
//
//                Button(
//                    onClick = {
//                        val result = controller.registerMember(
//                            name = name,
//                            email = email,
//                            phoneNumber = phone,
//                            membershipType = membershipType,
//                            joinDate = joinDate,
//                            expiryDate = expiryDate,
//                            emergencyContact = emergencyContact,
//                            address = address
//                        )
//
//                        if (result.isSuccess) {
//                            onMemberAdded()
//                        }
//                    },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Simpan")
//                }
//            }
//        }
//    }
//}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddMemberScreen(
//    controller: MemberController,
//    onBack: () -> Unit,
//    onMemberAdded: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    val today = Calendar.getInstance()
//
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var membershipType by remember { mutableStateOf(MembershipType.BASIC) }
//    var joinDate by remember { mutableStateOf(dateFormatter.format(today.time)) }
//    var expiryDate by remember { mutableStateOf("") }
//    var emergencyContact by remember { mutableStateOf("") }
//    var address by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) }
//    var showSnackbar by remember { mutableStateOf(false) }
//    var snackbarMessage by remember { mutableStateOf("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    // Date picker state
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = today.timeInMillis + (30 * 24 * 60 * 60 * 1000L)
//    )
//
//    // Date picker dialog
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        datePickerState.selectedDateMillis?.let { millis ->
//                            expiryDate = dateFormatter.format(Date(millis))
//                        }
//                        showDatePicker = false
//                    }
//                ) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) {
//                    Text("Batal")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//
//    // GUNAKAN LAZYCOLUMN UNTUK MENGHINDARI NESTED SCROLL
//    LazyColumn(
//        modifier = modifier.fillMaxSize().padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Header
//        item {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
//                }
//                Text(
//                    text = "Tambah Member Baru",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//
//        // Form fields
//        item {
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama Lengkap *") },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = phone,
//                onValueChange = { phone = it },
//                label = { Text("Nomor Telepon *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // Membership type dropdown
//        item {
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                OutlinedTextField(
//                    value = membershipType.displayName,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Tipe Membership *") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                    modifier = Modifier.menuAnchor().fillMaxWidth()
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    MembershipType.values().forEach { type ->
//                        DropdownMenuItem(
//                            text = {
//                                Column {
//                                    Text(type.displayName)
//                                    Text(
//                                        text = "Rp ${type.price.toInt()} / ${type.duration}",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                    )
//                                }
//                            },
//                            onClick = {
//                                membershipType = type
//                                expanded = false
//                                // Auto calculate expiry date
//                                val calendar = Calendar.getInstance()
//                                when (type.duration) {
//                                    "1 Bulan" -> calendar.add(Calendar.MONTH, 1)
//                                    "3 Bulan" -> calendar.add(Calendar.MONTH, 3)
//                                    "6 Bulan" -> calendar.add(Calendar.MONTH, 6)
//                                    "1 Tahun" -> calendar.add(Calendar.YEAR, 1)
//                                }
//                                expiryDate = dateFormatter.format(calendar.time)
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        // Date fields
//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                OutlinedTextField(
//                    value = joinDate,
//                    onValueChange = { },
//                    label = { Text("Tanggal Bergabung") },
//                    readOnly = true,
//                    modifier = Modifier.weight(1f),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        disabledBorderColor = MaterialTheme.colorScheme.outline,
//                    ),
//                    enabled = false
//                )
//
//                OutlinedTextField(
//                    value = expiryDate,
//                    onValueChange = { },
//                    label = { Text("Tanggal Berakhir *") },
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker = true }) {
//                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
//                        }
//                    },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }
//
//        // Helper text
//        item {
//            Text(
//                text = "* Tanggal bergabung otomatis hari ini, pilih tanggal berakhir membership",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = emergencyContact,
//                onValueChange = { emergencyContact = it },
//                label = { Text("Kontak Darurat") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = address,
//                onValueChange = { address = it },
//                label = { Text("Alamat") },
//                maxLines = 3,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // Action buttons
//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                OutlinedButton(
//                    onClick = onBack,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Batal")
//                }
//
//                Button(
//                    onClick = {
//                        if (expiryDate.isEmpty()) {
//                            snackbarMessage = "Pilih tanggal berakhir membership"
//                            showSnackbar = true
//                            return@Button
//                        }
//
//                        val result = controller.registerMember(
//                            name = name,
//                            email = email,
//                            phoneNumber = phone,
//                            membershipType = membershipType,
//                            joinDate = joinDate,
//                            expiryDate = expiryDate,
//                            emergencyContact = emergencyContact,
//                            address = address
//                        )
//
//                        if (result.isSuccess) {
//                            onMemberAdded()
//                        } else {
//                            snackbarMessage = result.exceptionOrNull()?.message ?: "Error"
//                            showSnackbar = true
//                        }
//                    },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Simpan")
//                }
//            }
//        }
//    }
//
//    // Snackbar for error messages
//    if (showSnackbar) {
//        LaunchedEffect(showSnackbar) {
//            kotlinx.coroutines.delay(3000)
//            showSnackbar = false
//        }
//
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.BottomCenter
//        ) {
//            Snackbar(
//                modifier = Modifier.padding(16.dp),
//                action = {
//                    TextButton(onClick = { showSnackbar = false }) {
//                        Text("OK")
//                    }
//                }
//            ) {
//                Text(snackbarMessage)
//            }
//        }
//    }
//}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddMemberScreen(
//    controller: MemberController,
//    onBack: () -> Unit,
//    onMemberAdded: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    val today = Calendar.getInstance()
//
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var membershipType by remember { mutableStateOf(MembershipType.BASIC) }
//    var joinDate by remember { mutableStateOf(dateFormatter.format(today.time)) }
//    var expiryDate by remember { mutableStateOf("") }
//    var emergencyContact by remember { mutableStateOf("") }
//    var address by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) }
//    var showSnackbar by remember { mutableStateOf(false) }
//    var snackbarMessage by remember { mutableStateOf("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//
//    // Date picker state
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = today.timeInMillis + (30 * 24 * 60 * 60 * 1000L) // 30 hari dari sekarang
//    )
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
//        // Header
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            IconButton(onClick = onBack) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//            }
//            Text(
//                text = "Tambah Member Baru",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        // Date picker dialog
//        if (showDatePicker) {
//            DatePickerDialog(
//                onDismissRequest = { showDatePicker = false },
//                confirmButton = {
//                    TextButton(
//                        onClick = {
//                            datePickerState.selectedDateMillis?.let { millis ->
//                                expiryDate = dateFormatter.format(Date(millis))
//                            }
//                            showDatePicker = false
//                        }
//                    ) {
//                        Text("OK")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDatePicker = false }) {
//                        Text("Batal")
//                    }
//                }
//            ) {
//                DatePicker(state = datePickerState)
//            }
//        }
//
//        Column(
//            modifier = modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//            // Header
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//                }
//                Text(
//                    text = "Tambah Member Baru",
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Form fields
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama Lengkap *") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = phone,
//                onValueChange = { phone = it },
//                label = { Text("Nomor Telepon *") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Membership type dropdown
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                OutlinedTextField(
//                    value = membershipType.displayName,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Tipe Membership *") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                    modifier = Modifier.menuAnchor().fillMaxWidth()
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    MembershipType.entries.forEach { type ->
//                        DropdownMenuItem(
//                            text = {
//                                Column {
//                                    Text(type.displayName)
//                                    Text(
//                                        text = "Rp ${type.price.toInt()} / ${type.duration}",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                    )
//                                }
//                            },
//                            onClick = {
//                                membershipType = type
//                                expanded = false
//                                // Auto calculate expiry date based on membership type
//                                val calendar = Calendar.getInstance()
//                                when (type.duration) {
//                                    "1 Bulan" -> calendar.add(Calendar.MONTH, 1)
//                                    "3 Bulan" -> calendar.add(Calendar.MONTH, 3)
//                                    "6 Bulan" -> calendar.add(Calendar.MONTH, 6)
//                                    "1 Tahun" -> calendar.add(Calendar.YEAR, 1)
//                                }
//                                expiryDate = dateFormatter.format(calendar.time)
//                            }
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // Join date (auto-filled, read-only)
//                OutlinedTextField(
//                    value = joinDate,
//                    onValueChange = { },
//                    label = { Text("Tanggal Bergabung") },
//                    readOnly = true,
//                    modifier = Modifier.weight(1f),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
//                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                        disabledBorderColor = MaterialTheme.colorScheme.outline,
//                    ),
//                    enabled = false
//                )
//
//                // Expiry date with date picker
//                OutlinedTextField(
//                    value = expiryDate,
//                    onValueChange = { },
//                    label = { Text("Tanggal Berakhir *") },
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker = true }) {
//                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
//                        }
//                    },
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Helper text for dates
//            Text(
//                text = "* Tanggal bergabung otomatis hari ini, pilih tanggal berakhir membership",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = emergencyContact,
//                onValueChange = { emergencyContact = it },
//                label = { Text("Kontak Darurat") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = address,
//                onValueChange = { address = it },
//                label = { Text("Alamat") },
//                maxLines = 3,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Action buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                OutlinedButton(
//                    onClick = onBack,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Batal")
//                }
//
//                Button(
//                    onClick = {
//                        if (expiryDate.isEmpty()) {
//                            snackbarMessage = "Pilih tanggal berakhir membership"
//                            showSnackbar = true
//                            return@Button
//                        }
//
//                        val result = controller.registerMember(
//                            name = name,
//                            email = email,
//                            phoneNumber = phone,
//                            membershipType = membershipType,
//                            joinDate = joinDate,
//                            expiryDate = expiryDate,
//                            emergencyContact = emergencyContact,
//                            address = address
//                        )
//
//                        if (result.isSuccess) {
//                            onMemberAdded()
//                        } else {
//                            snackbarMessage = result.exceptionOrNull()?.message ?: "Error"
//                            showSnackbar = true
//                        }
//                    },
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Simpan")
//                }
//            }
//        }
//
//        // Snackbar for error messages
//        if (showSnackbar) {
//            LaunchedEffect(showSnackbar) {
//                kotlinx.coroutines.delay(3000)
//                showSnackbar = false
//            }
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//                Snackbar(
//                    modifier = Modifier.padding(16.dp),
//                    action = {
//                        TextButton(onClick = { showSnackbar = false }) {
//                            Text("OK")
//                        }
//                    }
//                ) {
//                    Text(snackbarMessage)
//                }
//            }
//        }
//    }
//}

//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import com.example.gymmember.controller.MemberController
//import com.example.gymmember.model.MembershipType
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddMemberScreen(
//    controller: MemberController,
//    onBack: () -> Unit,
//    onMemberAdded: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//    var membershipType by remember { mutableStateOf(MembershipType.BASIC) }
//    var joinDate by remember { mutableStateOf("") }
//    var expiryDate by remember { mutableStateOf("") }
//    var emergencyContact by remember { mutableStateOf("") }
//    var address by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) }
//    var showSnackbar by remember { mutableStateOf(false) }
//    var snackbarMessage by remember { mutableStateOf("") }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
//        // Header
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            IconButton(onClick = onBack) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
//            }
//            Text(
//                text = "Tambah Member Baru",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Form fields
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Nama Lengkap *") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email *") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = phone,
//            onValueChange = { phone = it },
//            label = { Text("Nomor Telepon *") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Membership type dropdown
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = { expanded = !expanded }
//        ) {
//            OutlinedTextField(
//                value = membershipType.displayName,
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("Tipe Membership *") },
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                modifier = Modifier.menuAnchor().fillMaxWidth()
//            )
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                MembershipType.entries.forEach { type ->
//                    DropdownMenuItem(
//                        text = {
//                            Column {
//                                Text(type.displayName)
//                                Text(
//                                    text = "Rp ${type.price.toInt()} / ${type.duration}",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
//                                )
//                            }
//                        },
//                        onClick = {
//                            membershipType = type
//                            expanded = false
//                        }
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            OutlinedTextField(
//                value = joinDate,
//                onValueChange = { joinDate = it },
//                label = { Text("Tanggal Bergabung *") },
//                placeholder = { Text("DD/MM/YYYY") },
//                modifier = Modifier.weight(1f)
//            )
//
//            OutlinedTextField(
//                value = expiryDate,
//                onValueChange = { expiryDate = it },
//                label = { Text("Tanggal Berakhir *") },
//                placeholder = { Text("DD/MM/YYYY") },
//                modifier = Modifier.weight(1f)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = emergencyContact,
//            onValueChange = { emergencyContact = it },
//            label = { Text("Kontak Darurat") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = address,
//            onValueChange = { address = it },
//            label = { Text("Alamat") },
//            maxLines = 3,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        // Action buttons
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            OutlinedButton(
//                onClick = onBack,
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Batal")
//            }
//
//            Button(
//                onClick = {
//                    val result = controller.registerMember(
//                        name = name,
//                        email = email,
//                        phoneNumber = phone,
//                        membershipType = membershipType,
//                        joinDate = joinDate,
//                        expiryDate = expiryDate,
//                        emergencyContact = emergencyContact,
//                        address = address
//                    )
//
//                    if (result.isSuccess) {
//                        onMemberAdded()
//                    } else {
//                        snackbarMessage = result.exceptionOrNull()?.message ?: "Error"
//                        showSnackbar = true
//                    }
//                },
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("Simpan")
//            }
//        }
//    }
//
//    if (showSnackbar) {
//        LaunchedEffect(showSnackbar) {
//            showSnackbar = false
//        }
//    }
//}