package com.yun.mysimpletalk.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.data.model.UserModel
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private val _userInfo = MutableLiveData<UserModel.Info>()
    val userInfo: LiveData<UserModel.Info> get() = _userInfo

    private val _isBottomVisible = MutableLiveData(false)
    val isBottomVisible: LiveData<Boolean> get() = _isBottomVisible

    private val _myId = MutableLiveData<String>()
    val myId: LiveData<String> get() = _myId

    /**
     * 친구 유저 리스트
     */
    val friendUsers = ListLiveData<UserModel.User>()

    fun setUserInfo(info: Map<String, Any>?) {
        friendUsers.value = arrayListOf()
        if (info == null) _userInfo.value = null
        else {
            _userInfo.value = userInfoParams(info)
            selectFriend(userInfo.value!!.friends)
        }
    }

    private fun userInfoParams(info: Map<String, Any>) = UserModel.Info(
        "userId",
        info["token"].toString(),
        info["name"].toString(),
        info["profile"].toString(),
        info["type"].toString(),
        info["friend"] as ArrayList<String>,
        info["block"] as ArrayList<String>,
        info["wait"] as ArrayList<String>
    )

    private fun selectFriend(documents: ArrayList<String>) {
        FirebaseFirestore.getInstance().collection(FirebaseConstants.Path.USERS)
            .whereIn(FieldPath.documentId(), documents)
            .get()
            .addOnSuccessListener {
                val friends = arrayListOf<UserModel.User>()
                it.forEachIndexed { index, snap ->
                    friends.add(
                        UserModel.User(
                            index,
                            snap.id,
                            snap.getString("profile")!!,
                            snap.getString("name")!!
                        )
                    )
                }
                friendUsers.value = friends
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun setMyId(id: String?) {
        _myId.value = id
    }

    fun hideBottomNav() {
        _isBottomVisible.value = false
    }

    fun showBottomNav() {
        _isBottomVisible.value = true
    }
}