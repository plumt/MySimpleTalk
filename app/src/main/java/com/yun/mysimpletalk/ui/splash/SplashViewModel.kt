package com.yun.mysimpletalk.ui.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.data.UserModel
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application,
    private val sPrefs: PreferenceUtil
) : BaseViewModel(application) {

    private val fs = FirebaseFirestore.getInstance()

    private val _userInfo = MutableLiveData<UserModel.Info>()
    val userInfo: LiveData<UserModel.Info> get() = _userInfo

    fun memberCheck(userId: String, callBack: (String) -> Unit) {
        fs.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    updatePushToken(
                        userId,
                        document.getString("name")!!,
                        document.getString("type")!!,
                        callBack
                    )
                } else callBack(SIGNUP)
            }.addOnFailureListener { callBack(ERROR) }
    }

    private fun updatePushToken(
        userId: String,
        name: String,
        type: String,
        callBack: (String) -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {t ->
            fs.collection("User")
                .document(userId)
                .update("token", t.result)
                .addOnCompleteListener {
                    val info = UserModel.Info(userId, t.result, name, type)
                    _userInfo.value = info
                    callBack(MEMBER)
                }
                .addOnFailureListener {
                    callBack(ERROR)
                }
        }.addOnFailureListener { callBack(ERROR) }

    }
}