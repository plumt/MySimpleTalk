package com.yun.mysimpletalk.ui.setting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.LOGOUT
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNOUT
import com.yun.mysimpletalk.common.constants.FirebaseConstants.Path.USER
import com.yun.mysimpletalk.databinding.FragmentSettingBinding
import com.yun.mysimpletalk.ui.main.MainActivity
import com.yun.mysimpletalk.util.AuthUtil.snsLogout
import com.yun.mysimpletalk.util.AuthUtil.snsSignout
import com.yun.mysimpletalk.util.FirebaseUtil.deleteUser
import com.yun.mysimpletalk.util.FirebaseUtil.updateToken
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {
    override val viewModel: SettingViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_setting
    override fun setVariable(): Int = BR.setting
    override fun isOnBackEvent(): Boolean = true
    override fun onBackEvent() {

    }

    @Inject
    lateinit var sPrefs: PreferenceUtil

    private val fs = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let { v ->

            v.btnLogout.setOnClickListener {
                snsLogout()
            }

            v.btnSignout.setOnClickListener {
                snsSignout()
            }

        }
    }

    /**
     * sns logout
     */
    private fun snsLogout() {
        snsLogout(sPrefs.getString(requireContext(), "login") ?: "") { success ->
            if (success) deleteToken(LOGOUT)
            else {
                //TODO 로그아웃 실패
            }
        }
    }

    /**
     * sns signout
     */
    private fun snsSignout() {
        snsSignout(
            requireActivity(),
            sPrefs.getString(requireContext(), "login") ?: ""
        ) { success ->
            if (success) deleteToken(SIGNOUT)
            else {
                //TODO 로그아웃 실패
            }
        }
    }

    /**
     * fcm token delete
     */
    private fun deleteToken(state: String) {
        FirebaseMessaging.getInstance().deleteToken().addOnSuccessListener {
            when (state) {
                SIGNOUT -> {
                    //TODO 친구 리스트에서 해당 사용자 제거
                    //TODO 채팅 리스트에서 해당 사용자 제거
                    deleteUser(sharedViewModel.userInfo.value!!.userId) { success ->
                        if (success) moveLoginScreen()
                        else {
                            Log.e("lys","회원탈퇴 실패")
                        } // TODO 회원탈퇴 실패
                    }
                }
                LOGOUT -> {
                    updateToken(sharedViewModel.userInfo.value!!.userId, "") { success ->
                        if (success) moveLoginScreen()
                        else {
                            Log.e("lys","로그아웃 실패")
                        } // TODO 토크 삭제 실패
                    }
                }
            }
        }.addOnFailureListener {
            //TODO 토큰 삭제 실패
        }
    }


    private fun moveLoginScreen() {
        sharedViewModel.setUserInfo(null)
        navigate(R.id.action_settingFragment_to_loginFragment)
        (requireActivity() as MainActivity).binding.bottomNavView.selectedItemId = R.id.home
    }
}