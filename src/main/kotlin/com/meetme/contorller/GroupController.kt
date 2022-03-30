package com.meetme.contorller

import com.meetme.data.dto.goup.CreateGroupDto
import com.meetme.data.DataResponse
import com.meetme.data.dto.auth.UserDto
import com.meetme.data.dto.goup.EditGroupDto
import com.meetme.data.dto.goup.GroupDto
import com.meetme.data.dto.meeting.MeetingDto
import com.meetme.services.group.GroupService
import com.meetme.mapper.GroupToGroupDto
import com.meetme.mapper.MeetingToMeetingDto
import com.meetme.mapper.UserToUserDto
import com.meetme.services.file.FileStoreService
import com.meetme.tryExecute
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/groups")
class GroupController {

    @Autowired
    private lateinit var groupService: GroupService

    @Autowired
    private lateinit var groupToGroupDto: GroupToGroupDto

    @Autowired
    private lateinit var userToUserDto: UserToUserDto

    @Autowired
    private lateinit var meetingToMeetingDto: MeetingToMeetingDto


    @PostMapping("/create")
    fun createGroup(@RequestBody createGroupDto: CreateGroupDto): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.createGroup(createGroupDto))
        }

    @PostMapping("/{group_id}/edit")
    fun editGroup(
        @PathVariable("group_id") groupId: Long,
        @RequestBody editCredentials: EditGroupDto,
    ): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.editGroup(groupId, editCredentials))
        }

    @PostMapping("/{group_id}/participant/{user_id}")
    fun addUser(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("user_id") userId: Long,
    ): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.addParticipantToGroup(groupId, userId))
        }

    @DeleteMapping("/{group_id}/participant/{user_id}")
    fun deleteUser(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("user_id") userId: Long,
    ): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.deleteUser(groupId, userId))
        }

    @GetMapping("/{group_id}")
    fun getGroup(@PathVariable("group_id") groupId: Long): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.getGroup(groupId))
        }

    @GetMapping("/{group_id}/participants")
    fun getParticipantsOfGroup(@PathVariable("group_id") groupId: Long): DataResponse<List<UserDto>> =
        tryExecute {
            groupService.getParticipants(groupId)
                .map(userToUserDto)
                .sortedBy(UserDto::fullName)
        }

    @DeleteMapping("/{group_id}/{user_id}")
    fun deleteGroup(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("user_id") userId: Long,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.deleteGroup(groupId, userId)
            null
        }

    @PostMapping("/{group_id}/accept/{meeting_id}")
    fun acceptInvitation(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("meeting_id") meetingId: Long,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.acceptInvitation(groupId, meetingId)
            null
        }

    @PostMapping("/{group_id}/cancel/{meeting_id}")
    fun cancelInvitation(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("meeting_id") meetingId: Long,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.cancelInvitation(groupId, meetingId)
            null
        }

    @PostMapping("/{group_id}/invite/{meeting_id}")
    fun inviteGroupToMeeting(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("meeting_id") meetingId: Long,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.sendInvitationToGroup(groupId, meetingId)
            null
        }

    @PostMapping("/invite/{meeting_id}")
    fun invitesGroupsToMeeting(
        @PathVariable("meeting_id") meetingId: Long,
        @RequestBody groupsIds: List<Long>,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.sendInvitationToGroups(groupsIds, meetingId)
            null
        }

    @GetMapping("/user/{user_id}")
    fun getGroups(
        @PathVariable("user_id") userId: Long,
    ): DataResponse<List<GroupDto>> =
        tryExecute {
            groupService.getGroupsForUser(userId)
                .map(groupToGroupDto)
        }

    @GetMapping("/{group_id}/{search_query}")
    fun searchGroup(
        @PathVariable("group_id") groupId: Long,
        @PathVariable("search_query") searchQuery: String,
    ): DataResponse<List<GroupDto>> =
        tryExecute {
            groupService.searchGroups(searchQuery)
                .map(groupToGroupDto)
                .sortedBy(GroupDto::name)
        }

    @GetMapping("/{group_id}/meetings")
    fun getMeetingsOfGroup(@PathVariable("group_id") groupId: Long): DataResponse<List<MeetingDto>> =
        tryExecute {
            groupService.getMeetings(groupId)
                .map(meetingToMeetingDto)
        }

    @PostMapping("/{group_id}/participant")
    fun addUsers(
        @PathVariable("group_id") groupId: Long,
        @RequestBody userIds: List<Long>,
    ): DataResponse<Unit?> =
        tryExecute {
            groupService.addParticipantsToGroup(groupId, userIds)
            null
        }

    @PostMapping("/{group_id}/image")
    fun uploadImage(
        @RequestParam("image") image: MultipartFile,
        @PathVariable("group_id") groupId: Long,
    ): DataResponse<GroupDto> =
        tryExecute {
            groupToGroupDto(groupService.uploadImage(image, groupId))
        }
}