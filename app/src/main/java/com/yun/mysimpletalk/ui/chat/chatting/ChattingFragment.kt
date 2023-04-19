package com.yun.mysimpletalk.ui.chat.chatting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentChattingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingFragment : BaseFragment<FragmentChattingBinding, ChattingViewModel>() {
    override val viewModel: ChattingViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_chatting
    override fun isOnBackEvent(): Boolean = false
    override fun setVariable(): Int = BR.chatting
    override fun onBackEvent() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}