package com.yun.mysimpletalk.ui.wait

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.data.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaitViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    /**
     * 친구 요청한 유저 리스트
     */
    val waitUsers = ListLiveData<UserModel.User>()

    fun checkWaitCount(userInfo: UserModel.Info) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.Path.USER)
            .document(userInfo.userId)
            .get()
            .addOnSuccessListener {
                val wait = it.get("wait") as? ArrayList<String>
                if (wait == null || wait.isEmpty()) {
                } else {
                    selectFriend(wait)
                }
                Log.d("lys", "wait > $wait")
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun selectFriend(documents: ArrayList<String>) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.Path.USER)
            .whereIn(FieldPath.documentId(), documents)
            .get()
            .addOnSuccessListener {
                it.forEachIndexed { index, snap ->
                    waitUsers.add(UserModel.User(index, snap.id, snap.getString("name")!!))
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}