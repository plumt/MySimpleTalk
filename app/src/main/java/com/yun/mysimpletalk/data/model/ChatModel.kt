package com.yun.mysimpletalk.data.model

import com.google.firebase.Timestamp
import com.yun.mysimpletalk.base.Item

class ChatModel {

    data class Room(
        override var id: Int,
        val roomId: String,
        val members: ArrayList<String>,
        val title: String
    ) : Item()

    data class Chatting(
        override var id: Int,
        val userId: String,
        val name: String,
        val message: String,
        val timestamp: Timestamp,
        val read: String
    ) : Item()
}