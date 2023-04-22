package com.yun.mysimpletalk.ui.chat.list

import android.app.Application
import android.util.Log
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.data.model.ChatModel
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.util.FirebaseUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    val chatList = ListLiveData<ChatModel.Room>()

    fun selectChattingList(myId: String, friends: ArrayList<UserModel.User>) {
        FirebaseUtil.selectChatList(myId) { querySnap ->
            val rooms = arrayListOf<ChatModel.Room>()
            querySnap?.documents?.forEachIndexed { index, snap ->
                rooms.add(
                    ChatModel.Room(
                        index,
                        snap.id,
                        snap["members"] as ArrayList<String>,
                        friends.find { it.userId == (snap["members"] as ArrayList<String>).find { it != myId } }!!.nickName
                    )
                )
            }
            chatList.value = rooms
        }
    }
}