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
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.wallet.constant.WalletPassConstant
import com.huawei.hms.wallet.pass.*
import com.huawei.walletkit.util.BasisTimesUtils
import com.huawei.walletkit.util.BasisTimesUtils.END_TIME
import com.huawei.walletkit.util.BasisTimesUtils.START_TIME
import com.huawei.walletkit.util.BasisTimesUtils.TIME_FORMATE
import com.huawei.walletkit.util.BasisTimesUtils.getLongTimeOfYMD
import com.huawei.walletkit.util.BasisTimesUtils.showDatePickerDialog
import com.huawei.walletkit.util.CommonUtil.ISSUREID
import com.huawei.walletkit.util.CommonUtil.PASSOBJECT
import com.huawei.walletkit.util.CommonUtil.showToast
import kotlinx.android.synthetic.main.coupon_card_info.*
import java.text.SimpleDateFormat
import java.util.*

class CouponCardActivity : AppCompatActivity(), View.OnClickListener {
    private var statuslist = arrayOf("ACTIVE", "COMPLETED", "EXPIRED", "INACTIVE")
    var indext = 0
    private var startTime = ""
    private var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coupon_card_info)

        couponStartTime.setOnClickListener(this)
        couponEndTime.setOnClickListener(this)

        val statuslistadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuslist)
        statuslistadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCoupon.adapter = statuslistadapter
        spinnerCoupon.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, arg2: Int, arg3: Long) {
                indext = arg2
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        saveCouponData.setOnClickListener(View.OnClickListener {

            //serinumber
            val serinumber = serinumberCoupon.text.toString()
            if (TextUtils.isEmpty(serinumber)) {
                showToast(this, resources.getString(R.string.serialnumber))
                return@OnClickListener
            }

            //oraginaztionId cardNumberCoupon
            val organizationId = cardNumberCoupon.text.toString()
            if (TextUtils.isEmpty(organizationId)) {
                showToast(this, resources.getString(R.string.cardnumber))
                return@OnClickListener
            }

            //styleId
            val styleId = passStyleIdentifierCoupon.text.toString()
            if (TextUtils.isEmpty(styleId)) {
                showToast(this, resources.getString(R.string.templateid))
                return@OnClickListener
            }

            //Pass Type
            val typeId = typeIdentifierCoupon.text.toString()
            if (TextUtils.isEmpty(typeId)) {
                showToast(this, resources.getString(R.string.passtype))
                return@OnClickListener
            }

            //issuerId
            val issuerId = issuerIdCoupon.text.toString()
            if (TextUtils.isEmpty(issuerId)) {
                showToast(this, resources.getString(R.string.issuerid))
                return@OnClickListener
            }

            //passMerchantName
            val merchantNameCoupon = findViewById<TextView>(R.id.merchantNameCoupon)
            val merchantName = merchantNameCoupon.text.toString()
            if (TextUtils.isEmpty(merchantName)) {
                showToast(this, resources.getString(R.string.merchantname))
                return@OnClickListener
            }

            // Coupontitle
            val name = nameCoupon.text.toString()
            if (TextUtils.isEmpty(name)) {
                showToast(this, resources.getString(R.string.coupontitle))
                return@OnClickListener
            }

            //state
            var state = WalletPassConstant.PASS_STATE_ACTIVE
            when (statuslist.get(indext)) {
                "ACTIVE" -> state = WalletPassConstant.PASS_STATE_ACTIVE
                "COMPLETED" -> state = WalletPassConstant.PASS_STATE_COMPLETED
                "EXPIRED" -> state = WalletPassConstant.PASS_STATE_EXPIRED
                "INACTIVE" -> state = WalletPassConstant.PASS_STATE_INACTIVE
            }

            //time check
            val date = Date()
            val start = if (TextUtils.isEmpty(startTime)) {
                showToast(this, resources.getString(R.string.startTime))
                return@OnClickListener
            } else getLongTimeOfYMD(startTime)

            val end = if (TextUtils.isEmpty(endTime)) {
                showToast(this, resources.getString(R.string.endTime))
                return@OnClickListener
            } else getLongTimeOfYMD(endTime)

            if (end <= start || end <= date.time) {
                showToast(this, resources.getString(R.string.timedifference))
                return@OnClickListener
            }

            //provider name
            val providesCoupon = merchantProvidesCoupon.text.toString()
            if (TextUtils.isEmpty(providesCoupon)) {
                showToast(this, resources.getString(R.string.providesname))
                return@OnClickListener
            }

            //ImageUris
            val imageUris = imageModuleDataMainImageUrisCoupon.text.toString()
            val imageDes = imageModuleDataMainImageUrisDesCoupon.text.toString()
            val imageUris1 = imageModuleDataMainImageUrisCoupon1.text.toString()
            val imageDes1 = imageModuleDataMainImageUrisDesCoupon1.text.toString()

            //message
            val mgeHeader = messageHeaderCoupon.text.toString()
            val mgeBody = messageBodyCoupon.text.toString()
            val mgeHeader1 = messageHeaderCoupon1.text.toString()
            val mgeBody1 = messageBodyCoupon1.text.toString()
            val passBuilder = PassObject.getBuilder()
            //commonFields
            val commonField = ArrayList<CommonField>()
            //appendFields
            val appendFields = ArrayList<AppendField>()

            //Background color of the outer frame
            val backgroundColorCommonFild = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_BACKGROUND_COLOR)
                .setLabel(resources.getString(R.string.backgroundColorLable))
                .setValue(backgroundColorCoupon.text.toString())
                .build()
            commonField.add(backgroundColorCommonFild)

            //Logo on the coupon
            val logoCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_LOGO)
                .setLabel(resources.getString(R.string.logolabel))
                .setValue(logoCoupon.text.toString())
                .build()
            commonField.add(logoCommonField)

            //Merchant name
            val merchantNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_MERCHANT_NAME)
                .setLabel(resources.getString(R.string.merchantNamelabel))
                .setValue(merchantName)
                .build()
            commonField.add(merchantNameCommonField)

            //Coupon title
            val couponTitleCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_NAME)
                .setLabel(resources.getString(R.string.coupontitlelabel))
                .setValue(name)
                .build()
            commonField.add(couponTitleCommonField)

            //SimpleDateFormat
            val format = SimpleDateFormat(TIME_FORMATE, Locale.ENGLISH)
            passBuilder.setStatus(PassStatus.getBuilder()
                    .setState(state)
                    .setEffectTime(format.format(Date(start)))
                    .setExpireTime(format.format(Date(end))).build())

            //Merchant that provides the coupon
            val merchantProvidesCommonField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_PROVIDER_NAME)
                .setLabel(resources.getString(R.string.merchantProvideslabel))
                .setValue(providesCoupon)
                .build()
            appendFields.add(merchantProvidesCommonField)

            //Remarks
            val barCode = BarCode.getBuilder()
                .setType(BarCode.BARCODE_TYPE_QR_CODE)
                .setValue(barcodeValueCoupon.text.toString())
                .settext(barcodeTextCoupon.text.toString())
                .build()
            passBuilder.setBarCode(barCode)

            //Details
            val detailsppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_DETAILS)
                .setLabel(resources.getString(R.string.detailsppendFieldlabel))
                .setValue(detailsCoupon.text.toString())
                .build()
            appendFields.add(detailsppendField)

            //Scrolling images
            val imageList = ArrayList<AppendField>()
            imageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(imageDes)
                    .setValue(imageUris)
                    .build())
            imageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(imageDes1)
                    .setValue(imageUris1)
                    .build())
            passBuilder.addImageList(imageList)

            //Disclaimer
            val disclaimerAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_DISCLAIMER)
                .setLabel(resources.getString(R.string.disclaimerlabel))
                .setValue(disclaimerCoupon.text.toString())
                .build()
            appendFields.add(disclaimerAppendField)

            //Message
            val messageList = ArrayList<AppendField>()
            messageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(mgeHeader)
                    .setValue(mgeBody)
                    .build())
            messageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(mgeHeader1)
                    .setValue(mgeBody1)
                    .build())

            passBuilder.addMessageList(messageList)
            .setOrganizationPassId(organizationId)
            .setPassStyleIdentifier(styleId)
            .setPassTypeIdentifier(typeId)
            .setSerialNumber(serinumber)
            .addAppendFields(appendFields)
            .addCommonFields(commonField)
            val passObject = passBuilder.build()
            Log.d("passObject", "" + passObject.toJson())

            val intent = Intent(this, PassTestActivity::class.java).apply {
                putExtra(PASSOBJECT, passObject.toJson())
                putExtra(ISSUREID, issuerId)
            }
            startActivity(intent)
        })
    }

    /*button clicks*/
    override fun onClick(v: View) {
        when (v.id) {
            R.id.couponStartTime -> showDatePickerDialog(this, START_TIME, dateClick)
            R.id.couponEndTime -> showDatePickerDialog(this, END_TIME, dateClick)
            else -> Log.d("msg", "nothing happened")
        }
    }

    /*assign date*/
    private var dateClick: BasisTimesUtils.OnDatePickerListener =
        object : BasisTimesUtils.OnDatePickerListener {

            override fun onConfirm(date: String, dateType: String) {
                if (dateType == START_TIME) startTime = date else endTime = date
            }
        }
}