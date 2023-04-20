package com.yun.mysimpletalk.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
import com.yun.mysimpletalk.databinding.FragmentHomeBinding
import com.yun.mysimpletalk.ui.dialog.EdittextDialog
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
        }

        binding.let { v ->
            v.btnAddFriend.setOnClickListener {
                searchFriend()
            }
        }
    }

    fun searchFriend() {
        EdittextDialog().run {
            showDialog(requireActivity(), "검색", "검색하실 닉네임을 입력해 주세요")
            setDialogListener(object : EdittextDialog.CustomDialogListener {
                override fun onResult(result: String) {
                    nickNameCheck(result) { friendId ->
                        if (friendId != "") {
                            dismissDialog()
                            addFriend(friendId)
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "해당 닉네임을 가지고 있는 사용자가 존재하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        }
    }

    fun nickNameCheck(nickName: String, callBack: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection(USER)
            .whereEqualTo("name", nickName)
            .get()
            .addOnSuccessListener { documents ->
                if(!documents.isEmpty && nickName != sharedViewModel.userInfo.value!!.nickName){
                    callBack(documents.documents[0].id)
                } else callBack("")
            }
            .addOnFailureListener { callBack("") }
    }

    fun addFriend(friendId: String) {
        FirebaseFirestore.getInstance().collection(USER)
            .document(friendId)
            .update("wait", FieldValue.arrayUnion(sharedViewModel.userInfo.value!!.userId))
            .addOnCompleteListener {
                Log.d("lys", "add Friends > ${it.isSuccessful}")
            }
    }
}