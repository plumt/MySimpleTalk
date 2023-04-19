package com.yun.mysimpletalk.ui.chat

import android.app.Application
import com.yun.mysimpletalk.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {
}