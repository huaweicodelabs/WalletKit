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

import com.huawei.walletkit.util.HwHex.decodeHex
import com.huawei.walletkit.util.HwHex.encodeHex
import java.security.SecureRandom

object CommCryptUtil {
    fun byte2HexStr(array: ByteArray?): String? {
        return if (array == null) null else String(encodeHex(array, false))
    }

    fun hexStr2Byte(hexStr: String?): ByteArray {
        return if (hexStr == null) ByteArray(0) else decodeHex(hexStr.toCharArray())
    }

    fun genSecureRandomByte(byteSize: Int): ByteArray {
        val sr = SecureRandom()
        val bytes = ByteArray(byteSize)
        sr.nextBytes(bytes)
        return bytes
    }
}