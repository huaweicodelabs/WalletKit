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

import android.content.Context
import android.widget.Toast

object CommonUtil {

    const val PASSOBJECT = "PASSOBJECT"
    const val ISSUREID = "ISSUREID"
    const val SAVE_TO_ANDROID = 888

    /**
     * Check if an object is null.
     *
     * @param object the object to be checked.
     * @return if the object is null.
     */
    @JvmStatic
    fun isNull(`object`: Any?): Boolean {
        return `object` == null
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show()
    }
}