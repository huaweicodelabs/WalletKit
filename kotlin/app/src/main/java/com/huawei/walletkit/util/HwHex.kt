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

import java.nio.charset.Charset

object HwHex {
    var CHARSET: Charset? = null
    private val LOWER_CASE: CharArray
    private val UPPER_CASE: CharArray

    fun encodeHex(input: ByteArray, isLower: Boolean): CharArray {
        return encodeHex(input, if (isLower) LOWER_CASE else UPPER_CASE)
    }

    private fun encodeHex(input: ByteArray, caseStyle: CharArray): CharArray {
        val l = input.size
        val result = CharArray(l shl 1)
        var i = 0
        var index = 0
        while (i < l) {
            result[index++] = caseStyle[240 and input[i].toInt() ushr 4]
            result[index++] = caseStyle[15 and input[i].toInt()]
            ++i
        }
        return result
    }


    @Throws(Exception::class)
    fun decodeHex(inputChars: CharArray): ByteArray {
        val len = inputChars.size
        var i = 0
        return if (len and 1 != 0) {
            throw Exception("decode Hex Error")
        } else {
            val out = ByteArray(len shr 1)
            var j = 0
            while (j < len) {
                var f = toDigit(inputChars[j]) shl 4
                ++j
                f = f or toDigit(inputChars[j])
                ++j
                out[i] = (f and 255).toByte()
                ++i
            }
            out
        }
    }

    @Throws(Exception::class)
    internal fun toDigit(ch: Char): Int {
        val digit = Character.digit(ch, 16)
        return if (digit == -1) {
            throw Exception("Illegal character")
        } else {
            digit
        }
    }

    init {
        CHARSET = Charset.forName("UTF-8")
        LOWER_CASE = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        UPPER_CASE = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    }
}