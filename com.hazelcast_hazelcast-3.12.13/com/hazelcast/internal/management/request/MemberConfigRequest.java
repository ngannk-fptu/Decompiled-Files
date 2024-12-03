/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigXmlGenerator;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;

public class MemberConfigRequest
implements ConsoleRequest {
    @Override
    public int getType() {
        return 8;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) {
        JsonObject result = new JsonObject();
        ConfigXmlGenerator configXmlGenerator = new ConfigXmlGenerator(true);
        Config config = mcs.getHazelcastInstance().getConfig();
        String configXmlString = configXmlGenerator.generate(config);
        result.add("configXmlString", configXmlString);
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

