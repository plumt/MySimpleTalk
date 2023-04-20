package com.yun.mysimpletalk.ui.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentSettingBinding
import com.yun.mysimpletalk.ui.main.MainActivity
import com.yun.mysimpletalk.util.AuthUtil.snsLogout
import com.yun.mysimpletalk.util.AuthUtil.snsSignout
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
//                pushTokenClear()
            }

        }
    }

    /**
     * sns logout
     */
    private fun snsLogout() {
        snsLogout(sPrefs.getString(requireContext(), "login") ?: "") { success ->
            if (success) pushTokenClear()
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
            if (success) pushTokenClear()
            else {
                //TODO 로그아웃 실패
            }
        }
    }

    /**
     * fcm token delete
     */
    private fun pushTokenClear() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            fs.collection("User")
                .document(sharedViewModel.userInfo.value!!.userId)
                .update("token", "")
                .addOnCompleteListener { moveLoginScreen() }
                .addOnFailureListener {
                    //TODO 토큰 삭제 실패
                }
        }.addOnFailureListener {
            //TODO 토큰 삭제 실패
        }
    }

    private fun moveLoginScreen(){
        sharedViewModel.setUserInfo(null)
        navigate(R.id.action_settingFragment_to_loginFragment)
        (requireActivity() as MainActivity).binding.bottomNavView.selectedItemId = R.id.home
    }
}