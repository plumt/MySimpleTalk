package com.yun.mysimpletalk.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import com.yun.mysimpletalk.R
import com.yun.mysimpletalk.BR
import com.yun.mysimpletalk.databinding.DialogEdittextBinding

class EdittextDialog {
    lateinit var customDialogListener: CustomDialogListener

    interface CustomDialogListener {
        fun onResult(result: String)
    }

    fun setDialogListener(customDialogListener: CustomDialogListener) {
        this.customDialogListener = customDialogListener
    }

    lateinit var dialog: AlertDialog

    fun showDialog(context: Context, title: String, hint: String) {
        AlertDialog.Builder(context).run {
            setCancelable(true)
            val view = View.inflate(context, R.layout.dialog_edittext, null)
            val binding = DialogEdittextBinding.bind(view)
            binding.apply {
                setVariable(BR.title, title)
                setVariable(BR.hint, hint)
            }
            setView(binding.root)
            dialog = create()
            dialog.apply {
                window?.requestFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnDismissListener {
                    dialog.dismiss()
                }
            }

            binding.let { v ->

                v.btnResult.setOnClickListener {
                    if (v.etContents.text.trim().isNotEmpty()) {
                        customDialogListener.onResult(v.etContents.text.toString().trim())
                    }
                }

                v.etContents.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                        // 이전 텍스트 변경 이벤트 처리
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                        // 텍스트 변경 이벤트 처리
                    }

                    override fun afterTextChanged(s: Editable?) {
                        v.btnResult.isEnabled = s.toString().trim().isNotEmpty()
                        v.btnResult.setBackgroundColor(
                            getColor(
                                context, if (s.toString().trim().isNotEmpty()) R.color.color_FBA53B else R.color.color_CCCCCC
                            )
                        )
                    }
                })
            }
            dialog
        }.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}