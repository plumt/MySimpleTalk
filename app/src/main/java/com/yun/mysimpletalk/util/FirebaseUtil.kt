package com.yun.mysimpletalk.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Field.MEMBERS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.CHATS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.ERROR
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.EXISTS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.NOT_EXISTS

object FirebaseUtil {

    /**
     * 푸쉬 토큰 리턴
     */
    fun getToken(callBack: (String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { callBack(it) }
            .addOnFailureListener { callBack("") }
    }

    /**
     * 푸쉬 토큰 업데이트
     */
    fun updateToken(userId: String, token: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(USER)
            .document(userId)
            .update("token", token)
            .addOnCompleteListener { callBack(it.isSuccessful) }
    }

    /**
     * 유저 삭제
     */
    fun deleteUser(userId: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(USER)
            .document(userId)
            .delete().addOnCompleteListener { callBack(it.isSuccessful) }
    }

    /**
     * 닉네임 체크
     */
    fun nickNameCheck(nickName: String, callBack: (String?) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .whereEqualTo("name", nickName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) callBack("")
                else callBack(documents.documents[0].id)
            }
            .addOnFailureListener { callBack(null) }
    }

    /**
     * 닉네임 배열 체크
     */
    fun nickNameCheck(userId: String, userId2: String, callBack: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val arrayField = documentSnapshot.get("wait") as? ArrayList<String>
                if (arrayField?.contains(userId2) == true) callBack(EXISTS)
                else callBack(NOT_EXISTS)
            }
            .addOnFailureListener { callBack(ERROR) }
    }

    fun selectChatList(userId: String, callBack: (QuerySnapshot?) -> Unit) {
        FirebaseFirestore.getInstance().collection(CHATS)
            .whereArrayContains(MEMBERS, userId)
            .get()
            .addOnSuccessListener {
                callBack(it)
            }
            .addOnFailureListener { callBack(null) }
    }

    fun selectChatRoom(userId1: String, userId2: String, callBack: (QuerySnapshot?) -> Unit) {
        val members = if (userId1 > userId2) listOf(listOf(userId1, userId2))
        else listOf(listOf(userId2, userId1))
        FirebaseFirestore.getInstance().collection(CHATS)
            .whereIn(MEMBERS, members)
            .get()
            .addOnSuccessListener {
                callBack(it)
            }
            .addOnFailureListener { callBack(null) }
    }


}