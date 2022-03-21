package com.meetme.dto.meeting

import java.time.Instant
import java.util.*

data class CreateMeetingDto(
    val adminId: Long,
    val name: String,
    val description: String = "",
    val interests: Set<String> = setOf(),
    val links: MutableMap<String, String> = mutableMapOf(),
    val locate: String? = null,
    val isOnline: Boolean = false,
    val isPrivate: Boolean = false,
    val startDate: Long = Date.from(Instant.now()).time,
    val endDate: Long? = null,
    val maxAmountParticipants: Int = 1,
)