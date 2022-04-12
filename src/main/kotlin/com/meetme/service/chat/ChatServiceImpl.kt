package com.meetme.service.chat

import com.meetme.db.chat.Chat
import com.meetme.db.chat.ChatDao
import com.meetme.db.chat.Message
import com.meetme.util.doIfExist
import com.meetme.domain.dto.chat.GetMessagesRequestDto
import com.meetme.domain.dto.chat.SendMessageRequestDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ChatServiceImpl @Autowired constructor(
    private var chatDao: ChatDao,
    private var messageService: MessageService,
) : ChatService {

    private val logger = LoggerFactory.getLogger(ChatServiceImpl::class.java)

    override fun createChat(): Chat {
        val newChat = Chat()
        return chatDao.save(newChat)
    }

    override fun deleteChat(chat: Chat) {
        chatDao.delete(chat)
    }

    override fun sendMessage(sendMessageRequestDto: SendMessageRequestDto): Long =
        sendMessageRequestDto.chatId.doIfExist(chatDao, logger) { chat ->
            val message = messageService.sendMessage(
                chat = chat,
                content = sendMessageRequestDto.content,
                userId = sendMessageRequestDto.senderId
            )
            chat.messages.add(message)
            chatDao.save(chat)
            message.id
        }

    override fun getMessages(requestData: GetMessagesRequestDto): List<Message> =
        requestData.chatId.doIfExist(chatDao, logger) { chat ->
            with(requestData) {
                getMessageList(chat, anchor, messagesNumber)
            }
        }

    override fun deleteMessage(messageId: Long) {
        val message = messageService.getMessage(messageId)
        val chat = message.chat
        chat.messages.remove(message)
        messageService.deleteMessage(messageId)
        chatDao.save(chat)
    }

    private fun getMessageList(chat: Chat, anchor: Long, messagesNumber: Int): List<Message> {
        if (messagesNumber < 0)
            throw IllegalArgumentException("messagesNumber must be not negative")
        val reversedMessages = chat.messages.sortedByDescending(Message::timestamp)
        if (anchor == 0L) {
            val endIndex = min(messagesNumber, reversedMessages.size)
            return reversedMessages.subList(0, endIndex)
        }

        val startIndex = reversedMessages.indexOfFirst { message -> message.id == anchor }
        val endIndex = min(startIndex + messagesNumber + 1, reversedMessages.size)
        return reversedMessages.subList(startIndex + 1, endIndex)
    }
}