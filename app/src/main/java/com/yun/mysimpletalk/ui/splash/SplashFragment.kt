package com.yun.mysimpletalk.ui.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.KAKAO
import com.yun.mysimpletalk.common.constants.AuthConstants.LoginType.NAVER
import com.yun.mysimpletalk.common.constants.AuthConstants.UserState.MEMBER
import com.yun.mysimpletalk.databinding.FragmentSplashBinding
import com.yun.mysimpletalk.util.AuthUtil.kakaoLoginCallBack
import com.yun.mysimpletalk.util.AuthUtil.naverLoginCallBack
import com.yun.mysimpletalk.util.PreferenceUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_splash
    override fun setVariable(): Int = BR.splash
    override fun isOnBackEvent(): Boolean = true
    override fun onBackEvent() {}

    @Inject
    lateinit var sPrefs: PreferenceUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 하단 네비바 숨김
         */
        sVM.hideBottomNav()

        Handler().postDelayed({
            //TODO 지금은 딜레이 주고 있지만, 나중에는 앱 버전 체크와 퍼미션 체크(알림권한) 이후
            when (val type = sPrefs.getString(requireActivity(), "login")) {
                KAKAO, NAVER -> snsLogin(type)
                else -> navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 1000)

        viewModel.myId.observe(viewLifecycleOwner) { id ->
            if (id != null) sVM.setMyId(id)
        }
    }

    /**
     * @param true > 자동 로그인 > 홈 화면으로 이동
     * @param false > 수동 로그인 > 로그인 화면으로 이동
     */
    private fun moveScreen(isLogin: Boolean) {
        if (isLogin) navigate(R.id.action_splashFragment_to_homeFragment)
        else navigate(R.id.action_splashFragment_to_loginFragment)
    }

    private fun snsLogin(loginType: String) {
        // TODO 인터넷 연결 오류 체크 기능 추가 요함
        val callback: (String) -> Unit = { userId -> fbLogin(userId) }
        when (loginType) {
            NAVER -> {
                NaverIdLoginSDK.getAccessToken()?.run {
                    NidOAuthLogin().callProfileApi(naverLoginCallBack { user ->
                        if (user != null) callback(user.profile!!.id!!)
                        else moveScreen(false)
                    })
                } ?: moveScreen(false)
            }
            KAKAO -> {
                kakaoLoginCallBack { user ->
                    if (user != null) callback(user.id.toString())
                    else moveScreen(false)
                }
            }
            else -> moveScreen(false)
        }
    }

    /**
     * 파이어베이스 유저 정보 가져오기
     * 유저 정보가 있으면 자동 로그인 > 홈 화면으로 이동
     * 유저 정보가 없으면 로그인 화면으로 이동
     */
    private fun fbLogin(userId: String) {
        viewModel.autoLogin(userId) { result ->
            moveScreen(result == MEMBER)
        }
    }
}