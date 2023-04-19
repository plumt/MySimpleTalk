package com.yun.mysimpletalk.ui.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.KAKAO
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.NAVER
import com.yun.mysimpletalk.databinding.FragmentLoginBinding
import com.yun.mysimpletalk.util.AuthUtil.naverLogin
import com.yun.mysimpletalk.util.AuthUtil.naverLoginCallBack
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
                        NidOAuthLogin().callProfileApi(naverLoginCallBack { result ->
                            if (result != null) {
                                Log.d("lys", "naver login success > ${result.profile!!.id}")
                            } else serverError()
                        })
                    } else serverError()
                }
            }
            KAKAO -> {

            }
            else -> serverError()
        }
    }

    /**
     * 네이버 로그인 리스너
     */
//    private val naverLoginCallback = object : NidProfileCallback<NidProfileResponse> {
//        override fun onSuccess(result: NidProfileResponse) {
//            Log.d("lys", "naver login success > result : $result")
//
//        }
//
//        override fun onError(errorCode: Int, message: String) {
//            serverError()
//            Log.e("lys", "naver login onError > code:${errorCode} message:$message")
//        }
//
//        override fun onFailure(httpStatus: Int, message: String) {
//            serverError()
//            Log.e("lys", "naver login onFailure > httpStatus:${httpStatus} message:$message")
//        }
//    }

    /**
     * server error
     */
    private fun serverError() {
        Toast.makeText(requireActivity(), "서버 연결에 실패했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }
}