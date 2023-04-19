package com.yun.mysimpletalk.ui.chat.chatting

import android.app.Application
import com.yun.mysimpletalk.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChattingViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
}