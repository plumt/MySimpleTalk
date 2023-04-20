package com.yun.mysimpletalk.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USERS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.ERROR
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.EXISTS
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Result.NOT_EXISTS
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.util.FirebaseUtil.nickNameCheck
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    /**
     * 친구 유저 리스트
     */
    val friendUsers = ListLiveData<UserModel.User>()

    fun addFriend(friendId: String, userId: String, callBack: (Boolean) -> Unit) {
        if (friendId == userId) {
            callBack(false)
            return
        }

        nickNameCheck(friendId, userId) { result ->
            Log.d("lys", "success > $result")
            when(result){
                EXISTS ->{
                    // 이미 등록
                    Toast.makeText(mContext,"이미 등록하셨습니다",Toast.LENGTH_SHORT).show()
                    callBack(true)
                }
                NOT_EXISTS -> {
                    // 등록 가능
                    FirebaseFirestore.getInstance().collection(FirebaseConstants.Path.USERS)
                        .document(friendId)
                        .update("wait", FieldValue.arrayUnion(userId))
                        .addOnCompleteListener {
                            Log.d("lys", "add Friends > ${it.isSuccessful}")
                            callBack(it.isSuccessful)
                        }
                }
                ERROR -> { callBack(false)}
            }
        }
    }

    fun selectFriend(documents: ArrayList<String>) {
        FirebaseFirestore.getInstance().collection(USERS)
            .whereIn(FieldPath.documentId(), documents)
            .get()
            .addOnSuccessListener {
                val friends = arrayListOf<UserModel.User>()
                it.forEachIndexed { index, snap ->
                    friends.add(UserModel.User(index, snap.id,snap.getString("profile")!! ,snap.getString("name")!!))
                }
                friendUsers.value = friends
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}