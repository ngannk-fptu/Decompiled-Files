/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.AsyncConsoleRequest;

public class PromoteMemberRequest
implements AsyncConsoleRequest {
    @Override
    public int getType() {
        return 42;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        JsonObject result = new JsonObject();
        try {
            mcs.getHazelcastInstance().getCluster().promoteLocalLiteMember();
            result.add("success", true);
        }
        catch (IllegalStateException e) {
            result.add("success", false);
            result.add("message", e.getMessage());
        }
        out.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

