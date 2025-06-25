package com.example.gymmember.repository

import com.example.gymmember.model.Member
import com.example.gymmember.model.MembershipType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemberRepository {
    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    private val membersList = mutableListOf<Member>()

    init {
        // Data dummy
        addDummyData()
    }

    fun addMember(member: Member) {
        membersList.add(member)
        _members.value = membersList.toList()
    }

    fun getAllMembers(): List<Member> = membersList

    fun getMemberById(id: String): Member? = membersList.find { it.id == id }

    fun updateMember(member: Member) {
        val index = membersList.indexOfFirst { it.id == member.id }
        if (index != -1) {
            membersList[index] = member
            _members.value = membersList.toList()
        }
    }

    fun deleteMember(id: String) {
        membersList.removeIf { it.id == id }
        _members.value = membersList.toList()
    }

    fun searchMembers(query: String): List<Member> {
        return membersList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.phoneNumber.contains(query, ignoreCase = true)
        }
    }

    private fun addDummyData() {
        val dummyMembers = listOf(
            Member("1", "John Doe", "john@email.com", "081234567890",
                MembershipType.PREMIUM, "01/01/2024", "01/02/2024"),
            Member("2", "Jane Smith", "jane@email.com", "081234567891",
                MembershipType.VIP, "15/01/2024", "15/02/2024"),
            Member("3", "Bob Wilson", "bob@email.com", "081234567892",
                MembershipType.BASIC, "20/01/2024", "20/02/2024", false)
        )
        membersList.addAll(dummyMembers)
        _members.value = membersList.toList()
    }
}