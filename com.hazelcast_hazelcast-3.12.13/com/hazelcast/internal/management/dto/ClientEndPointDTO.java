/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.core.Client;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ClientEndPointDTO
implements JsonSerializable {
    public String uuid;
    public String address;
    public String clientType;
    public String name;
    public Set<String> labels;
    public String ipAddress;
    public String canonicalHostName;

    public ClientEndPointDTO() {
    }

    public ClientEndPointDTO(Client client) {
        this.uuid = client.getUuid();
        this.clientType = client.getClientType().toString();
        this.name = client.getName();
        this.labels = client.getLabels();
        InetSocketAddress socketAddress = client.getSocketAddress();
        this.address = socketAddress.getHostName() + ":" + socketAddress.getPort();
        InetAddress address = socketAddress.getAddress();
        this.ipAddress = address != null ? address.getHostAddress() : null;
        this.canonicalHostName = address != null ? address.getCanonicalHostName() : null;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = Json.object();
        root.add("uuid", this.uuid);
        root.add("address", this.address);
        root.add("clientType", this.clientType);
        root.add("name", this.name);
        JsonArray labelsObject = Json.array();
        for (String label : this.labels) {
            labelsObject.add(label);
        }
        root.add("labels", labelsObject);
        root.add("ipAddress", this.ipAddress);
        root.add("canonicalHostName", this.canonicalHostName);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.uuid = JsonUtil.getString(json, "uuid");
        this.address = JsonUtil.getString(json, "address");
        this.clientType = JsonUtil.getString(json, "clientType");
        this.name = JsonUtil.getString(json, "name");
        JsonArray labelsArray = JsonUtil.getArray(json, "labels");
        this.labels = new HashSet<String>();
        for (JsonValue labelValue : labelsArray) {
            this.labels.add(labelValue.asString());
        }
        this.ipAddress = JsonUtil.getString(json, "ipAddress", null);
        this.canonicalHostName = JsonUtil.getString(json, "canonicalHostName", null);
    }
}

