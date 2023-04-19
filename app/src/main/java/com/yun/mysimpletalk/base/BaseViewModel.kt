package com.yun.mysimpletalk.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class BaseViewModel constructor(application: Application) : AndroidViewModel(application){
    val mContext = application.applicationContext
}