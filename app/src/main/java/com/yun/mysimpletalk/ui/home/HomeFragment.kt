package com.yun.mysimpletalk.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.base.BaseRecyclerAdapter
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.databinding.FragmentHomeBinding
import com.yun.mysimpletalk.databinding.ItemHomeBinding
import com.yun.mysimpletalk.ui.dialog.EdittextDialog
import com.yun.mysimpletalk.util.FirebaseUtil.nickNameCheck
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_home
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.home
    override fun onBackEvent() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sVM.showBottomNav()

        binding.tv.text = sVM.userInfo.value.toString() ///////////

        sVM.friendUsers.observe(viewLifecycleOwner) { friends ->
            if (friends != null) viewModel.friendUsers.value = friends
        }

        binding.let { v ->

            v.rvHome.apply {
                adapter = object : BaseRecyclerAdapter.Create<UserModel.User, ItemHomeBinding>(
                    R.layout.item_home,
                    BR.itemHome,
                    BR.homeListener
                ) {
                    override fun onItemClick(item: UserModel.User, view: View) {
                        showToast(item.nickName)
                        navigate(R.id.action_homeFragment_to_chattingFragment, Bundle().apply {
                            putString("userId", item.userId)
                        })
                    }

                    override fun onItemLongClick(item: UserModel.User, view: View): Boolean = true
                }
            }

            v.btnAddFriend.setOnClickListener {
                searchFriend()
            }
            v.btnCheckWait.setOnClickListener {
                sVM.hideBottomNav()
                navigate(R.id.action_homeFragment_to_waitFragment)
            }
        }
    }

    private fun searchFriend() {
        EdittextDialog().run {
            showDialog(requireActivity(), "검색", "검색하실 닉네임을 입력해 주세요")
            setDialogListener(object : EdittextDialog.CustomDialogListener {
                override fun onResult(result: String) {
                    nickNameCheck(result) { id ->
                        if (id.isNullOrEmpty()) showToast("해당 닉네임을 가지고 있는 사용자가 존재하지 않습니다.")
                        else if (id == sVM.userInfo.value!!.userId) showToast("본인")
                        else {
                            dismissDialog()
                            addFriend(id)
                        }
                    }
                }
            })
        }
    }

    private fun addFriend(friendId: String) {
        viewModel.addFriend(
            friendId,
            sVM.userInfo.value!!.userId
        ) { success -> if (success) showToast("친추 성공") else showToast("잠시 후 다시 시도해 주세요.") }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }
}