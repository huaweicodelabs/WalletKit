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
package com.huawei.walletkit

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.huawei.hms.wallet.CreateWalletPassRequest
import com.huawei.hms.wallet.ResolveTaskHelper
import com.huawei.hms.wallet.Wallet
import com.huawei.hms.wallet.constant.WalletPassConstant
import com.huawei.walletkit.util.CommonUtil.ISSUREID
import com.huawei.walletkit.util.CommonUtil.PASSOBJECT
import com.huawei.walletkit.util.CommonUtil.SAVE_TO_ANDROID
import com.huawei.walletkit.util.JwtUtil.generateJwt
import kotlinx.android.synthetic.main.sec_main.*

class PassTestActivity : FragmentActivity(), View.OnClickListener {
    private var issuerId: String? = ""
    private var passObject: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sec_main)

        savetowallet.setOnClickListener(this)
        addpass.setOnClickListener(this)

        passObject = intent.getStringExtra(PASSOBJECT)
        issuerId = intent.getStringExtra(ISSUREID)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.savetowallet -> saveToHuaWeiWallet()
            R.id.addpass -> addCard()
            else -> Log.d("msg", "nothing happened")
        }
    }

    //add by wallet kit sdk
    private fun saveToHuaWeiWallet() {
        val jwtStr = getJwtFromAppServer(passObject)
        val request = CreateWalletPassRequest.getBuilder()
            .setJwt(jwtStr)
            .build()
        val walletObjectsClient = Wallet.getWalletPassClient(this)
        val task = walletObjectsClient.createWalletPass(request)
        ResolveTaskHelper.excuteTask(task, this, SAVE_TO_ANDROID)
    }

    private fun addCard() {
        val jwtStr = getJwtFromAppServer(passObject)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("hms://www.huawei.com/payapp/{$jwtStr}")
        try {
            startActivityForResult(intent, SAVE_TO_ANDROID)
        } catch (e: ActivityNotFoundException) {
            Log.d("msg", "HMS error:ActivityNotFoundException")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SAVE_TO_ANDROID && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "save success", Toast.LENGTH_LONG).show()
        } else {
            val errorCode = data?.getIntExtra(WalletPassConstant.EXTRA_ERROR_CODE, -1)
            Toast.makeText(this, "fail, [" + errorCode + "]：" + analyzeErrorCode(errorCode), Toast.LENGTH_LONG).show()
        }
    }

    private fun analyzeErrorCode(errorCode: Int?): String {
        val tips = when (errorCode) {
            WalletPassConstant.ERROR_CODE_SERVICE_UNAVAILABLE -> "server unavailable（net error）"
            WalletPassConstant.ERROR_CODE_INTERNAL_ERROR -> "internal error"
            WalletPassConstant.ERROR_CODE_INVALID_PARAMETERS -> "invalid parameters or card is added"
            WalletPassConstant.ERROR_CODE_MERCHANT_ACCOUNT_ERROR -> "JWT verify fail"
            WalletPassConstant.ERROR_CODE_USER_ACCOUNT_ERROR -> "hms account error（invalidity or Authentication failed）"
            WalletPassConstant.ERROR_CODE_UNSUPPORTED_API_REQUEST -> "unSupport API"
            WalletPassConstant.ERROR_CODE_OTHERS -> "unknown Error"
            else -> "unknown Error"
        }
        return tips
    }

    /**
     * in this demo,method getJwtFromAppServer just simulate how to get jwt form passObject
     * in product environment,
     * issuerId, privateKye and SessionPublicKey is saved on the developer's Server
     * developer should send passObject to developer's Server(pls use Https)
     * Server should generateJwt by passObject ,and send back jwt to app
     *
     * @param passObject passObject
     * @return JWT
     */
    private fun getJwtFromAppServer(passObject: String?): String? {
        val jwtStr = try {
            generateJwt(issuerId, passObject)
        } catch (e: Exception) {
            Toast.makeText(this, "fail ：jwt trans error", Toast.LENGTH_LONG).show()
            return null
        }
        Log.i("jwtStr", "" + jwtStr)
        return jwtStr
    }


}