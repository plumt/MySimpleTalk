package com.yun.mysimpletalk.data.model

import com.yun.mysimpletalk.base.Item

class ChatModel {

    data class Room(
        override var id: Int,
        val roomId: String,
        val members: ArrayList<String>,
        val title: String
    ) : Item()
}