package com.yun.mysimpletalk.ui.auth

import android.app.Application
import com.yun.mysimpletalk.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(application: Application) : BaseViewModel(application) {
}