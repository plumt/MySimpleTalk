package com.yun.mysimpletalk.ui.chat.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentChatListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding, ChatListViewModel>() {
    override val viewModel: ChatListViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_chat_list
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.chatList
    override fun onBackEvent() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}