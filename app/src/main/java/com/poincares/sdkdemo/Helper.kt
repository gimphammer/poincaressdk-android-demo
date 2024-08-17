/**
 * @file Helper.kt
 * @author wuliang (wuliang@poincares.com)
 * @brief
 * @version 0.1
 * @date 2024-07-26
 *
 * @copyright Copyright (c) 2024 @poincares.com
 *
 */
package com.poincares.sdkdemo

import android.content.Context
import androidx.appcompat.app.AlertDialog


class Helper {

    companion object {
        fun showDialog(context: Context, title: String, message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)

            // 设置正面按钮
            builder.setPositiveButton("OK") { dialog, which ->
                // 处理正面按钮点击事件
                // 例如，关闭对话框
                dialog.dismiss()
            }

            // 设置负面按钮
//            builder.setNegativeButton("Cancel") { dialog, which ->
//                // 处理负面按钮点击事件
//                // 例如，关闭对话框
//                dialog.dismiss()
//            }

            // 创建并显示对话框
            val dialog = builder.create()
            dialog.show()
        }

    }

}