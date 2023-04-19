package com.yun.mysimpletalk.ui.auth

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.base.BaseViewModel
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

    val fs = FirebaseFirestore.getInstance()
    var pushToken = ""


    fun memberCheck(userId: String, callBack: (Boolean) -> Unit) {
        fs.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    document.data?.forEach { (key, value) ->
                        Log.d("lys", "data > $key $value")
                    }
                } else {
                    //TODO 닉네임을 먼저 입력 받고, 회원 가입 진행해야 함
//                    fbSignUp(userId, callBack)
                }
            }.addOnFailureListener { e ->
                Log.e("lys", "memberCheck fail > $e")
                callBack(false)
            }
    }

    private fun fbSignUp(userId: String, callBack: (Boolean) -> Unit) {
        fs.collection("User")
            .document(userId)
            .set(
                mapOf(
                    "name" to "testName",
                    "token" to pushToken
                )
            )
            .addOnSuccessListener {
                Log.d("lys", "snsSignUp success")
                callBack(true)
            }
            .addOnFailureListener { e ->
                Log.e("lys", "snsSignUp fail > $e")
                callBack(false)
            }
    }
}