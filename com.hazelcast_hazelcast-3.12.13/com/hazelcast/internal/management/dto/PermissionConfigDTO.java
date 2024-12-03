/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.PermissionConfig;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.ManagementDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PermissionConfigDTO
implements JsonSerializable,
IdentifiedDataSerializable {
    private PermissionConfig permissionConfig;

    public PermissionConfigDTO() {
    }

    public PermissionConfigDTO(PermissionConfig permissionConfig) {
        this.permissionConfig = permissionConfig;
    }

    @Override
    public JsonObject toJson() {
        Set<String> actions;
        JsonObject object = new JsonObject();
        object.add("permissionType", this.permissionConfig.getType().getNodeName());
        object.add("name", this.permissionConfig.getName());
        if (StringUtil.isNullOrEmptyAfterTrim(this.permissionConfig.getPrincipal())) {
            object.add("principal", "*");
        } else {
            object.add("principal", this.permissionConfig.getPrincipal());
        }
        Set<String> endpoints = this.permissionConfig.getEndpoints();
        if (endpoints != null) {
            JsonArray endpointsArray = new JsonArray();
            for (String endpoint : endpoints) {
                endpointsArray.add(endpoint);
            }
            object.add("endpoints", endpointsArray);
        }
        if ((actions = this.permissionConfig.getActions()) != null) {
            JsonArray actionsArray = new JsonArray();
            for (String action : actions) {
                actionsArray.add(action);
            }
            object.add("actions", actionsArray);
        }
        return object;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonValue actionsVal;
        this.permissionConfig = new PermissionConfig();
        this.permissionConfig.setType(PermissionConfig.PermissionType.getType(json.getString("permissionType", null)));
        this.permissionConfig.setName(json.get("name").asString());
        this.permissionConfig.setPrincipal(json.getString("principal", "*"));
        JsonValue endpointsVal = json.get("endpoints");
        if (endpointsVal != null) {
            HashSet<String> endpoints = new HashSet<String>();
            for (JsonValue endpoint : endpointsVal.asArray().values()) {
                endpoints.add(endpoint.asString());
            }
            this.permissionConfig.setEndpoints(endpoints);
        }
        if ((actionsVal = json.get("actions")) != null) {
            HashSet<String> actions = new HashSet<String>();
            for (JsonValue action : actionsVal.asArray().values()) {
                actions.add(action.asString());
            }
            this.permissionConfig.setActions(actions);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.permissionConfig.getType().getNodeName());
        out.writeUTF(this.permissionConfig.getName());
        if (StringUtil.isNullOrEmptyAfterTrim(this.permissionConfig.getPrincipal())) {
            out.writeUTF("*");
        } else {
            out.writeUTF(this.permissionConfig.getPrincipal());
        }
        Set<String> endpoints = this.permissionConfig.getEndpoints();
        out.writeInt(endpoints.size());
        for (String endpoint : endpoints) {
            out.writeUTF(endpoint);
        }
        Set<String> actions = this.permissionConfig.getActions();
        out.writeInt(actions.size());
        for (String action : actions) {
            out.writeUTF(action);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int actionsSize;
        this.permissionConfig = new PermissionConfig();
        this.permissionConfig.setType(PermissionConfig.PermissionType.getType(in.readUTF()));
        this.permissionConfig.setName(in.readUTF());
        this.permissionConfig.setPrincipal(in.readUTF());
        int endpointsSize = in.readInt();
        if (endpointsSize != 0) {
            HashSet<String> endpoints = new HashSet<String>();
            for (int i = 0; i < endpointsSize; ++i) {
                endpoints.add(in.readUTF());
            }
            this.permissionConfig.setEndpoints(endpoints);
        }
        if ((actionsSize = in.readInt()) != 0) {
            HashSet<String> actions = new HashSet<String>();
            for (int i = 0; i < actionsSize; ++i) {
                actions.add(in.readUTF());
            }
            this.permissionConfig.setActions(actions);
        }
    }

    public PermissionConfig getPermissionConfig() {
        return this.permissionConfig;
    }

    @Override
    public int getFactoryId() {
        return ManagementDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 6;
    }
}

