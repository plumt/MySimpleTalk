package com.yun.mysimpletalk.util

import android.content.Context
import android.util.Log
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
//        KakaoSdk.init(
//            context,
//            context.getString(R.string.social_login_info_kakao_native_key)
//        )
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
}