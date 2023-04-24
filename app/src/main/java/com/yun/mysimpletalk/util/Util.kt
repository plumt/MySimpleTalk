package com.yun.mysimpletalk.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.yun.mysimpletalk.R
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

    @BindingAdapter("setImages")
    @JvmStatic
    fun ImageView.setImages(path: String?) {

        Glide.with(this)
            .load(path)
            .override(SIZE_ORIGINAL)
//            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(this)

//        if (!path.isNullOrEmpty()) {
//            Glide.with(this)
//                .load(path)
//                .override(SIZE_ORIGINAL)
//                .into(this)
//        } else {
//            Glide.with(this)
//                .load(ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground))
//                .into(this)
//        }
    }
}