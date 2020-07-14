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

import com.huawei.walletkit.util.CommonUtil.isNull

object Base64Hw {
    private const val BASELENGTH = 128
    private const val LOOKUPLENGTH = 64
    private const val TWENTYFOURBITGROUP = 24
    private const val EIGHTBIT = 8
    private const val SIXTEENBIT = 16
    private const val SIGN = -128
    private const val PAD = '='
    private val BASE64_ALPHABET = ByteArray(BASELENGTH)
    private val LOOK_UP_BASE64_ALPHABET = CharArray(LOOKUPLENGTH)

    /**
     * Encodes hex octects into Base64Hw
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64Hw array
     */
    fun encode(binaryData: ByteArray): String? {
        if (isNull(binaryData)) {
            return null
        }
        val lengthDataBits = binaryData.size * EIGHTBIT
        if (lengthDataBits == 0) {
            return ""
        }
        val fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP
        val numberTriplets = lengthDataBits / TWENTYFOURBITGROUP
        val numberQuartet = if (fewerThan24bits != 0) numberTriplets + 1 else numberTriplets
        val encodedData = CharArray(numberQuartet * 4)
        var k1: Byte
        var l1: Byte
        var b1: Byte
        var b2: Byte
        var b3: Byte
        var encodedIndex = 0
        var dataIndex = 0
        var val1: Byte
        var val2: Byte
        var val3: Byte
        for (i in 0 until numberTriplets) {
            b1 = binaryData[dataIndex++]
            b2 = binaryData[dataIndex++]
            b3 = binaryData[dataIndex++]
            l1 = (b2.toInt() and 0x0f).toByte()
            k1 = (b1.toInt() and 0x03).toByte()
            val1 =
                if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            val2 =
                if (b2.toInt() and SIGN == 0) (b2.toInt() shr 4).toByte() else (b2.toInt() shr 4 xor 0xf0).toByte()
            val3 =
                if (b3.toInt() and SIGN == 0) (b3.toInt() shr 6).toByte() else (b3.toInt() shr 6 xor 0xfc).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val1.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val2.toInt() or (k1.toInt() shl 4)]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[l1.toInt() shl 2 or val3.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[b3.toInt() and 0x3f]
        }

        // form integral number of 6-bit groups
        assembleInteger(binaryData, fewerThan24bits, encodedData, encodedIndex, dataIndex)
        return String(encodedData)
    }

    private fun assembleInteger(
        binaryData: ByteArray,
        fewerThan24bits: Int,
        encodedData: CharArray,
        encodedIndex: Int,
        dataIndex: Int
    ) {

        var encodedIndex = encodedIndex
        val b1: Byte
        val k1: Byte
        val b2: Byte
        val l1: Byte
        val val4: Byte
        val val5: Byte
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex]
            k1 = (b1.toInt() and 0x03).toByte()
            val4 =
                if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val4.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[k1.toInt() shl 4]
            encodedData[encodedIndex++] = PAD
            encodedData[encodedIndex++] = PAD
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex]
            b2 = binaryData[dataIndex + 1]
            l1 = (b2.toInt() and 0x0f).toByte()
            k1 = (b1.toInt() and 0x03).toByte()
            val4 =
                if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            val5 =
                if (b2.toInt() and SIGN == 0) (b2.toInt() shr 4).toByte() else (b2.toInt() shr 4 xor 0xf0).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val4.toInt()]
            encodedData[encodedIndex++] =
                LOOK_UP_BASE64_ALPHABET[val5.toInt() or (k1.toInt() shl 4)]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[l1.toInt() shl 2]
            encodedData[encodedIndex++] = PAD
        }
    }

    init {

        for (idx in 0 until BASELENGTH) {
            BASE64_ALPHABET[idx] = -1
        }
        run {
            var idx = 'Z'
            while (idx >= 'A') {
                BASE64_ALPHABET[idx.toInt()] = (idx - 'A'.toInt()).toByte()
                idx--
            }
        }
        run {
            var idx = 'z'
            while (idx >= 'a') {
                BASE64_ALPHABET[idx.toInt()] = (idx - 'a'.toInt() + 26).toByte()
                idx--
            }
        }
        var idx = '9'
        while (idx >= '0') {
            BASE64_ALPHABET[idx.toInt()] = (idx - '0'.toInt() + 52).toByte()
            idx--
        }
        BASE64_ALPHABET['+'.toInt()] = 62
        BASE64_ALPHABET['/'.toInt()] = 63
        for (i in 0..25) {
            LOOK_UP_BASE64_ALPHABET[i] = ('A'.toInt() + i).toChar()
        }
        run {
            var i = 26
            var j = 0
            while (i <= 51) {
                LOOK_UP_BASE64_ALPHABET[i] = ('a' + j)
                i++
                j++
            }
        }
        var i = 52
        var j = 0
        while (i <= 61) {
            LOOK_UP_BASE64_ALPHABET[i] = ('0' + j)
            i++
            j++
        }
        LOOK_UP_BASE64_ALPHABET[62] = '+'
        LOOK_UP_BASE64_ALPHABET[63] = '/'
    }
}