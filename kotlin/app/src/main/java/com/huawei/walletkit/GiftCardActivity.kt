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
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.wallet.constant.WalletPassConstant
import com.huawei.hms.wallet.pass.*
import com.huawei.walletkit.util.BasisTimesUtils
import com.huawei.walletkit.util.BasisTimesUtils.END_TIME
import com.huawei.walletkit.util.BasisTimesUtils.START_TIME
import com.huawei.walletkit.util.BasisTimesUtils.TIME_FORMATE
import com.huawei.walletkit.util.BasisTimesUtils.UPDATE_TIME
import com.huawei.walletkit.util.BasisTimesUtils.getLongTimeOfYMD
import com.huawei.walletkit.util.CommonUtil.ISSUREID
import com.huawei.walletkit.util.CommonUtil.PASSOBJECT
import com.huawei.walletkit.util.CommonUtil.showToast
import kotlinx.android.synthetic.main.gift_card_info.*
import java.text.SimpleDateFormat
import java.util.*

class GiftCardActivity : AppCompatActivity(), View.OnClickListener {
    private var index = 0
    var barCodeStyleIndex = 0
    private var updateTime = ""
    private var startTime = ""
    private var endTime = ""
    private var statuslist = arrayOf("ACTIVE", "COMPLETED", "EXPIRED", "INACTIVE")
    private var barCodeStyleList = arrayOf("codabar", "qrCode")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gift_card_info)

        val statuslistadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuslist)
        statuslistadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGift.adapter = statuslistadapter
        spinnerGift.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, arg2: Int, arg3: Long) {
                index = arg2
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        val barCodeStyleAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, barCodeStyleList)
        barCodeStyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnergitfBarcodeStyle.adapter = barCodeStyleAdapter
        spinnergitfBarcodeStyle.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, arg2: Int, arg3: Long) {
                barCodeStyleIndex = arg2
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        giftStartTime.setOnClickListener(this)
        giftEndTime.setOnClickListener(this)
        passBalanceUpdateTime.setOnClickListener(this)

        saveGiftData.setOnClickListener(View.OnClickListener {

            val styleId = passStyleIdentifierGift.text.toString()
            if (TextUtils.isEmpty(styleId)) {
                showToast(this,  resources.getString(R.string.templateid))
                return@OnClickListener
            }

            //Construct gift card data.
            val passBuilder = PassObject.getBuilder()
            //commonFields
            val commonField = ArrayList<CommonField>()
            //appendFields
            val appendFields = ArrayList<AppendField>()

            //Background image of the card
            val backgroundImageCommonFild = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BACKGROUND_IMG)
                .setLabel(giftBackGroundImageDesc.text.toString())
                .setValue(giftBackGroundImage.text.toString())
                .build()
            commonField.add(backgroundImageCommonFild)

            //Logo
            val logoCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_LOGO)
                .setLabel(resources.getString(R.string.logolabel))
                .setValue(giftCardLogo.text.toString())
                .build()
            commonField.add(logoCommonField)

            //Merchant name
            val merchantNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_MERCHANT_NAME)
                .setLabel(resources.getString(R.string.merchantNamelabel))
                .setValue(giftMerchantName.text.toString())
                .build()
            commonField.add(merchantNameCommonField)

            //Card name
            val cardNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_NAME)
                .setLabel(resources.getString(R.string.cardNamelabel))
                .setValue(giftCardname.text.toString())
                .build()
            commonField.add(cardNameCommonField)

            //Card number
            if (TextUtils.isEmpty(giftCardNumber.text.toString())) {
                showToast(this,  resources.getString(R.string.cardnumber))
                return@OnClickListener
            }
            val cardNumberCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_CARD_NUMBER)
                .setLabel(resources.getString(R.string.cardNumberCommonField))
                .setValue(giftCardNumber.text.toString())
                .build()
            commonField.add(cardNumberCommonField)

            //Balance
            if (TextUtils.isEmpty(giftBalance.text.toString())) {
                showToast(this,  resources.getString(R.string.startTime))
                return@OnClickListener
            }
            val balanceCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BALANCE)
                .setLabel(resources.getString(R.string.balanceCommonField))
                .setValue(giftBalance.text.toString())
                .build()
            commonField.add(balanceCommonField)

            //Barcode or QR code 8*Remarks
            var barType = BarCode.BARCODE_TYPE_QR_CODE
            when (barCodeStyleList.get(barCodeStyleIndex)) {
                "codabar" -> barType = BarCode.BARCODE_TYPE_QR_CODE
                "qrCode" -> barType = BarCode.BARCODE_TYPE_CODABAR
            }
            val barCode = BarCode.getBuilder()
                .setType(barType)
                .setValue(passBarcodeValue.text.toString())
                .settext(passBarcodeAlternateText.text.toString())
                .build()
            passBuilder.setBarCode(barCode)

            //Balance update time
            val update = if (TextUtils.isEmpty(updateTime)) {
                showToast(this,  resources.getString(R.string.updateTime))
                return@OnClickListener
            } else {
                getLongTimeOfYMD(updateTime)
            }

            // SimpleDateFormat
            val format = SimpleDateFormat(TIME_FORMATE, Locale.ENGLISH)
            // create SimpleDateFormat
            val updateTimeValue = format.format(Date(update))
            val banlanceUpdateTimeAppendField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BALANCE_REFRESH_TIME)
                .setLabel("Updated")
                .setValue(updateTimeValue)
                .build()
            commonField.add(banlanceUpdateTimeAppendField)

            //PIN
            val pinCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BLANCE_PIN)
                .setLabel("PIN Number")
                .setValue(giftCardPin.text.toString())
                .build()
            commonField.add(pinCommonField)

            //Event number
            val eventNumber = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_EVENT_NUMBER)
                .setLabel("Event Number")
                .setValue(giftEventNumber.text.toString())
                .build()
            appendFields.add(eventNumber)

            //message
            val messageList = ArrayList<AppendField>()
            messageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(messageHeaderGift.text.toString())
                    .setValue(messageBodyGift.text.toString())
                    .build())

            messageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(messageHeaderGift1.text.toString())
                    .setValue(messageBodyGift1.text.toString())
                    .build())

            passBuilder.addMessageList(messageList)

            //Scrolling images
            val imageList = ArrayList<AppendField>()
            imageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(giftScrollingDesc1.text.toString())
                    .setValue(giftScrollingImages1.text.toString())
                    .build())

            imageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(giftScrollingDesc2.text.toString())
                    .setValue(giftScrollingImages2.text.toString())
                    .build())

            passBuilder.addImageList(imageList)

            // Nearby stores
            val nearbyAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_NEARBY_LOCATIONS)
                .setLabel(giftNearbyStoresName.text.toString())
                .setValue(giftNearbyStoresUrl.text.toString())
                .build()
            appendFields.add(nearbyAppendField)

            //Main page
            val mainPageAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_MAINPAGE)
                .setLabel(giftMainPageName.text.toString())
                .setValue(giftMainPageUrl.text.toString())
                .build()
            appendFields.add(mainPageAppendField)
            //Hotline
            val hotlineAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_HOTLINE)
                .setLabel(giftHotlineName.text.toString())
                .setValue(giftHotlinePone.text.toString())
                .build()

            //time check
            val date = Date()
            val start = if (TextUtils.isEmpty(startTime)) {
                showToast(this,  resources.getString(R.string.startTime))
                return@OnClickListener
            } else getLongTimeOfYMD(startTime)

            val end = if (TextUtils.isEmpty(endTime)) {
                showToast(this,  resources.getString(R.string.endTime))
                return@OnClickListener
            } else getLongTimeOfYMD(endTime)

            if (end <= start || end <= date.time) {
                showToast(this,  resources.getString(R.string.timedifference))
                return@OnClickListener
            }
            //serinumber
            val organizationPassId = organizationPassIdGift.text.toString()
            if (TextUtils.isEmpty(organizationPassId)) {
                showToast(this,  resources.getString(R.string.serialnumber))
                return@OnClickListener
            }
            val typeId = passTypeIdentifier.text.toString()
            if (TextUtils.isEmpty(typeId)) {
                showToast(this,  resources.getString(R.string.passtype))
                return@OnClickListener
            }
            val issuerId = issuerIdGift.text.toString()
            if (TextUtils.isEmpty(issuerId)) {
                showToast(this,  resources.getString(R.string.issuerid))
                return@OnClickListener
            }

            //state
            var state = WalletPassConstant.PASS_STATE_ACTIVE
            when (statuslist.get(index)) {
                "ACTIVE" -> state = WalletPassConstant.PASS_STATE_ACTIVE
                "COMPLETED" -> state = WalletPassConstant.PASS_STATE_COMPLETED
                "EXPIRED" -> state = WalletPassConstant.PASS_STATE_EXPIRED
                "INACTIVE" -> state = WalletPassConstant.PASS_STATE_INACTIVE
            }
            appendFields.add(hotlineAppendField)
            //PassStyleIdentifier
            passBuilder.setOrganizationPassId(giftCardNumber.text.toString())
            .setPassStyleIdentifier(passStyleIdentifierGift.text.toString())
            .setPassTypeIdentifier(typeId)
            .setSerialNumber(organizationPassId)
            .setStatus(PassStatus.getBuilder().setState(state).setEffectTime(format.format(Date(start))).setExpireTime(format.format(Date(end))).build())
            .addAppendFields(appendFields)
            .addCommonFields(commonField)
            val passObject = passBuilder.build()
            Log.d("passObject",  ""+passObject.toJson())

            val intent = Intent(this, PassTestActivity::class.java).apply {
                putExtra(PASSOBJECT, passObject.toJson())
                putExtra(ISSUREID, issuerId)
            }
            startActivity(intent)
        })

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.giftStartTime -> BasisTimesUtils.showDatePickerDialog(this, START_TIME, dateClick)
            R.id.giftEndTime -> BasisTimesUtils.showDatePickerDialog(this, END_TIME, dateClick)
            R.id.passBalanceUpdateTime -> BasisTimesUtils.showDatePickerDialog(this, UPDATE_TIME, dateClick)
            else -> Log.d("msg", "nothing happened")
        }
    }

    private var dateClick: BasisTimesUtils.OnDatePickerListener = object : BasisTimesUtils.OnDatePickerListener {

            override fun onConfirm(date: String, dateType: String) {
                if (dateType == START_TIME) {
                    startTime = date
                } else if (dateType == END_TIME) {
                    endTime = date
                } else updateTime = date
            }
        }
}