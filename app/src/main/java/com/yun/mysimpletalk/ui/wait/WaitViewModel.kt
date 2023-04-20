package com.yun.mysimpletalk.ui.wait

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
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
                    waitUsers.add(
                        UserModel.User(
                            index,
                            snap.id,
                            snap.getString("profile")!!,
                            snap.getString("name")!!
                        )
                    )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun removeWaitUser(
        flag: String,
        myId: String,
        userId: String,
        callBack: (Boolean) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(myId)
            .update("wait", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                when (flag) {
                    "block" -> addBlockUser(myId, userId, callBack)
                    "accept" -> addFriendUser(myId, userId, callBack)
                    else -> callBack(false)
                }

            }
            .addOnFailureListener { callBack(false) }
    }

    private fun addBlockUser(myId: String, userId: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(myId)
            .update("block", FieldValue.arrayUnion(userId))
            .addOnCompleteListener {
                Log.d("lys", "add block > ${it.isSuccessful}")
                callBack(it.isSuccessful)
            }
    }

    private fun addFriendUser(myId: String, userId: String, callBack: (Boolean) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(myId)
            .update("friend", FieldValue.arrayUnion(userId))
            .addOnCompleteListener {
                Log.d("lys", "add friend > ${it.isSuccessful}")
                callBack(it.isSuccessful)
            }
    }

    fun blockUser(myId: String, item: UserModel.User, callBack: (Boolean) -> Unit) {
        removeWaitUser("block", myId, item.userId) { success ->
            if (success) {
                waitUsers.remove(item)
                callBack(true)
            } else callBack(false)
        }
    }

    fun acceptUser(myId: String, item: UserModel.User, callBack: (Boolean) -> Unit) {
        removeWaitUser("accept", myId, item.userId) { success ->
            if (success) {
                waitUsers.remove(item)
                callBack(true)
            } else callBack(false)
        }
    }
}