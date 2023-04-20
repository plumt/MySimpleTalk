package com.yun.mysimpletalk.ui.wait

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.base.BaseFragment
import com.yun.mysimpletalk.base.BaseRecyclerAdapter
import com.yun.mysimpletalk.data.model.UserModel
import com.yun.mysimpletalk.databinding.FragmentWaitBinding
import com.yun.mysimpletalk.databinding.ItemWaitBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitFragment : BaseFragment<FragmentWaitBinding, WaitViewModel>() {
    override val viewModel: WaitViewModel by viewModels()
    override fun getResourceId(): Int = R.layout.fragment_wait
    override fun setVariable(): Int = BR.wait
    override fun isOnBackEvent(): Boolean = true
    override fun onBackEvent() {
        findNavController().popBackStack()
        sharedViewModel.showBottomNav()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkWaitCount(sharedViewModel.userInfo.value!!)

        binding.let { v->
            v.rvWait.apply {
                adapter = object : BaseRecyclerAdapter.Create<UserModel.User, ItemWaitBinding>(
                    R.layout.item_wait,
                    BR.itemWait,
                    BR.waitListener
                ) {
                    override fun onItemClick(item: UserModel.User, view: View) {
                        when(view.tag){
                            "block" -> {
                                Toast.makeText(requireContext(),"차단",Toast.LENGTH_SHORT).show()
                            }
                            "accept" -> {
                                Toast.makeText(requireContext(),"추가",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onItemLongClick(item: UserModel.User, view: View): Boolean = true
                }
            }
        }
    }
}