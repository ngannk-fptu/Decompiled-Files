/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.util.JsonUtil;

public class ConnectionManagerDTO
implements JsonSerializable {
    public int clientConnectionCount;
    public int activeConnectionCount;
    public int connectionCount;

    public ConnectionManagerDTO() {
    }

    public ConnectionManagerDTO(NetworkingService ns) {
        AggregateEndpointManager aggregate = ns.getAggregateEndpointManager();
        EndpointManager cem = ns.getEndpointManager(EndpointQualifier.CLIENT);
        this.clientConnectionCount = cem != null ? cem.getActiveConnections().size() : -1;
        this.activeConnectionCount = aggregate.getActiveConnections().size();
        this.connectionCount = aggregate.getConnections().size();
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("clientConnectionCount", this.clientConnectionCount);
        root.add("activeConnectionCount", this.activeConnectionCount);
        root.add("connectionCount", this.connectionCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.clientConnectionCount = JsonUtil.getInt(json, "clientConnectionCount", -1);
        this.activeConnectionCount = JsonUtil.getInt(json, "activeConnectionCount", -1);
        this.connectionCount = JsonUtil.getInt(json, "connectionCount", -1);
    }
}

