package com.yun.mysimpletalk.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.common.constants.FirebaseConstants
import com.yun.mysimpletalk.databinding.ActivityMainBinding
import com.yun.mysimpletalk.util.AuthUtil.snsSdkSetting
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityMainBinding
    lateinit var infoListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this@MainActivity
        binding.main = mainViewModel

        binding.bottomNavView.run {
            setOnItemSelectedListener { menuItem ->
                Log.d("lys", "menuItem.title > ${menuItem.title}")
                if (mainViewModel.userInfo.value == null) return@setOnItemSelectedListener true
                when (menuItem.title) {
                    "홈" -> navController.navigate(R.id.action_global_homeFragment)
                    "채팅" -> navController.navigate(R.id.action_global_chatFragment)
                    "일정" -> {}
                    "설정" -> navController.navigate(R.id.action_global_settingFragment)
                }
                true
            }
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        snsSdkSetting(this)

        mainViewModel.myId.observe(this) { id ->
            if (id != null) observeMyProfile(id)
        }
    }

    /**
     * 나의 정보 구독
     */
    private fun observeMyProfile(id: String) {
        infoListener =
            FirebaseFirestore.getInstance().collection(FirebaseConstants.Path.USERS)
                .document(id)
                .addSnapshotListener { value, error ->
                    if (error != null) return@addSnapshotListener
                    if (value != null && value.exists()) {
                        mainViewModel.setUserInfo(value.data)
                        Log.d("lys", "value > ${value.data}")
                    }
                }
    }

    /**
     * 나의 정보 구독 해제
     */
    fun listenerRemove(){
        infoListener.remove()
    }

    override fun onDestroy() {
        listenerRemove()
        super.onDestroy()
    }
}