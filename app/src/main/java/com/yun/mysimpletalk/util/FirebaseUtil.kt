package com.yun.mysimpletalk.util

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Field.MEMBERS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.CHATS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USERS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.ERROR
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.EXISTS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.NOT_EXISTS
import com.yun.mysimpletalk.data.model.ChatModel
import com.yun.mysimpletalk.data.model.UserModel

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
            .collection(USERS)
            .document(userId)
            .update("token", token)
            .addOnCompleteListener { callBack(it.isSuccessful) }
    }

    /**
     * 유저 삭제
     */
    fun deleteUser(userId: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(USERS)
            .document(userId)
            .delete().addOnCompleteListener { callBack(it.isSuccessful) }
    }

    /**
     * 닉네임 체크
     */
    fun nickNameCheck(nickName: String, callBack: (String?) -> Unit) {
        FirebaseFirestore.getInstance().collection(USERS)
            .whereEqualTo("name", nickName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) callBack("")
                else callBack(documents.documents[0].id)
            }
            .addOnFailureListener { callBack(null) }
    }

    /**
     * 유저 등록
     */
    fun insertUser(userId: String, info: Map<String, Any>, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection(USERS)
            .document(userId)
            .set(info)
            .addOnCompleteListener { callBack(it.isSuccessful) }
    }

    /**
     * 가입된 유저인지 체크
     */
    fun memberCheck(userId: String, callBack: (Boolean?) -> Unit) {
        FirebaseFirestore.getInstance().collection(USERS).document(userId).get()
            .addOnSuccessListener { document ->
                callBack(document != null && document.exists())
            }
            .addOnFailureListener { callBack(null) }
    }

    /**
     * 닉네임 배열 체크
     */
    fun nickNameCheck(userId: String, userId2: String, callBack: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection(USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val arrayField = documentSnapshot.get("wait") as? ArrayList<String>
                if (arrayField?.contains(userId2) == true) callBack(EXISTS)
                else callBack(NOT_EXISTS)
            }
            .addOnFailureListener { callBack(ERROR) }
    }

    /**
     * 채팅방 리스트
     */
    fun selectChatList(userId: String, callBack: (QuerySnapshot?) -> Unit) {
        FirebaseFirestore.getInstance().collection(CHATS)
            .whereArrayContains(MEMBERS, userId)
            .get()
            .addOnSuccessListener {
                callBack(it)
            }
            .addOnFailureListener { callBack(null) }
    }

    /**
     * 채팅방 유무 확인
     */
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

    /**
     * 메시지 보내기
     */
    fun sendMessage(docId: String, userId: String, message: String, callBack: (Boolean) -> Unit) {
        val msg = mapOf(
            "id" to userId,
            "message" to message,
            "read" to "1",
            "timestamp" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance().collection(CHATS)
            .document(docId)
            .collection("list")
            .document()
            .set(msg)
            .addOnCompleteListener { callBack(it.isSuccessful) }
    }
}