/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.wallet.hms.loyalty;

import com.huawei.wallet.hms.ServerApiService;
import com.huawei.wallet.hms.ServerApiServiceImpl;
import com.huawei.wallet.util.ConfigUtil;
import com.huawei.wallet.util.HwWalletObjectUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

/**
 * Loyalty instance tests.
 *
 * @since 2019-12-12
 */
public class LoyaltyInstanceTest {
    private final ServerApiService serverApiService = new ServerApiServiceImpl();

    /**
     * Add a loyalty instance to HMS wallet server.
     * Run the "createLoyaltyModel" test before running this test.
     * After using this API, you will use a thin JWE to bind this instance to a user. Or you can add an instance by
     * sending a JWE with complete instance information, without using this API. See JWE example tests.
     * POST http://XXX/hmspass/v1/loyalty/instance
     */
    @Test
    public void addLoyaltyInstance() {
        System.out.println("addLoyaltyInstance begin.");

        // Read a loyalty instance from a JSON file.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("LoyaltyInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Post the new loyalty instance to HMS wallet server.
        String urlSegment = "loyalty/instance";
        JSONObject responseInstance =
            serverApiService.postToWalletServer(urlSegment, JSONObject.toJSONString(instance));
        System.out.println("Posted loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get a loyalty instance by its instance ID.
     * Run the "createLoyaltyInstance" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void getLoyaltyInstance() {
        System.out.println("getLoyaltyInstance begin.");

        // ID of the loyalty instance you want to get.
        String instanceId = "40001";

        // Get the loyalty instance.
        String urlSegment = "loyalty/instance/";
        JSONObject responseInstance = serverApiService.getHwWalletObjectById(urlSegment, instanceId);
        System.out.println("Corresponding loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Get loyalty instance belonging to a specific loyalty model.
     * Run the "createLoyaltyInstance" test before running this test.
     * GET http://xxx/hmspass/v1/loyalty/instance?modelId=XXX&session=XXX&pageSize=XXX
     */
    @Test
    public void getLoyaltyInstanceList() {
        System.out.println("getLoyaltyInstanceList begin.");

        // Model ID of loyalty instances you want to get.
        String modelId = "loyaltyModelTest";

        // Get a list of loyalty instances.
        String urlSegment = "loyalty/instance";

        JSONArray instances = serverApiService.getInstances(urlSegment, modelId, 5);
        System.out.println("Total instances count: " + instances.size());
        System.out.println("Instances list: " + instances.toJSONString());
    }

    /**
     * Overwrite a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PUT http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void fullUpdateLoyaltyInstance() {
        System.out.println("fullUpdateLoyaltyInstance begin.");

        // Read a HwWalletObject from a JSON file. This HwWalletObject will overwrite the corresponding instance.
        JSONObject instance = JSONObject.parseObject(ConfigUtil.readFile("FullUpdateLoyaltyInstance.json"));

        // Validate parameters.
        HwWalletObjectUtil.validateInstance(instance);

        // Update the loyalty instance.
        String urlSegment = "loyalty/instance/";
        JSONObject responseInstance = serverApiService.fullUpdateHwWalletObject(urlSegment,
            instance.getString("serialNumber"), JSONObject.toJSONString(instance));
        System.out.println("Updated loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}
     */
    @Test
    public void partialUpdateLoyaltyInstance() {
        System.out.println("partialUpdateLoyaltyInstance begin.");

        // ID of the loyalty instance you want to update.
        String instanceId = "40001";

        // Read a HwWalletObject from a JSON file. This HwWalletObject will merge with the corresponding instance.
        String instanceStr = ConfigUtil.readFile("PartialUpdateLoyaltyInstance.json");

        // Update the loyalty instance.
        String urlSegment = "loyalty/instance/";
        JSONObject responseInstance = serverApiService.partialUpdateHwWalletObject(urlSegment, instanceId, instanceStr);
        System.out.println("Updated loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Add messages to a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * POST http://xxx/hmspass/v1/loyalty/instance/{instanceId}/addMessage
     */
    @Test
    public void addMessageToLoyaltyInstance() {
        System.out.println("addMessageToLoyaltyInstance begin.");

        // ID of the loyalty instance you want to update.
        String instanceId = "40001";

        // Create a list of messages you want to add to an instance. Each message contains key, value, and label.
        // The list should not contain multiple messages with the same key. You can update an existing message by adding
        // a new message with the same key. One instance contains at most 10 messages. If an instance already have 10
        // messages and you keep adding new messages, the oldest messages will be removed. You should not add more than
        // 10 messages at a time.

        // Read messages from a JSON file.
        String messagesStr = ConfigUtil.readFile("Messages.json");

        // Add messages to the loyalty instance.
        String urlSegment = "loyalty/instance/";
        JSONObject responseInstance = serverApiService.addMessageToHwWalletObject(urlSegment, instanceId, messagesStr);
        System.out.println("Updated loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }

    /**
     * Update linked offers of a loyalty instance.
     * Run the "createLoyaltyInstance" test before running this test.
     * PATCH http://xxx/hmspass/v1/loyalty/instance/{instanceId}/linkedoffers
     */
    @Test
    public void updateLinkedOffersToLoyaltyInstance() {
        System.out.println("updateLinkedOffersToLoyaltyInstance begin.");

        // ID of the loyalty instance to be updated.
        String instanceId = "40001";

        // Create two lists of linked offer instances, one for offers you want to link to a loyalty instance and the
        // other one for offers you want to remove from it. Each linked offer object has two parameters,
        // passTypeIdentifier and instanceId, indicating which offer instance you want to add or remove. The adding list
        // and the removing list should not contain the same offer instances. You should make sure the offer instances
        // you want to link already exist in HMS wallet server before using this API.

        // Read a LinkedOfferInstanceIds from a JSON file.
        String linkedOfferInstanceIdsStr = ConfigUtil.readFile("LinkedOfferInstanceIds.json");

        // Update relatedPassIds in the loyalty instance.
        String urlSegment = "loyalty/instance/";
        JSONObject responseInstance =
            serverApiService.updateLinkedOffersToLoyaltyInstance(urlSegment, instanceId, linkedOfferInstanceIdsStr);
        System.out.println("Updated loyalty instance: " + JSONObject.toJSONString(responseInstance));
    }
}
