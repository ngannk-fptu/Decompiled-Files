/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.WanCheckConsistencyOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;

public class WanCheckConsistencyRequest
implements ConsoleRequest {
    public static final String SUCCESS = "success";
    private String schemeName;
    private String publisherName;
    private String mapName;

    public WanCheckConsistencyRequest() {
    }

    public WanCheckConsistencyRequest(String schemeName, String publisherName, String mapName) {
        this.schemeName = schemeName;
        this.publisherName = publisherName;
        this.mapName = mapName;
    }

    @Override
    public int getType() {
        return 43;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) {
        out.add("result", mcs.syncCallOnThis(new WanCheckConsistencyOperation(this.schemeName, this.publisherName, this.mapName)));
    }

    @Override
    public void fromJson(JsonObject json) {
        this.schemeName = JsonUtil.getString(json, "schemeName");
        this.publisherName = JsonUtil.getString(json, "publisherName");
        this.mapName = JsonUtil.getString(json, "mapName");
    }
}

