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

import android.util.Base64
import android.util.Log
import com.huawei.walletkit.util.Base64Hw.encode
import java.security.Key
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object RSA {
    private const val SIGN_ALGORITHMS256 = "SHA256WithRSA"

    /* Sign content */
    fun sign(content: String, privateKey: String?): String {
        val charset = "utf-8"
        try {
            val privatePKCS8 = PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT))
            val keyf = KeyFactory.getInstance("RSA")
            val priKey = keyf.generatePrivate(privatePKCS8)
            val signatureObj = Signature.getInstance(SIGN_ALGORITHMS256)
            signatureObj.initSign(priKey)
            signatureObj.update(content.toByteArray(charset(charset)))
            val signed = signatureObj.sign()
            return Base64.encodeToString(signed, Base64.DEFAULT)
        } catch (ex: Exception) {
            Log.i("RSA", "SIGN Fail")
        }
        return ""
    }

    /*encrypt bytes */
    @Throws(Exception::class)
    fun encrypt(bytes: ByteArray?, publicKey: String, algorithm: String?): String? {
        val key: Key = getPublicKey(publicKey)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val b1 = cipher.doFinal(bytes)
        return encode(b1)
    }

    /*getPublicKey*/
    @Throws(Exception::class)
    private fun getPublicKey(key: String): PublicKey {
        val keySpec = X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT))
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
}