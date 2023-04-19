package com.yun.mysimpletalk.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.data.UserModel
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val sPrefs: PreferenceUtil
) : BaseViewModel(application) {

    private val _userInfo = MutableLiveData<UserModel.Info>()
    val userInfo: LiveData<UserModel.Info> get() = _userInfo

    private val fs = FirebaseFirestore.getInstance()

    fun memberCheck(userId: String, type: String, callBack: (String) -> Unit) {
        fs.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val info = UserModel.Info(
                        userId,
                        document.getString("token")!!,
                        document.getString("name")!!,
                        document.getString("type")!!
                    )
                    _userInfo.value = info
                    sPrefs.setString(mContext, "login", type)
                    callBack(MEMBER)
                } else callBack(SIGNUP)
            }.addOnFailureListener { callBack(ERROR) }
    }

    private fun fbSignUp(
        userId: String,
        nickName: String,
        type: String,
        callBack: (Boolean) -> Unit
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("lys", "FirebaseMessaging.getInstance().token > ${it.result}")
            fs.collection("User")
                .document(userId)
                .set(
                    mapOf(
                        "name" to nickName,
                        "token" to it.result,
                        "type" to type
                    )
                )
                .addOnSuccessListener { _ ->
                    val info = UserModel.Info(
                        userId,
                        it.result,
                        nickName,
                        type
                    )
                    _userInfo.value = info
                    sPrefs.setString(mContext, "login", type)
                    callBack(true)
                }
                .addOnFailureListener { callBack(false) }
        }.addOnFailureListener { callBack(false) }
    }

    fun nickNameCheck(userId: String, nickName: String, type: String, callBack: (Boolean) -> Unit) {
        fs.collection("User")
            .whereEqualTo("name", nickName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) fbSignUp(userId, nickName, type, callBack)
                else callBack(false)
            }
            .addOnFailureListener { callBack(false) }
    }
}