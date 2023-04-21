package com.yun.mysimpletalk.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.util.FirebaseUtil.getToken
import com.yun.mysimpletalk.util.FirebaseUtil.memberCheck
import com.yun.mysimpletalk.util.FirebaseUtil.updateToken
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    private val _myId = MutableLiveData<String>()
    val myId: LiveData<String> get() = _myId

    fun autoLogin(userId: String, callBack: (String) -> Unit) {
        memberCheck(userId) { checkSuccess ->
            when (checkSuccess) {
                true -> getToken { token ->
                    updateToken(userId, token) { updateSuccess ->
                        if (updateSuccess) {
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
}