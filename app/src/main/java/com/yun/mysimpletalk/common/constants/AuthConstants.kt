package com.yun.mysimpletalk.common.constants

class AuthConstants {

    object LoginType {
        const val NAVER = "100001"
        const val KAKAO = "100002"
    }

    object UserState {
        const val ERROR = "10001"
        const val MEMBER = "10002"
        const val SIGNUP = "10003"
        const val SIGNOUT = "10004"
        const val LOGOUT = "10005"
    }

}