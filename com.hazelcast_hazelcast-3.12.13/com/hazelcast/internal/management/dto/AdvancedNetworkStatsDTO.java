/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.networking.nio.AdvancedNetworkStats;
import com.hazelcast.util.JsonUtil;

public class AdvancedNetworkStatsDTO
implements JsonSerializable {
    private AdvancedNetworkStats advancedNetworkStats;

    public AdvancedNetworkStatsDTO() {
    }

    public AdvancedNetworkStatsDTO(AdvancedNetworkStats advancedNetworkStats) {
        this.advancedNetworkStats = advancedNetworkStats;
    }

    public AdvancedNetworkStats getAdvancedNetworkStats() {
        return this.advancedNetworkStats;
    }

    @Override
    public JsonObject toJson() {
        JsonObject bytesTransceivedJson = new JsonObject();
        for (ProtocolType type : ProtocolType.valuesAsSet()) {
            bytesTransceivedJson.add(type.name(), this.advancedNetworkStats != null ? this.advancedNetworkStats.getBytesTransceivedForProtocol(type) : 0L);
        }
        JsonObject result = new JsonObject();
        result.add("bytesTransceived", bytesTransceivedJson);
        return result;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.advancedNetworkStats = new AdvancedNetworkStats();
        JsonObject bytesTransceivedJson = JsonUtil.getObject(json, "bytesTransceived", null);
        if (bytesTransceivedJson != null) {
            for (ProtocolType type : ProtocolType.valuesAsSet()) {
                this.advancedNetworkStats.setBytesTransceivedForProtocol(type, bytesTransceivedJson.getLong(type.name(), 0L));
            }
        }
    }

    public String toString() {
        return "AdvancedNetworkStatsDTO{advancedNetworkStats=" + this.advancedNetworkStats + '}';
    }
}

