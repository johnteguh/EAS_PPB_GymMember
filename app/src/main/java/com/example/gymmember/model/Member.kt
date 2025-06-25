package com.example.gymmember.model

data class Member(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val membershipType: MembershipType,
    val joinDate: String,
    val expiryDate: String,
    val isActive: Boolean = true,
    val profileImageUrl: String? = null,
    val emergencyContact: String = "",
    val address: String = ""
)