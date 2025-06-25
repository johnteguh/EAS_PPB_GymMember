package com.example.gymmember.model

import androidx.compose.ui.graphics.Color

enum class MembershipType(
    val displayName: String,
    val price: Double,
    val duration: String,
    val color: Color,
    val benefits: List<String>
) {
    BASIC(
        "Basic",
        250000.0,
        "1 Bulan",
        Color(0xFF4CAF50),
        listOf("Akses gym", "Locker", "1 sesi konsultasi")
    ),
    PREMIUM(
        "Premium",
        450000.0,
        "1 Bulan",
        Color(0xFF2196F3),
        listOf("Akses gym", "Locker", "Personal trainer 2x", "Kelas grup")
    ),
    VIP(
        "VIP",
        800000.0,
        "1 Bulan",
        Color(0xFFFF9800),
        listOf("Akses 24/7", "Personal trainer unlimited", "Spa access", "Nutrisi konsultasi")
    )
}