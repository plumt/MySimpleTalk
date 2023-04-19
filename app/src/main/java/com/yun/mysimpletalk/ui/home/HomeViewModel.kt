package com.yun.mysimpletalk.ui.home

import android.app.Application
import com.yun.mysimpletalk.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {
}