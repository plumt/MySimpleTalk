package com.yun.mysimpletalk.ui.chat.list

import android.app.Application
import com.yun.mysimpletalk.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    var chatList = arrayListOf<String>()
}