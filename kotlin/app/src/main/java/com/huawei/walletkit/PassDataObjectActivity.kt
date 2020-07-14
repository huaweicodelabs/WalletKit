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
import com.huawei.walletkit.util.BasisTimesUtils.END_TIME
import com.huawei.walletkit.util.BasisTimesUtils.OnDatePickerListener
import com.huawei.walletkit.util.BasisTimesUtils.START_TIME
import com.huawei.walletkit.util.BasisTimesUtils.TIME_FORMATE
import com.huawei.walletkit.util.BasisTimesUtils.getLongTimeOfYMD
import com.huawei.walletkit.util.BasisTimesUtils.showDatePickerDialog
import com.huawei.walletkit.util.CommonUtil.ISSUREID
import com.huawei.walletkit.util.CommonUtil.PASSOBJECT
import com.huawei.walletkit.util.CommonUtil.showToast
import kotlinx.android.synthetic.main.pass_info.*
import java.text.SimpleDateFormat
import java.util.*

class PassDataObjectActivity : AppCompatActivity(), View.OnClickListener {

    private var statuslist = arrayOf("ACTIVE", "COMPLETED", "EXPIRED", "INACTIVE")
    private var index = 0
    private var startTime: String = ""
    private var endTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pass_info)

        selectStartTime.setOnClickListener(this)
        selectEndTime.setOnClickListener(this)

        val statuslistadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuslist)
        statuslistadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = statuslistadapter

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, arg2: Int, arg3: Long) {
                index = arg2
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        saveData.setOnClickListener(View.OnClickListener {

            //serinumber
            val serinumber = serinumberLoyalty.text.toString()
            if (TextUtils.isEmpty(serinumber)) {
                showToast(this, resources.getString(R.string.serialnumber))
                return@OnClickListener
            }

            //passType
            val typeId = passTypeId.text.toString()
            if (TextUtils.isEmpty(typeId)) {
                showToast(this, resources.getString(R.string.passtype))
                return@OnClickListener
            }

            // issuerId
            val issuerId = issuerId.text.toString()
            if (TextUtils.isEmpty(issuerId)) {
                showToast(this, resources.getString(R.string.issuerid))
                return@OnClickListener
            }

            //cardNumber
            val cardNumber = cardNumberLoyalty.text.toString()
            if (TextUtils.isEmpty(cardNumber)) {
                showToast(this, resources.getString(R.string.cardnumber))
                return@OnClickListener
            }

            //memberName
            val memberNameLoyalty = memberName.text.toString()
            if (TextUtils.isEmpty(memberNameLoyalty)) {
                showToast(this, resources.getString(R.string.membername))
                return@OnClickListener
            }

            //latitude and longitude
            val latitudeStr = latitude.text.toString()
            val longitudeStr = longitude.text.toString()
            val latitudeStr1 = latitude1.text.toString()
            val longitudeStr1 = longitude1.text.toString()

            if (TextUtils.isEmpty(latitudeStr) || TextUtils.isEmpty(longitudeStr)) {
                showToast(this, resources.getString(R.string.latitudeandlongitude))
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
            val passBuilder = PassObject.getBuilder()

            //commonFields
            val commonField = ArrayList<CommonField>()

            //appendFields
            val appendFields = ArrayList<AppendField>()

            //Background image of the card
            val backgroundImageCommonFild = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BACKGROUND_IMG)
                .setLabel(resources.getString(R.string.backgroundImageLable))
                .setValue(backgroundImage.text.toString())
                .build()
            commonField.add(backgroundImageCommonFild)

            //Logo on the card
            val logoCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_LOGO)
                .setLabel(resources.getString(R.string.logolabel))
                .setValue(logoLoyalty.text.toString())
                .build()
            commonField.add(logoCommonField)

            //Merchant name
            val merchantNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_MERCHANT_NAME)
                .setLabel(resources.getString(R.string.merchantNamelabel))
                .setValue(merchantNameLoyalty.text.toString())
                .build()
            commonField.add(merchantNameCommonField)

            //Card name
            val cardNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_NAME)
                .setLabel(resources.getString(R.string.cardNamelabel))
                .setValue(nameLoyalty.text.toString())
                .build()
            commonField.add(cardNameCommonField)

            //Card number
            val cardNumberCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_CARD_NUMBER)
                .setLabel(resources.getString(R.string.membernumber))
                .setValue(cardNumber)
                .build()
            commonField.add(cardNumberCommonField)

            //Balance
            val balanceCommonField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_BALANCE)
                .setLabel(resources.getString(R.string.balancelabel))
                .setValue(balanceLoyalty.text.toString())
                .build()
            appendFields.add(balanceCommonField)

            //Number of associated coupons
            val relatedPassIds = ArrayList<RelatedPassInfo>()
            relatedPassIds.add(RelatedPassInfo(relatedPassId1.text.toString(), relatedPassId2.text.toString()))
            relatedPassIds.add(RelatedPassInfo(relatedPassId3.text.toString(),relatedPassId4.text.toString()))
            passBuilder.addRelatedPassIds(relatedPassIds)

            //Number of loyalty points
            val pointsNubAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_POINTS)
                .setLabel("Points")
                .setValue(pointsLoyalty.text.toString())
                .build()
            appendFields.add(pointsNubAppendField)

            //Barcode or QR code
            //Remarks
            val barCode = BarCode.getBuilder()
                .setType(BarCode.BARCODE_TYPE_QR_CODE)
                .setValue(barcodeValueLoyalty.text.toString())
                .settext(barcodeTextLoyalty.text.toString())
                .build()
            passBuilder.setBarCode(barCode)

            //Member name
            val memberNameCommonField = CommonField.getBuilder()
                .setKey(WalletPassConstant.PASS_COMMON_FIELD_KEY_MEMBER_NAME)
                .setLabel("Member Name")
                .setValue(memberNameLoyalty)
                .build()
            commonField.add(memberNameCommonField)

            //Loyalty level
            val levelAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_REWARDS_LEVEL)
                .setLabel("Tier")
                .setValue(levelLoyalty.text.toString())
                .build()
            appendFields.add(levelAppendField)

            //Message
            val messageList = ArrayList<AppendField>()
            messageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(messageHeader.text.toString())
                    .setValue(messageBody.text.toString())
                    .build())
            messageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(messageHeader1.text.toString())
                    .setValue(messageBody1.text.toString())
                    .build())
            passBuilder.addMessageList(messageList)

            //ImageUris
            val imageList = ArrayList<AppendField>()
            imageList.add(AppendField.getBuilder()
                    .setKey("1")
                    .setLabel(imageModuleDataMainImageUrisDes.text.toString())
                    .setValue(imageModuleDataMainImageUris.text.toString())
                    .build())
            imageList.add(AppendField.getBuilder()
                    .setKey("2")
                    .setLabel(imageModuleDataMainImageUrisDes1.text.toString())
                    .setValue(imageModuleDataMainImageUris1.text.toString())
                    .build())

            passBuilder.addImageList(imageList)

            //Nearby stores
            val nearbyLocationsLableStr = nearbyLocationsLable.text.toString()
            val nearbyLocationsValueStr = nearbyLocationsValue.text.toString()
            val nearbyAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_NEARBY_LOCATIONS)
                .setLabel(nearbyLocationsLableStr)
                .setValue(nearbyLocationsValueStr)
                .build()
            appendFields.add(nearbyAppendField)

            //Main page
            val websiteLableStr = websiteLable.text.toString()
            val websiteValueStr = websiteValue.text.toString()
            val mainPageAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_MAINPAGE)
                .setLabel(websiteLableStr)
                .setValue(websiteValueStr)
                .build()
            appendFields.add(mainPageAppendField)

            //Hotline
            val hotlineLableStr = hotlineLable.text.toString()
            val hotlineValueStr = hotlineValue.text.toString()
            val hotlineAppendField = AppendField.getBuilder()
                .setKey(WalletPassConstant.PASS_APPEND_FIELD_KEY_HOTLINE)
                .setLabel(hotlineLableStr)
                .setValue(hotlineValueStr)
                .build()
            appendFields.add(hotlineAppendField)
            //LatLng
            val locationList = ArrayList<Location>()
            locationList.add(Location(latitudeStr, longitudeStr))
            locationList.add(Location(latitudeStr1, longitudeStr1))
            passBuilder.addLocationList(locationList)

            val start = if (TextUtils.isEmpty(startTime)) {
                showToast(this, resources.getString(R.string.startTime))
                return@OnClickListener
            } else getLongTimeOfYMD(startTime)

            val end = if (TextUtils.isEmpty(endTime)) {
                showToast(this, resources.getString(R.string.endTime))
                return@OnClickListener
            } else getLongTimeOfYMD(endTime)

            //time check
            val date = Date()
            if (end <= start || end <= date.time) {
                showToast(this, resources.getString(R.string.timedifference))
                return@OnClickListener
            }
            val format = SimpleDateFormat(TIME_FORMATE, Locale.ENGLISH)
            passBuilder.setOrganizationPassId(cardNumber)
                .setPassStyleIdentifier(passStyleIdentifier.text.toString())
                .setPassTypeIdentifier(typeId)
                .setStatus(PassStatus.getBuilder().setState(state).setEffectTime(format.format(Date(start))).setExpireTime(format.format(Date(end))).build())
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.selectStartTime -> showDatePickerDialog(this, START_TIME, dateClick)
            R.id.selectEndTime -> showDatePickerDialog(this, END_TIME, dateClick)
            else -> Log.d("msg", "nothing happened")
        }
    }

    private var dateClick: OnDatePickerListener = object : OnDatePickerListener {

        override fun onConfirm(date: String, dateType: String) {
            if (dateType == START_TIME) startTime = date else endTime = date
        }
    }
}