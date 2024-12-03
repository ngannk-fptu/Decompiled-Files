/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.core.EntryView;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;

public class GetMapEntryRequest
implements ConsoleRequest {
    private String mapName;
    private String type;
    private String key;

    public GetMapEntryRequest() {
    }

    public GetMapEntryRequest(String type, String mapName, String key) {
        this.type = type;
        this.mapName = mapName;
        this.key = key;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) throws Exception {
        IMap map = mcs.getHazelcastInstance().getMap(this.mapName);
        JsonObject result = new JsonObject();
        EntryView entry = null;
        if (this.type.equals("string")) {
            entry = map.getEntryView(this.key);
        } else if (this.type.equals("long")) {
            entry = map.getEntryView(Long.valueOf(this.key));
        } else if (this.type.equals("integer")) {
            entry = map.getEntryView(Integer.valueOf(this.key));
        }
        if (entry != null) {
            Object value = entry.getValue();
            result.add("browse_value", value != null ? value.toString() : "null");
            result.add("browse_class", value != null ? value.getClass().getName() : "null");
            result.add("memory_cost", Long.toString(entry.getCost()));
            result.add("date_creation_time", Long.toString(entry.getCreationTime()));
            result.add("date_expiration_time", Long.toString(entry.getExpirationTime()));
            result.add("browse_hits", Long.toString(entry.getHits()));
            result.add("date_access_time", Long.toString(entry.getLastAccessTime()));
            result.add("date_update_time", Long.toString(entry.getLastUpdateTime()));
            result.add("browse_version", Long.toString(entry.getVersion()));
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.mapName = JsonUtil.getString(json, "mapName");
        this.type = JsonUtil.getString(json, "type");
        this.key = JsonUtil.getString(json, "key");
    }
}

