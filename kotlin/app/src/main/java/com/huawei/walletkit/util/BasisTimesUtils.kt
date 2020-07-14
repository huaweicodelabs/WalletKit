/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.huawei.walletkit.util

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object BasisTimesUtils {

    const val START_TIME = "START_TIME"
    const val END_TIME = "END_TIME"
    const val UPDATE_TIME = "UPDATE_TIME"
    const val TIME_FORMATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    fun getLongTimeOfYMD(time: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = sdf.parse(time)
        return date.time
    }

    fun showDatePickerDialog(
        context: Context,
        dateType: String,
        onDateTimePickerListener: OnDatePickerListener?
    ): String {
        val currentTime = Calendar.getInstance()
        val currentyear = currentTime.get(Calendar.YEAR)
        val currentmonth = currentTime.get(Calendar.MONTH)
        val currentday = currentTime.get(Calendar.DAY_OF_MONTH)
        var date: String? = ""
        val datePicker = DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                date = String.format("%d-%d-%d", year, month + 1, dayOfMonth)
                onDateTimePickerListener?.onConfirm(date.toString(), dateType)
                Log.d("date", "" + date)
            }, currentyear, currentmonth, currentday
        )
        datePicker.show()
        return date.toString()
    }

    interface OnDatePickerListener {
        fun onConfirm(date: String, dateType: String)
    }
}