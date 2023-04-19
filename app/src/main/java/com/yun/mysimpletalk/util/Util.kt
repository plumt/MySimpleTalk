package com.yun.mysimpletalk.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

object Util {
    fun keyHash(context: Context){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        try {
            val information = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA").apply {
                    update(signature.toByteArray())
                }
                val HASH_CODE = String(Base64.encode(md.digest(), 0))

                Log.d("lys", "HASH_CODE -> $HASH_CODE")
            }
        } catch (e: Exception) {
            Log.d("lys", "Exception -> $e")
        }
    }
}