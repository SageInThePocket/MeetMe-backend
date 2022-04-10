package com.meetme.participants.group

import com.meetme.domain.StoreService
import com.meetme.group.GroupService
import com.meetme.group.db.Group
import com.meetme.participants.base.ParticipantsBaseService
import com.meetme.user.UserService
import com.meetme.user.db.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("groupParticipantsService")
class GroupParticipantsService @Autowired constructor(
    userService: UserService,
    groupService: GroupService,
) : ParticipantsBaseService<Group>(userService, groupService) {

    override fun checkEntityBeforeAdd(entity: Group, user: User) {
        if (entity.participants.contains(user))
            throw IllegalArgumentException(
                "User with id = ${user.id} already is participant of meeting with id = ${entity.id}"
            )
    }

    override fun checkEntityBeforeRemove(entity: Group, user: User) {
        if (!entity.participants.contains(user))
            throw IllegalArgumentException(
                "The user with id = ${user.id} is not a member of the meeting ${entity.id}"
            )
    }

    override fun addContainerToUser(container: Group, user: User) {
        user.groups.add(container)
    }

    override fun removeContainerFromUser(container: Group, user: User) {
        user.groups.remove(container)
    }
}