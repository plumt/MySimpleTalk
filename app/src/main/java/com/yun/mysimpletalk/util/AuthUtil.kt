package com.yun.mysimpletalk.util

import android.content.Context
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.yun.mysimpletalk.R

object AuthUtil {

    /**
     * naver, kakao sns login sdk setting
     */
    fun snsSdkSetting(context: Context) {
        NaverIdLoginSDK.initialize(
            context,
            context.getString(R.string.social_login_info_naver_client_id),
            context.getString(R.string.social_login_info_naver_client_secret),
            context.getString(R.string.social_login_info_naver_client_name)
        )
        KakaoSdk.init(
            context,
            context.getString(R.string.social_login_info_kakao_native_key)
        )
    }

    /**
     * naver login
     */
    fun naverLogin(context: Context, callBack: (Boolean) -> Unit) {
        val oathLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                callBack(true)
            }

            override fun onError(errorCode: Int, message: String) {
                callBack(false)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                callBack(false)
            }
        }
        NaverIdLoginSDK.authenticate(context, oathLoginCallback)
    }

    /**
     * naver login callBack
     */
    fun naverLoginCallBack(callBack: (NidProfileResponse?) -> Unit): NidProfileCallback<NidProfileResponse> {
        return object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                callBack(result)
            }

            override fun onError(errorCode: Int, message: String) {
                callBack(null)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                callBack(null)
            }
        }
    }

    fun kakaoLogin(context: Context, callBack: (Boolean) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            // 카카오톡 앱 로그인
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        // 카카오톡에서 취소한 경우, 의도적 취소로 판단하여 로그인 취소 처리
                        callBack(false)
                        return@loginWithKakaoTalk
                    }
                    kakaoSignUp(context, callBack)
                } else if (token != null) {
                    callBack(true)
                }
            }
        } else {
            // 웹뷰 로그인
            kakaoSignUp(context, callBack)
        }
    }

    private fun kakaoSignUp(context: Context, callBack: (Boolean) -> Unit) {
        UserApiClient.instance.loginWithKakaoAccount(
            context,
            callback = { token, throwable ->
                if (throwable != null) callBack(false)
                else if (token != null) callBack(true)
            }
        )
    }

    fun kakaoLoginCallBack(callBack: (User?) -> Unit) {
        UserApiClient.instance.me { user, error ->
            callBack(user)
        }
    }
}