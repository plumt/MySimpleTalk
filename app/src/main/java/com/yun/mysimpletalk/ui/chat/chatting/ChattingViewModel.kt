package com.yun.mysimpletalk.ui.chat.chatting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.base.ListLiveData
import com.yun.mysimpletalk.data.model.ChatModel
import com.yun.mysimpletalk.data.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    val chatting = ListLiveData<ChatModel.Chatting>()

    val roomId = MutableLiveData("")

    lateinit var friendInfo: UserModel.User
}