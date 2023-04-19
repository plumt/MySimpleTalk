package com.yun.mysimpletalk.data

class UserModel {
    data class Info(
        val userId: String,
        val pushToken: String,
        val nickName: String,
        val loginType: String
    )
}