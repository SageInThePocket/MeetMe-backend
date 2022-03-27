package com.meetme.invitation.group

import com.meetme.group.Group
import com.meetme.meeting.Meeting
import javax.persistence.*

@Entity(name = "GroupInvitation")
data class InvitationGroupToMeeting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_invitation_id")
    val id: Long = 0,

    @ManyToOne(targetEntity = Meeting::class, fetch = FetchType.EAGER)
    val meeting: Meeting? = null,

    @ManyToOne(targetEntity = Group::class, fetch = FetchType.EAGER)
    val group: Group? = null,

    @Column(name = "is_column")
    var isAccepted: Boolean = false,

    @Column(name = "is_canceled")
    var isCanceled: Boolean = false,
)