package com.yun.mysimpletalk.ui.chat.chatting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.base.BaseRecyclerAdapter
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.CHATS
import com.yun.mysimpletalk.data.model.ChatModel
import com.yun.mysimpletalk.databinding.FragmentChattingBinding
import com.yun.mysimpletalk.databinding.ItemChatBinding
import com.yun.mysimpletalk.ui.main.MainActivity
import com.yun.mysimpletalk.util.FirebaseUtil.selectChatRoom
import com.yun.mysimpletalk.util.FirebaseUtil.sendMessage
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sVM.let { sv ->
            sv.hideBottomNav()
        }
        arguments?.run {
            viewModel.friendInfo = sVM.friendUsers.value!!.find { it.userId == (getString("userId") ?: "") }!!
            chatRoomSetting(getString("userId") ?: "")
        }

        binding.btnTest.setOnClickListener {
            selectChatList(lastChat)
        }

        binding.rvChat.apply {
            (this.layoutManager as LinearLayoutManager).stackFromEnd = true
            adapter = object : BaseRecyclerAdapter.Create<ChatModel.Chatting, ItemChatBinding>(
                R.layout.item_chat,
                BR.itemChat,
                BR.chatListener
            ) {
                override fun onItemClick(item: ChatModel.Chatting, view: View) {}
                override fun onItemLongClick(item: ChatModel.Chatting, view: View): Boolean = true
            }
        }

        binding.btnSend.setOnClickListener {
            sendMessage(viewModel.roomId.value!!, sVM.myId.value!!, "test") {
                Log.d("lys", "sendMessage > $it")
            }
        }

        viewModel.roomId.observe(viewLifecycleOwner) {
            if (it != "") selectChatList(null)
        }
    }

    var chatListener: ListenerRegistration? = null
    var lastChat: DocumentSnapshot? = null

    private fun selectChatList(lastChat: DocumentSnapshot?) {
        FirebaseFirestore.getInstance().collection(CHATS)
            .document(viewModel.roomId.value!!)
            .collection("list")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .let { if (lastChat != null) it.startAfter(lastChat) else it }
            .limit(5) // 나중에 최대한으로 수정할 것
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.exception == null) {
                    val chatList = arrayListOf<ChatModel.Chatting>()
                    it.result.documents.forEach { snap ->
                        val date = (snap["timestamp"] as Timestamp)
                        val index = viewModel.chatting.sizes() + chatList.size
                        val id = snap.data!!["id"] as String
                        val message = snap.data!!["message"] as String
                        val read = snap.data!!["read"] as String
                        chatList.add(
                            ChatModel.Chatting(
                                index, id, chatName(snap), message, date, read
                            )
                        )
                        this.lastChat = snap
                    }
                    chatList.reverse()
                    chatList.addAll(viewModel.chatting.value!!)
                    viewModel.chatting.value = chatList
                    Log.d("lys", "chat > ${viewModel.chatting.value}")
                    Log.d("lys", "chat[0] > ${viewModel.chatting.value!![0]}")
                    selectChatListListener()
                } else {
                    Log.e("lys", "error > ${it.exception?.message}")
                }
            }
    }

    private fun chatName(snap: DocumentSnapshot): String =
        if (snap.data!!["id"] == sVM.myId.value!!) "" else viewModel.friendInfo.nickName


    private fun selectChatListListener() {
        if (chatListener != null) return
        chatListener = FirebaseFirestore.getInstance().collection(CHATS)
            .document(viewModel.roomId.value!!)
            .collection("list")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (value == null) return@addSnapshotListener
                if (value.documents[0]["timestamp"] != null) {
                    val data = value.documents[0]
                    val date = data["timestamp"] as Timestamp
                    val id = data["id"] as String
                    val message = data["message"] as String
                    val read = data["read"] as String
                    if (viewModel.chatting.value!![viewModel.chatting.sizes() - 1].timestamp == date
                        && viewModel.chatting.value!![viewModel.chatting.sizes() - 1].message == message
                    ) return@addSnapshotListener
                    viewModel.chatting.add(
                        ChatModel.Chatting(
                            viewModel.chatting.sizes(), id, "", message, date, read
                        )
                    )
                    Log.d("lys", "viewModel.chatting > ${viewModel.chatting.value}")
                }
            }
    }

    private fun chatRoomSetting(userId: String) {
        selectChatRoom(sVM.myId.value!!, userId) {
            if (it == null) Log.d("lys", "서버 에러")
            else if (it.isEmpty) {
                Log.d("lys", "채팅방 아직 없음")
                createChatRoom(userId)
            } else {
                viewModel.roomId.value = it.documents[0].id
                Log.d("lys", "채팅방 있음 > ${it.documents[0].id}")
            }
        }
    }

    private fun createChatRoom(userId: String) {
        val document = FirebaseFirestore.getInstance().collection(CHATS).document()
        viewModel.roomId.value = document.id
        val newChatRoom = mapOf(
            "members" to listOf(sVM.userInfo.value!!.userId, userId), // 멤버 리스트
        )
        document.set(newChatRoom)
            .addOnSuccessListener {
                Log.d("lys", "chat room create")
            }.addOnFailureListener {
                Log.e("lys", "chat room not create")
            }
    }

    override fun onDestroy() {
        chatListener?.remove()
        super.onDestroy()
    }
}