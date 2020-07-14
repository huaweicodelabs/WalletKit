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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveLoyaltyCard.setOnClickListener(this)
        saveGiftCard.setOnClickListener(this)
        saveCouponCard.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.saveLoyaltyCard -> startActivity(Intent(this, PassDataObjectActivity::class.java))
            R.id.saveGiftCard -> startActivity(Intent(this, GiftCardActivity::class.java))
            R.id.saveCouponCard -> startActivity(Intent(this, CouponCardActivity::class.java))
            else -> Log.d("msg", "nothing happened")
        }
    }
}
