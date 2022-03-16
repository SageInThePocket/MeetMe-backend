package com.meetme.meeting.dto

data class CreateMeetingDto(
    val adminId: Long,
    val name: String,
    val description: String = "",
    val interests: Set<String> = setOf(),
    val links: MutableMap<String, String> = mutableMapOf()
)