package com.yun.mysimpletalk.data.model

import com.yun.mysimpletalk.base.Item

class UserModel {
    data class Info(
        val userId: String,
        val pushToken: String,
        val nickName: String,
        val loginType: String
    )

    data class User(
        override var id: Int,
        val userId: String,
        val nickName: String
    ) : Item()
}