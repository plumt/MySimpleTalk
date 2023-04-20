package com.yun.mysimpletalk.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseUtil {

    fun getToken(callBack: (String) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { callBack(it) }
            .addOnFailureListener { callBack("") }
    }

    fun updateToken(userId: String, token: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("User")
            .document(userId)
            .update("token", token)
            .addOnCompleteListener { callBack(it.isSuccessful) }
    }



}