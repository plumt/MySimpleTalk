package com.yun.mysimpletalk.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.databinding.ActivityMainBinding
import com.yun.mysimpletalk.util.AuthUtil.snsSdkSetting
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.run {
            lifecycleOwner = this@MainActivity
            main = mainViewModel
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        snsSdkSetting(this)
    }
}