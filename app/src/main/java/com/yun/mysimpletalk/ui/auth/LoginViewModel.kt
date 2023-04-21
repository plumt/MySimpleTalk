package com.yun.mysimpletalk.ui.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.util.FirebaseUtil.getToken
import com.yun.mysimpletalk.util.FirebaseUtil.insertUser
import com.yun.mysimpletalk.util.FirebaseUtil.memberCheck
import com.yun.mysimpletalk.util.FirebaseUtil.updateToken
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val sPrefs: PreferenceUtil
) : BaseViewModel(application) {

    private val _myId = MutableLiveData<String>()
    val myId: LiveData<String> get() = _myId

    fun memberCheck(userId: String, type: String, callBack: (String) -> Unit) {
        memberCheck(userId) { checkSuccess ->
            when (checkSuccess) {
                true -> getToken { token ->
                    updateToken(userId, token) { success ->
                        if (success) {
                            sPrefs.setString(mContext, "login", type)
                            _myId.value = userId
                            callBack(MEMBER)
                        } else callBack(ERROR)
                    }
                }
                false -> callBack(SIGNUP)
                else -> callBack(ERROR)
            }
        }
    }

    /**
     * 파이어베이스 유저 등록
     */
    fun fbSignUp(userId: String, nickName: String, type: String, callBack: (Boolean) -> Unit) {
        getToken { token ->
            insertUser(userId, signupParams(nickName, token, type)) { success ->
                if (success) sPrefs.setString(mContext, "login", type)
                callBack(success)
            }
        }
    }

    private fun signupParams(
        nickName: String, token: String, type: String
    ) = mapOf(
        "name" to nickName,
        "token" to token,
        "type" to type,
        "profile" to "",
        "friend" to listOf<String>(),
        "block" to listOf<String>(),
        "wait" to listOf<String>()
    )
}