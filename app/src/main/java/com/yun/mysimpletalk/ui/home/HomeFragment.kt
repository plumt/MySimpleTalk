package com.yun.mysimpletalk.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_home
    override fun isOnBackEvent(): Boolean = true
    override fun setVariable(): Int = BR.home
    override fun onBackEvent() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.let {  sv ->
            sv.userInfo.observe(viewLifecycleOwner){
                Log.d("lys","userInfo > $it")
            }

            sv.showBottomNav()
        }

    }
}