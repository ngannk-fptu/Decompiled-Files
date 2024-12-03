/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.ClearWanQueuesOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;

public class ClearWanQueuesRequest
implements ConsoleRequest {
    private String schemeName;
    private String publisherName;

    public ClearWanQueuesRequest() {
    }

    public ClearWanQueuesRequest(String schemeName, String publisherName) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
    }

    @Override
    public int getType() {
        return 40;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) {
        out.add("result", mcs.syncCallOnThis(new ClearWanQueuesOperation(this.schemeName, this.publisherName)));
    }

    @Override
    public void fromJson(JsonObject json) {
        this.schemeName = JsonUtil.getString(json, "schemeName");
        this.publisherName = JsonUtil.getString(json, "publisherName");
    }
}

