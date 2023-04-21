package com.yun.mysimpletalk.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.navercorp.nid.oauth.NidOAuthLogin
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.KAKAO
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.NAVER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.ERROR
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.SIGNUP
import com.yun.mysimpletalk.databinding.FragmentLoginBinding
import com.yun.mysimpletalk.ui.dialog.EdittextDialog
import com.yun.mysimpletalk.util.AuthUtil.kakaoLogin
import com.yun.mysimpletalk.util.AuthUtil.kakaoLoginCallBack
import com.yun.mysimpletalk.util.AuthUtil.naverLogin
import com.yun.mysimpletalk.util.AuthUtil.naverLoginCallBack
import com.yun.mysimpletalk.util.AuthUtil.snsLogout
import com.yun.mysimpletalk.util.FirebaseUtil.nickNameCheck
import com.yun.mysimpletalk.util.Util.keyHash
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override val viewModel: LoginViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_login
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.login
    override fun onBackEvent() {
        //TODO 뒤로가기 이벤트
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyHash(requireActivity())

        /**
         * 바텀 네비바 숨김
         */
        sVM.hideBottomNav()

        binding.btnNaver.setOnClickListener { snsLogin(NAVER) }
        binding.btnKakao.setOnClickListener { snsLogin(KAKAO) }

        viewModel.myId.observe(viewLifecycleOwner) { id ->
            if (id != null) sVM.setMyId(id)
        }
    }

    private fun snsLogin(loginType: String) {
        // TODO 인터넷 연결 오류 체크 기능 추가 요함

        val callback: (String) -> Unit = { userId ->
            fbLogin(userId, loginType)
        }
        when (loginType) {
            NAVER -> {
                naverLogin(requireActivity()) { success ->
                    if (success) NidOAuthLogin().callProfileApi(naverLoginCallBack { user ->
                        if (user != null) callback(user.profile!!.id!!)
                        else serverError()
                    })
                    else serverError()
                }
            }
            KAKAO -> {
                kakaoLogin(requireActivity()) { success ->
                    if (success) kakaoLoginCallBack { user ->
                        if (user != null) callback(user.id.toString())
                        else serverError()
                    }
                    else serverError()
                }
            }
            else -> serverError()
        }
    }

    /**
     * 파이어베이스 유저 정보 확인
     */
    private fun fbLogin(userId: String, loginType: String) {
        viewModel.memberCheck(userId, loginType) {
            when (it) {
                MEMBER -> loginSuccess()
                SIGNUP -> showNicknameInputDialog(userId, loginType)
                ERROR -> snsLogout(loginType) { serverError() }
            }
        }
    }

    private fun showNicknameInputDialog(userId: String, loginType: String) {
        EdittextDialog().run {
            showDialog(requireActivity(), "회원가입", "사용하실 닉네임을 입력해 주세요")
            setDialogListener(object : EdittextDialog.CustomDialogListener {
                override fun onResult(result: String) {
                    nickNameCheck(result) { id ->
                        when (id) {
                            "" -> {
                                viewModel.fbSignUp(userId, result, loginType) { success ->
                                    if (success) {
                                        dismissDialog()
                                        loginSuccess()
                                    } else serverError()
                                }
                            }
                            null -> serverError()
                            else -> nickNameError()
                        }
                    }
                }
            })
        }
    }

    private fun loginSuccess() {
        navigate(R.id.action_loginFragment_to_homeFragment)
    }

    /**
     * server error
     */
    private fun serverError() {
        Toast.makeText(requireActivity(), "서버 연결에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }

    private fun nickNameError() {
        Toast.makeText(requireActivity(), "해당 닉네임은 사용하실 수 없습니다", Toast.LENGTH_SHORT).show()
    }
}