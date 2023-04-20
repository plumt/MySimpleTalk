package com.yun.mysimpletalk.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.util.FirebaseUtil.getToken
import com.yun.mysimpletalk.util.FirebaseUtil.updateToken
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

    fun memberCheck(userId: String, type: String, callBack: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    getToken { token ->
                        updateToken(userId, token) { success ->
                            if (success) {
                                val name = document.getString("name")!!
                                val profile = document.getString("profile")!!
                                val friends =
                                    document.get("friend") as? ArrayList<String> ?: arrayListOf()
                                val block =
                                    document.get("block") as? ArrayList<String> ?: arrayListOf()
                                val wait =
                                    document.get("wait") as? ArrayList<String> ?: arrayListOf()
                                setUserInfo(
                                    userId,
                                    token,
                                    name,
                                    profile,
                                    type,
                                    friends,
                                    block,
                                    wait
                                )
                                callBack(MEMBER)
                            } else callBack(ERROR)
                        }
                    }
                } else callBack(SIGNUP)
            }.addOnFailureListener { callBack(ERROR) }
    }

    fun fbSignUp(
        userId: String,
        nickName: String,
        profile: String,
        type: String,
        callBack: (Boolean) -> Unit
    ) {
        getToken { token ->
            FirebaseFirestore.getInstance().collection(USER)
                .document(userId)
                .set(signupParams(nickName, token, type))
                .addOnSuccessListener {
                    setUserInfo(
                        userId, token, nickName, profile, type, arrayListOf(), arrayListOf(),
                        arrayListOf()
                    )
                    callBack(true)
                }.addOnFailureListener { callBack(false) }
        }
    }

    private fun setUserInfo(
        userId: String,
        token: String,
        nickName: String,
        profile: String,
        type: String,
        friend: ArrayList<String>,
        block: ArrayList<String>,
        wait: ArrayList<String>
    ) {
        _userInfo.value =
            UserModel.Info(userId, token, nickName, profile, type, friend, block, wait)
        sPrefs.setString(mContext, "login", type)
    }

    private fun signupParams(nickName: String, token: String, type: String) = mapOf(
        "name" to nickName,
        "token" to token,
        "type" to type
    )
}