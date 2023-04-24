package com.yun.mysimpletalk.data.model

import com.google.firebase.Timestamp
import com.yun.mysimpletalk.base.Item
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        val read: String,
        val profile: String,
        // 0 > 이름 및 프로필 중복 x > 친구 메시지 적용
        // 1 > 날짜 > 친구, 나 메시지 적용
        var skip: List<Boolean>
    ) : Item() {
        fun dateConvert(date: Timestamp): String {
            val formatter = SimpleDateFormat("a h:m")
            return formatter.format(Date(date.seconds * 1000))
        }
    }
}