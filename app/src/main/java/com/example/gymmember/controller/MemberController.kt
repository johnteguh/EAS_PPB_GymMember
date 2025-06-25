package com.example.gymmember.controller

import com.example.gymmember.model.Member
import com.example.gymmember.model.MembershipType
import com.example.gymmember.repository.MemberRepository
import kotlinx.coroutines.flow.StateFlow

class MemberController(private val repository: MemberRepository) {

    val members: StateFlow<List<Member>> = repository.members

    fun registerMember(
        name: String,
        email: String,
        phoneNumber: String,
        membershipType: MembershipType,
        joinDate: String,
        expiryDate: String,
        emergencyContact: String = "",
        address: String = ""
    ): Result<Boolean> {
        return try {
            if (name.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
                return Result.failure(Exception("Data tidak boleh kosong"))
            }

            val member = Member(
                id = generateId(),
                name = name,
                email = email,
                phoneNumber = phoneNumber,
                membershipType = membershipType,
                joinDate = joinDate,
                expiryDate = expiryDate,
                emergencyContact = emergencyContact,
                address = address
            )
            repository.addMember(member)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllMembers(): List<Member> = repository.getAllMembers()

    fun getMemberById(id: String): Member? = repository.getMemberById(id)

    fun searchMembers(query: String): List<Member> = repository.searchMembers(query)

    fun toggleMemberStatus(id: String): Boolean {
        val member = repository.getMemberById(id)
        return if (member != null) {
            repository.updateMember(member.copy(isActive = !member.isActive))
            true
        } else {
            false
        }
    }

    fun deleteMember(id: String): Boolean {
        return try {
            repository.deleteMember(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getStatistics(): Map<String, Int> {
        val members = getAllMembers()
        return mapOf(
            "total" to members.size,
            "active" to members.count { it.isActive },
            "inactive" to members.count { !it.isActive },
            "basic" to members.count { it.membershipType == MembershipType.BASIC },
            "premium" to members.count { it.membershipType == MembershipType.PREMIUM },
            "vip" to members.count { it.membershipType == MembershipType.VIP }
        )
    }

    private fun generateId(): String = System.currentTimeMillis().toString()
}

//class MemberController(private val repository: MemberRepository) {
//
//    val members: StateFlow<List<Member>> = repository.members
//
//    fun registerMember(
//        name: String,
//        email: String,
//        phoneNumber: String,
//        membershipType: MembershipType,
//        joinDate: String,
//        expiryDate: String,
//        emergencyContact: String = "",
//        address: String = ""
//    ): Result<Boolean> {
//        return try {
//            if (name.isBlank() || email.isBlank() || phoneNumber.isBlank()) {
//                return Result.failure(Exception("Data tidak boleh kosong"))
//            }
//
//            val member = Member(
//                id = generateId(),
//                name = name,
//                email = email,
//                phoneNumber = phoneNumber,
//                membershipType = membershipType,
//                joinDate = joinDate,
//                expiryDate = expiryDate,
//                emergencyContact = emergencyContact,
//                address = address
//            )
//            repository.addMember(member)
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    fun getAllMembers(): List<Member> = repository.getAllMembers()
//
//    fun getMemberById(id: String): Member? = repository.getMemberById(id)
//
//    fun searchMembers(query: String): List<Member> = repository.searchMembers(query)
//
//    fun toggleMemberStatus(id: String): Boolean {
//        val member = repository.getMemberById(id)
//        return if (member != null) {
//            repository.updateMember(member.copy(isActive = !member.isActive))
//            true
//        } else {
//            false
//        }
//    }
//
//    fun deleteMember(id: String): Boolean {
//        return try {
//            repository.deleteMember(id)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    fun getStatistics(): Map<String, Int> {
//        val members = getAllMembers()
//        return mapOf(
//            "total" to members.size,
//            "active" to members.count { it.isActive },
//            "inactive" to members.count { !it.isActive },
//            "basic" to members.count { it.membershipType == MembershipType.BASIC },
//            "premium" to members.count { it.membershipType == MembershipType.PREMIUM },
//            "vip" to members.count { it.membershipType == MembershipType.VIP }
//        )
//    }
//
//    private fun generateId(): String = System.currentTimeMillis().toString()
//}