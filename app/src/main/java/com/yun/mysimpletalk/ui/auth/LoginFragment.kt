package com.yun.mysimpletalk.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.KAKAO
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.NAVER
import com.yun.mysimpletalk.databinding.FragmentLoginBinding
import com.yun.mysimpletalk.util.AuthUtil.kakaoLogin
import com.yun.mysimpletalk.util.AuthUtil.kakaoLoginCallBack
import com.yun.mysimpletalk.util.AuthUtil.naverLogin
import com.yun.mysimpletalk.util.AuthUtil.naverLoginCallBack
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

        binding.let { v ->
            v.btnNaver.setOnClickListener {
                snsLogin(NAVER)
            }
            v.btnKakao.setOnClickListener {
                snsLogin(KAKAO)
            }
        }
    }

    private fun snsLogin(loginType: String) {
        // TODO 인터넷 연결 오류 체크 기능 추가 요함
        when (loginType) {
            NAVER -> {
                naverLogin(requireActivity()) { success ->
                    if (success) {
                        NidOAuthLogin().callProfileApi(naverLoginCallBack { user ->
                            if (user != null) {
                                Log.d("lys", "naver login success > ${user.profile!!.id}")
                            } else serverError()
                        })
                    } else serverError()
                }
            }
            KAKAO -> {
                kakaoLogin(requireActivity()) { success ->
                    if (success) {
                        kakaoLoginCallBack { user ->
                            if (user != null) {
                                Log.d("lys", "kakao login success > ${user.id}")
                            } else serverError()
                        }
                    } else serverError()
                }
            }
            else -> serverError()
        }
    }

    /**
     * server error
     */
    private fun serverError() {
        Toast.makeText(requireActivity(), "서버 연결에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }
}