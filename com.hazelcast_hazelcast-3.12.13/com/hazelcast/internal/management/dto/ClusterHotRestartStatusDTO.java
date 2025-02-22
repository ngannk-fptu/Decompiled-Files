/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.HotRestartClusterDataRecoveryPolicy;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.Map;

public class ClusterHotRestartStatusDTO
implements JsonSerializable {
    private HotRestartClusterDataRecoveryPolicy dataRecoveryPolicy;
    private ClusterHotRestartStatus hotRestartStatus;
    private long remainingValidationTimeMillis;
    private long remainingDataLoadTimeMillis;
    private Map<String, MemberHotRestartStatus> memberHotRestartStatusMap;

    public ClusterHotRestartStatusDTO() {
        this(HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY, ClusterHotRestartStatus.UNKNOWN, -1L, -1L, Collections.emptyMap());
    }

    public ClusterHotRestartStatusDTO(HotRestartClusterDataRecoveryPolicy dataRecoveryPolicy, ClusterHotRestartStatus hotRestartStatus, long remainingValidationTimeMillis, long remainingDataLoadTimeMillis, Map<String, MemberHotRestartStatus> memberHotRestartStatusMap) {
        Preconditions.isNotNull(dataRecoveryPolicy, "dataRecoveryPolicy");
        Preconditions.isNotNull(hotRestartStatus, "hotRestartStatus");
        Preconditions.isNotNull(memberHotRestartStatusMap, "memberHotRestartStatusMap");
        this.dataRecoveryPolicy = dataRecoveryPolicy;
        this.hotRestartStatus = hotRestartStatus;
        this.remainingValidationTimeMillis = remainingValidationTimeMillis;
        this.remainingDataLoadTimeMillis = remainingDataLoadTimeMillis;
        this.memberHotRestartStatusMap = memberHotRestartStatusMap;
    }

    public HotRestartClusterDataRecoveryPolicy getDataRecoveryPolicy() {
        return this.dataRecoveryPolicy;
    }

    public ClusterHotRestartStatus getHotRestartStatus() {
        return this.hotRestartStatus;
    }

    public long getRemainingValidationTimeMillis() {
        return this.remainingValidationTimeMillis;
    }

    public long getRemainingDataLoadTimeMillis() {
        return this.remainingDataLoadTimeMillis;
    }

    public Map<String, MemberHotRestartStatus> getMemberHotRestartStatusMap() {
        return this.memberHotRestartStatusMap;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("dataRecoveryPolicy", this.dataRecoveryPolicy.toString());
        root.add("hotRestartStatus", this.hotRestartStatus.toString());
        root.add("remainingValidationTimeMillis", this.remainingValidationTimeMillis);
        root.add("remainingDataLoadTimeMillis", this.remainingDataLoadTimeMillis);
        JsonArray memberStatuses = new JsonArray();
        for (Map.Entry<String, MemberHotRestartStatus> entry : this.memberHotRestartStatusMap.entrySet()) {
            String member = entry.getKey();
            MemberHotRestartStatus status = entry.getValue();
            JsonObject pair = new JsonObject();
            pair.add("member", member);
            pair.add("status", status.toString());
            memberStatuses.add(pair);
        }
        root.add("memberHotRestartStatuses", memberStatuses);
        return root;
    }

    @Override
    public void fromJson(JsonObject root) {
        String dataRecoveryPolicyStr = root.getString("dataRecoveryPolicy", HotRestartClusterDataRecoveryPolicy.FULL_RECOVERY_ONLY.toString());
        this.dataRecoveryPolicy = HotRestartClusterDataRecoveryPolicy.valueOf(dataRecoveryPolicyStr);
        String hotRestartStatusStr = root.getString("hotRestartStatus", ClusterHotRestartStatus.UNKNOWN.toString());
        this.hotRestartStatus = ClusterHotRestartStatus.valueOf(hotRestartStatusStr);
        this.remainingValidationTimeMillis = root.getLong("remainingValidationTimeMillis", -1L);
        this.remainingDataLoadTimeMillis = root.getLong("remainingDataLoadTimeMillis", -1L);
        JsonArray memberStatuses = (JsonArray)root.get("memberHotRestartStatuses");
        this.memberHotRestartStatusMap = MapUtil.createHashMap(memberStatuses.size());
        for (JsonValue value : memberStatuses) {
            JsonObject memberStatus = (JsonObject)value;
            String member = memberStatus.getString("member", "<unknown>");
            MemberHotRestartStatus status = MemberHotRestartStatus.valueOf(memberStatus.getString("status", MemberHotRestartStatus.PENDING.toString()));
            this.memberHotRestartStatusMap.put(member, status);
        }
    }

    public static enum MemberHotRestartStatus {
        PENDING,
        LOAD_IN_PROGRESS,
        SUCCESSFUL,
        FAILED;

    }

    public static enum ClusterHotRestartStatus {
        UNKNOWN,
        IN_PROGRESS,
        FAILED,
        SUCCEEDED;

    }
}

