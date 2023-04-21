package com.yun.mysimpletalk.ui.chat.chatting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.CHATS
import com.yun.mysimpletalk.databinding.FragmentChattingBinding
import com.yun.mysimpletalk.ui.main.MainActivity
import com.yun.mysimpletalk.util.FirebaseUtil.selectChatRoom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingFragment : BaseFragment<FragmentChattingBinding, ChattingViewModel>() {
    override val viewModel: ChattingViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_chatting
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.chatting
    override fun onBackEvent() {
        (requireActivity() as MainActivity).binding.bottomNavView.selectedItemId = R.id.chatting
    }

    var chatId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sVM.let { sv ->
            sv.hideBottomNav()
        }
        arguments?.run {
            chatRoomSetting(getString("userId") ?: "")
        }


    }

    private fun chatRoomSetting(userId: String) {
        selectChatRoom(sVM.myId.value!!, userId) {
            if (it == null) Log.d("lys", "서버 에러")
            else if (it.isEmpty) {
                Log.d("lys", "채팅방 아직 없음")
                createChatRoom(userId)
            } else {
                Log.d("lys", "채팅방 있음 > ${it.documents[0].id}")
            }
        }
    }

    private fun createChatRoom(userId: String){
        val document = FirebaseFirestore.getInstance().collection(CHATS).document()
        chatId = document.id
        val newChatRoom = mapOf(
            "members" to listOf(sVM.userInfo.value!!.userId, userId), // 멤버 리스트
        )
        document.set(newChatRoom)
            .addOnSuccessListener {
                Log.d("lys","chat room create")
            }.addOnFailureListener {
                Log.e("lys","chat room not create")
            }
    }
}