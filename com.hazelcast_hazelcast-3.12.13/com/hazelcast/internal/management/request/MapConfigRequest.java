/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Member;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.dto.MapConfigDTO;
import com.hazelcast.internal.management.operation.GetMapConfigOperation;
import com.hazelcast.internal.management.operation.UpdateMapConfigOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;
import java.util.Set;

public class MapConfigRequest
implements ConsoleRequest {
    private String mapName;
    private MapConfigDTO config;
    private boolean update;

    public MapConfigRequest() {
    }

    public MapConfigRequest(String mapName, MapConfigDTO config, boolean update) {
        this.mapName = mapName;
        this.config = config;
        this.update = update;
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) {
        JsonObject result = new JsonObject();
        result.add("update", this.update);
        if (this.update) {
            Set<Member> members = mcs.getHazelcastInstance().getCluster().getMembers();
            for (Member member : members) {
                ManagementCenterService.resolveFuture(mcs.callOnMember(member, new UpdateMapConfigOperation(this.mapName, this.config.getMapConfig())));
            }
            result.add("updateResult", "success");
        } else {
            MapConfig cfg = (MapConfig)ManagementCenterService.resolveFuture(mcs.callOnThis(new GetMapConfigOperation(this.mapName)));
            if (cfg != null) {
                result.add("hasMapConfig", true);
                result.add("mapConfig", new MapConfigDTO(cfg).toJson());
            } else {
                result.add("hasMapConfig", false);
            }
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.mapName = JsonUtil.getString(json, "mapName");
        this.update = JsonUtil.getBoolean(json, "update");
        this.config = new MapConfigDTO();
        this.config.fromJson(JsonUtil.getObject(json, "config"));
    }
}

