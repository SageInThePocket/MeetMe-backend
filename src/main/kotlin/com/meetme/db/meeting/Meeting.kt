package com.meetme.db.meeting

import com.fasterxml.jackson.annotation.JsonIgnore
import com.meetme.db.chat.Chat
import com.meetme.db.user.User
import com.meetme.db.image_store.Image
import com.meetme.db.interest.Interest
import com.meetme.db.invitation.Invitation
import com.meetme.db.group.Post
import com.meetme.domain.entity.ParticipantsContainer
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "Meeting")
data class Meeting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_name")
    val id: Long = 0,

    @Column(name = "name")
    var name: String = "",

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "photo_url")
    var photoUrl: String? = null,

    @Column(name = "is_online")
    var isOnline: Boolean = false,

    @Column(name = "start_date")
    var startDate: String = "",

    @Column(name = "end_date")
    var endDate: String? = null,

    @Column(name = "is_private")
    var private: Boolean = false,

    @Column(name = "location")
    var location: String? = null,

    @Column(name = "max_number_of_participants")
    var maxNumberOfParticipants: Int = 1,

    @ManyToOne(targetEntity = User::class)
    @JoinColumn(name = "user_id")
    val admin: User = User(),

    @ManyToMany(targetEntity = Interest::class)
    @JoinTable(
        name = "interests_of_meeting",
        joinColumns = [JoinColumn(name = "meeting_id")],
        inverseJoinColumns = [JoinColumn(name = "interest_id")],
    )
    var interests: MutableSet<Interest> = mutableSetOf(),

    @ManyToMany(targetEntity = User::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "participants_of_meeting",
        joinColumns = [JoinColumn(name = "meeting_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
    )
    override val participants: MutableList<User> = mutableListOf(admin),

    @OneToOne(
        targetEntity = Chat::class,
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    var chat: Chat = Chat(),

    @OneToMany(targetEntity = Post::class, mappedBy = "meeting", cascade = [CascadeType.ALL])
    val postsWithMeeting: MutableSet<Post> = mutableSetOf()

) : ParticipantsContainer {

    @JsonIgnore
    @OneToMany(targetEntity = Image::class, mappedBy = "meeting")
    val images: MutableList<Image> = mutableListOf()

    @JsonIgnore
    @OneToOne(
        targetEntity = Invitation::class,
        mappedBy = "meeting",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val invitation: Invitation? = null

    val numberOfParticipants: Int = participants.size

    val isVisitedMeeting: Boolean
        get() {
            val now = Date.from(Instant.now())
            val format = SimpleDateFormat("MM-dd-yyyy HH:mm")
            val dateStr = endDate ?: startDate
            return format.parse(dateStr).before(now)
        }

    val isPlannedMeeting: Boolean
        get() = !isVisitedMeeting
}