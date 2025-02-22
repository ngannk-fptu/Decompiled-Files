/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.util.JsonUtil;

public class ProxyServiceDTO
implements JsonSerializable {
    public int proxyCount;

    public ProxyServiceDTO() {
    }

    public ProxyServiceDTO(ProxyService proxyService) {
        this.proxyCount = proxyService.getProxyCount();
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("proxyCount", this.proxyCount);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.proxyCount = JsonUtil.getInt(json, "proxyCount", -1);
    }
}

