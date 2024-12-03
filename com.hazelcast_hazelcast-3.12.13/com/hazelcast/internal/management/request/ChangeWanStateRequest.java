/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.config.WanPublisherState;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.ChangeWanStateOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;

public class ChangeWanStateRequest
implements ConsoleRequest {
    private String schemeName;
    private String publisherName;
    private WanPublisherState state;

    public ChangeWanStateRequest() {
    }

    public ChangeWanStateRequest(String schemeName, String publisherName, WanPublisherState state) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
        this.state = state;
    }

    @Override
    public int getType() {
        return 33;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) {
        out.add("result", mcs.syncCallOnThis(new ChangeWanStateOperation(this.schemeName, this.publisherName, this.state)));
    }

    @Override
    public void fromJson(JsonObject json) {
        this.schemeName = JsonUtil.getString(json, "schemeName");
        this.publisherName = JsonUtil.getString(json, "publisherName");
        this.state = WanPublisherState.valueOf(JsonUtil.getString(json, "state"));
    }
}

