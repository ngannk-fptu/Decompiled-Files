/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.monitor.NodeState;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.version.MemberVersion;
import com.hazelcast.version.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeStateImpl
implements NodeState {
    private ClusterState clusterState;
    private com.hazelcast.instance.NodeState nodeState;
    private Version clusterVersion;
    private MemberVersion memberVersion;
    private Map<String, List<String>> weakSecretsConfigs;

    public NodeStateImpl() {
    }

    public NodeStateImpl(ClusterState clusterState, com.hazelcast.instance.NodeState nodeState, Version clusterVersion, MemberVersion memberVersion) {
        this(clusterState, nodeState, clusterVersion, memberVersion, Collections.emptyMap());
    }

    public NodeStateImpl(ClusterState clusterState, com.hazelcast.instance.NodeState nodeState, Version clusterVersion, MemberVersion memberVersion, Map<String, List<String>> weakSecretsConfigs) {
        this.clusterState = clusterState;
        this.nodeState = nodeState;
        this.clusterVersion = clusterVersion;
        this.memberVersion = memberVersion;
        this.weakSecretsConfigs = weakSecretsConfigs;
    }

    @Override
    public ClusterState getClusterState() {
        return this.clusterState;
    }

    @Override
    public com.hazelcast.instance.NodeState getNodeState() {
        return this.nodeState;
    }

    @Override
    public Version getClusterVersion() {
        return this.clusterVersion;
    }

    @Override
    public MemberVersion getMemberVersion() {
        return this.memberVersion;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("clusterState", this.clusterState.name());
        root.add("nodeState", this.nodeState.name());
        root.add("clusterVersion", this.clusterVersion.toString());
        root.add("memberVersion", this.memberVersion.toString());
        JsonObject weaknesses = new JsonObject();
        for (Map.Entry<String, List<String>> entry : this.weakSecretsConfigs.entrySet()) {
            JsonArray values = new JsonArray();
            for (String value : entry.getValue()) {
                values.add(value);
            }
            weaknesses.add(entry.getKey(), values);
        }
        root.add("weakConfigs", weaknesses);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        String jsonClusterVersion;
        String jsonNodeState;
        String jsonClusterState = JsonUtil.getString(json, "clusterState", null);
        if (jsonClusterState != null) {
            this.clusterState = ClusterState.valueOf(jsonClusterState);
        }
        if ((jsonNodeState = JsonUtil.getString(json, "nodeState", null)) != null) {
            this.nodeState = com.hazelcast.instance.NodeState.valueOf(jsonNodeState);
        }
        if ((jsonClusterVersion = JsonUtil.getString(json, "clusterVersion", null)) != null) {
            this.clusterVersion = Version.of(jsonClusterVersion);
        }
        String jsonNodeVersion = JsonUtil.getString(json, "memberVersion", null);
        if (jsonNodeState != null) {
            this.memberVersion = MemberVersion.of(jsonNodeVersion);
        }
        this.weakSecretsConfigs = new HashMap<String, List<String>>();
        JsonValue jsonWeakConfigs = json.get("weakConfigs");
        if (jsonWeakConfigs != null) {
            JsonObject weakConfigsJsObj = jsonWeakConfigs.asObject();
            for (JsonObject.Member member : weakConfigsJsObj) {
                ArrayList<String> weaknesses = new ArrayList<String>();
                for (JsonValue value : member.getValue().asArray()) {
                    weaknesses.add(value.asString());
                }
                this.weakSecretsConfigs.put(member.getName(), weaknesses);
            }
        }
    }

    public String toString() {
        return "NodeStateImpl{clusterState=" + (Object)((Object)this.clusterState) + ", nodeState=" + (Object)((Object)this.nodeState) + ", clusterVersion=" + this.clusterVersion + ", memberVersion=" + this.memberVersion + '}';
    }
}

