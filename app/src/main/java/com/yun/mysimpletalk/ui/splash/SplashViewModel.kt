package com.yun.mysimpletalk.ui.splash

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.common.constants.AuthConstants
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application,
    private val sPrefs: PreferenceUtil
) : BaseViewModel(application) {

    private val fs = FirebaseFirestore.getInstance()

    fun memberCheck(userId: String, callBack: (String) -> Unit) {
        fs.collection("User")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    document.data?.forEach { (key, value) ->
                        Log.d("lys", "data > $key $value")
                    }
                    callBack(AuthConstants.UserState.MEMBER)
                } else callBack(AuthConstants.UserState.SIGNUP)
            }.addOnFailureListener { callBack(AuthConstants.UserState.ERROR) }
    }
}