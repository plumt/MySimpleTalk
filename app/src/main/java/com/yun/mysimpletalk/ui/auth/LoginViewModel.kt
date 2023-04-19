package com.yun.mysimpletalk.ui.auth

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.ui.dialog.EdittextDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {
    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("lys", "FirebaseMessaging.getInstance().token > ${it.result}")
            pushToken = it.result
        }
    }

    private val fs = FirebaseFirestore.getInstance()
    private var pushToken = ""


    fun memberCheck(userId: String, callBack: (String) -> Unit) {
        fs.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    document.data?.forEach { (key, value) ->
                        Log.d("lys", "data > $key $value")
                    }
                    callBack(MEMBER)
                } else callBack(SIGNUP)
            }.addOnFailureListener { callBack(ERROR) }
    }

    private fun fbSignUp(userId: String, nickName: String, callBack: (Boolean) -> Unit) {
        fs.collection("User")
            .document(userId)
            .set(
                mapOf(
                    "name" to nickName,
                    "token" to pushToken
                )
            )
            .addOnSuccessListener { callBack(true) }
            .addOnFailureListener { callBack(false) }
    }

    fun nickNameCheck(userId: String, nickName: String, callBack: (Boolean) -> Unit) {
        fs.collection("User")
            .whereEqualTo("name", nickName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) fbSignUp(userId, nickName, callBack)
                else callBack(false)
            }
            .addOnFailureListener { callBack(false) }
    }
}