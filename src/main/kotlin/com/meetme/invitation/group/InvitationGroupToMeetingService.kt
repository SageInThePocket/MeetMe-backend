package com.meetme.invitation.group

import com.meetme.group.Group
import com.meetme.meeting.Meeting
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InvitationGroupToMeetingService {

    @Autowired
    private lateinit var invitationGroupToMeetingDao: InvitationGroupToMeetingDao

    private fun getInvitation(group: Group, meeting: Meeting): InvitationGroupToMeeting =
        invitationGroupToMeetingDao.findByGroupAndMeeting(group, meeting)
            ?: throw IllegalArgumentException("Invitation for group: $group on the $meeting does not exist")

    fun sendInvitationToGroup(group: Group, meeting: Meeting): InvitationGroupToMeeting {
        if (invitationGroupToMeetingDao.findByGroupAndMeeting(group, meeting) != null)
            throw IllegalArgumentException("Invitation for group $group on the $meeting already exist")

        val newInvitationGroupToMeeting = InvitationGroupToMeeting(
            group = group,
            meeting = meeting,
        )
        invitationGroupToMeetingDao.save(newInvitationGroupToMeeting)

        return newInvitationGroupToMeeting
    }

    fun acceptInvitation(group: Group, meeting: Meeting): InvitationGroupToMeeting {
        val invitation = getInvitation(group, meeting)
        if (invitation.isCanceled)
            throw IllegalArgumentException("Invite already canceled")
        if (invitation.isAccepted)
            throw IllegalArgumentException("Invite already accepted")

        invitation.isAccepted = true
        invitationGroupToMeetingDao.save(invitation)

        return invitation
    }

    fun cancelInvitation(group: Group, meeting: Meeting): InvitationGroupToMeeting {
        val invitation = getInvitation(group, meeting)
        if (invitation.isCanceled)
            throw IllegalArgumentException("Invite already canceled")
        if (invitation.isAccepted)
            invitation.isAccepted = false

        invitation.isCanceled = true
        invitationGroupToMeetingDao.save(invitation)

        return invitation
    }
}