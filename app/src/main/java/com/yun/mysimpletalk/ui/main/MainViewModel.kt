package com.yun.mysimpletalk.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yun.mysimpletalk.base.BaseViewModel
import com.yun.mysimpletalk.data.model.UserModel
import javax.inject.Inject

class MainViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private val _userInfo = MutableLiveData<UserModel.Info>()
    val userInfo: LiveData<UserModel.Info> get() = _userInfo

    private val _isBottomVisible = MutableLiveData(false)
    val isBottomVisible: LiveData<Boolean> get() = _isBottomVisible

    fun setUserInfo(info: UserModel.Info?){
        _userInfo.value = info
    }

    fun hideBottomNav(){
        _isBottomVisible.value = false
    }
    fun showBottomNav(){
        _isBottomVisible.value = true
    }
}