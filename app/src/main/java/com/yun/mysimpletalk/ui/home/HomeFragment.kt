package com.yun.mysimpletalk.ui.home

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.model.mutation.FieldMask
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
import com.yun.mysimpletalk.databinding.FragmentHomeBinding
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

        sharedViewModel.let { sv ->
            sv.userInfo.observe(viewLifecycleOwner) {
                Log.d("lys", "userInfo > $it")
            }

            sv.showBottomNav()

            if (sv.userInfo.value != null) {
                binding.tv.text = sv.userInfo.value.toString()
            }
        }

        binding.let { v ->
            v.btnAddFriend.setOnClickListener {
                searchFriend()
            }
            v.btnCheckWait.setOnClickListener {
                sharedViewModel.hideBottomNav()
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
                        if (id.isNullOrEmpty()) {
                            Toast.makeText(
                                requireActivity(),
                                "해당 닉네임을 가지고 있는 사용자가 존재하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            dismissDialog()
                            addFriend(id)
                        }
                    }
                }
            })
        }
    }

    private fun addFriend(friendId: String) {
        if (friendId == sharedViewModel.userInfo.value!!.userId) {
            Toast.makeText(requireActivity(), "본인", Toast.LENGTH_SHORT).show()
        }
        viewModel.addFriend(
            friendId,
            sharedViewModel.userInfo.value!!.userId
        ) { success ->
            if (success) Toast.makeText(requireActivity(), "친추 성공", Toast.LENGTH_SHORT).show()
        }
    }


}