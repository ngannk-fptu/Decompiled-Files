/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.config.PermissionConfig;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.PermissionConfigDTO;
import java.util.HashSet;
import java.util.Set;

public class UpdatePermissionConfigRequest
implements JsonSerializable {
    private Set<PermissionConfig> permissionConfigs;

    public UpdatePermissionConfigRequest() {
    }

    public UpdatePermissionConfigRequest(Set<PermissionConfig> permissionConfigs) {
        this.permissionConfigs = permissionConfigs;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        JsonArray permissionConfigsArr = new JsonArray();
        for (PermissionConfig permissionConfig : this.permissionConfigs) {
            permissionConfigsArr.add(new PermissionConfigDTO(permissionConfig).toJson());
        }
        root.add("permissionConfigs", permissionConfigsArr);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonArray confArray = json.get("permissionConfigs").asArray();
        this.permissionConfigs = new HashSet<PermissionConfig>(confArray.size());
        for (JsonValue permissionConfigDTO : confArray.values()) {
            PermissionConfigDTO dto = new PermissionConfigDTO();
            dto.fromJson(permissionConfigDTO.asObject());
            this.permissionConfigs.add(dto.getPermissionConfig());
        }
    }

    public Set<PermissionConfig> getPermissionConfigs() {
        return this.permissionConfigs;
    }
}

