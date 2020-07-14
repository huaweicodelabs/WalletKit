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

import android.util.Log
import java.nio.charset.Charset

object EncodeUtil {
    val UTF_8: Charset = Charset.forName("UTF-8")
    fun byte2Hex(array: ByteArray?): String {
        return CommCryptUtil.byte2HexStr(array).toString()
    }

    fun hex2Byte(hex: String?): ByteArray? {
        try {
            return CommCryptUtil.hexStr2Byte(hex)
        } catch (var2: Exception) {
            Log.i("EncodeUtil", var2.message.toString())
        }
        return null
    }
}