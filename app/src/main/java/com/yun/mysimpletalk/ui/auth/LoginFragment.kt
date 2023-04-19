package com.yun.mysimpletalk.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override val viewModel: LoginViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_login
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.login
    override fun onBackEvent() {
        //TODO 뒤로가기 이벤트
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}