package com.yun.mysimpletalk.ui.chat.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentChatListBinding
import com.yun.mysimpletalk.util.FirebaseUtil.selectChatList
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

        sVM.let { sv ->

            sv.showBottomNav()
        }

        selectChatList(sVM.userInfo.value!!.userId){
            viewModel.chatList.clear()
            if(it != null){
                it.forEach {
                    Log.d("lys","members > ${it.data["members"]}")
                    val members = it.data["members"] as ArrayList<String>
                    viewModel.chatList.add(if(members[0] == sVM.userInfo.value!!.userId) members[1] else members[0])
                }
                //TODO 친구 목록과 비교해서 이름과 이미지 파일 가져와서 리사이클러 뷰로 나열하면 될듯
            }
        }
    }
}