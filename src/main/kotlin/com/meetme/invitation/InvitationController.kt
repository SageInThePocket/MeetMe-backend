package com.meetme.invitation

import com.meetme.domain.dto.DataResponse
import com.meetme.domain.dto.invitation.InvitationDto
import com.meetme.tryExecute
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/meetings/{meeting_id}/invite")
class InvitationController {

    @Qualifier("userInvitationService")
    @Autowired
    private lateinit var userInvitationService: InvitationService

    @Qualifier("groupInvitationService")
    @Autowired
    private lateinit var groupInvitationService: InvitationService

    @PostMapping
    fun sendInvitations(
        @PathVariable("meeting_id") meetingId: Long,
        @RequestBody invitationDto: InvitationDto,
    ): DataResponse<Unit?> =
        tryExecute {
            userInvitationService.sendInvitations(invitationDto.users, meetingId)
            groupInvitationService.sendInvitations(invitationDto.group, meetingId)
            null
        }
}