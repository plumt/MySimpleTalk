package com.yun.mysimpletalk.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER

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


}