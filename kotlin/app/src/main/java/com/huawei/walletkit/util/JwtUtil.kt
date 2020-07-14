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
import com.alibaba.fastjson.JSONObject
import com.huawei.walletkit.util.AESUtils.encryptAESCBC
import com.huawei.walletkit.util.RSA.encrypt
import com.huawei.walletkit.util.RSA.sign
import com.huawei.walletkit.util.RandomUtils.generateSecureRandomFactor
import io.jsonwebtoken.Jwts
import java.util.*

object JwtUtil {
    private const val PRIVATE_KEY_RUSSIA_DEBUG =
        "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"
    private const val SESSION_KEY_PUBLIC =
        "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAgBJB4usbO33Xg5vhJqfHJsMZj44f7rxpjRuPhGy37bUBjSLXN+dS6HpxnZwSVJCtmiydjl3Inq3Mzu4SCGxfb9RIjqRRfHA7ab5p3JnJVQfTEHMHy8XcABl6EPYIJMh26kztPOKU2Mkn6yhRaCurhVUD3n9bD8omiNrR4rg442AJlNamA7vgKs65AoqBuU4NBkGHg0VWWpEHCUx/xyX6hIwqc1aD7P2f62ZHsKpNZBOek/riWhaVx3dTAa9ZS+Av3IGLOZiplhYIow9f8dlWyqs8nff9FZoJO03QhXLvOORT+lPAkW6gFzaoeMaGb40HakkZn3uvlAEKrKrtR0rZEok+N1hnboaAu8oaKK0rF1W6iNrXcFrO0rcrCsFTVF8qCa/1dFmIXwUd2M6cUzT9W0YkNyb6ZBbwEhjwBL4DNW4JfeF2Dzj0eZYlSuYV7e7e1e+XEO8lwPLAiy4bEFAWCaeuDVIhbIoBaU6xHNVQoyzct98gaOYxE4mVDqAUVmhfAgMBAAE="
    private const val RAS_MODEL = "RSA/NONE/OAEPwithSHA-256andMGF1Padding"

    @Throws(Exception::class)
    fun generateJwt(issuerId: String?, passObject: String?): String {
        val privateKey = PRIVATE_KEY_RUSSIA_DEBUG
        val sessionKeyPublicKey = SESSION_KEY_PUBLIC
        val sessionKey = generateSecureRandomFactor(16)
        val jsonObject = JSONObject.parseObject(passObject)
        jsonObject["jti"] = "jwt"
        jsonObject["iss"] = issuerId
        val sessionKeyBytes = EncodeUtil.hex2Byte(sessionKey)
        val payLoadEncrypt =
            encryptAESCBC(jsonObject.toJSONString().toByteArray(EncodeUtil.UTF_8), sessionKeyBytes)
        val headerMap: MutableMap<String, Any?> = HashMap()
        val sessionKeyPlaintext = encrypt(sessionKey.toByteArray(), sessionKeyPublicKey, RAS_MODEL)
        headerMap["sessionKey"] = sessionKeyPlaintext
        val builder = Jwts.builder().setHeader(headerMap).setPayload(payLoadEncrypt)
        val userToken = builder.compact()
        val content = userToken.substring(0, userToken.length - 1)
        val sign = sign(content, privateKey)
        Log.i("JwtUtil", "JWT:$userToken$sign")
        return userToken + sign
    }
}