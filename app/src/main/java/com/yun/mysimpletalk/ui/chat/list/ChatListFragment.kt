package com.yun.mysimpletalk.ui.chat.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.base.BaseRecyclerAdapter
import com.yun.mysimpletalk.data.model.ChatModel
import com.yun.mysimpletalk.databinding.FragmentChatListBinding
import com.yun.mysimpletalk.databinding.ItemRoomBinding
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

        sVM.showBottomNav()

        binding.rvRoom.apply {
            adapter = object : BaseRecyclerAdapter.Create<ChatModel.Room, ItemRoomBinding>(
                R.layout.item_room,
                BR.itemRoom,
                BR.roomListener
            ) {
                override fun onItemClick(item: ChatModel.Room, view: View) {
                    navigate(R.id.action_chatListFragment_to_chattingFragment, Bundle().apply {
                        putString("userId", item.members.find { it != sVM.myId.value!! })
                    })
                }

                override fun onItemLongClick(item: ChatModel.Room, view: View): Boolean = true
            }
        }

        selectChatList(sVM.myId.value!!) {
            val rooms = arrayListOf<ChatModel.Room>()
            it?.documents?.forEachIndexed { index, snap ->
                Log.d("lys", "selectChatList > ${snap.data}")
                rooms.add(
                    ChatModel.Room(
                        index,
                        snap.id,
                        snap["members"] as ArrayList<String>,
                        "채팅방제목"
                    )
                )
            }
            viewModel.chatList.value = rooms
        }
    }
}