/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.AdvancedNetworkStatsDTO;
import com.hazelcast.internal.management.dto.ClientEndPointDTO;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.internal.management.dto.MXBeansDTO;
import com.hazelcast.monitor.HotRestartState;
import com.hazelcast.monitor.LocalCacheStats;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.monitor.LocalInstanceStats;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.monitor.LocalMemoryStats;
import com.hazelcast.monitor.LocalMultiMapStats;
import com.hazelcast.monitor.LocalOperationStats;
import com.hazelcast.monitor.LocalPNCounterStats;
import com.hazelcast.monitor.LocalQueueStats;
import com.hazelcast.monitor.LocalReplicatedMapStats;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.MemberPartitionState;
import com.hazelcast.monitor.MemberState;
import com.hazelcast.monitor.NodeState;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.monitor.impl.HotRestartStateImpl;
import com.hazelcast.monitor.impl.LocalCacheStatsImpl;
import com.hazelcast.monitor.impl.LocalExecutorStatsImpl;
import com.hazelcast.monitor.impl.LocalFlakeIdGeneratorStatsImpl;
import com.hazelcast.monitor.impl.LocalMapStatsImpl;
import com.hazelcast.monitor.impl.LocalMemoryStatsImpl;
import com.hazelcast.monitor.impl.LocalMultiMapStatsImpl;
import com.hazelcast.monitor.impl.LocalOperationStatsImpl;
import com.hazelcast.monitor.impl.LocalPNCounterStatsImpl;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.monitor.impl.LocalReplicatedMapStatsImpl;
import com.hazelcast.monitor.impl.LocalTopicStatsImpl;
import com.hazelcast.monitor.impl.LocalWanStatsImpl;
import com.hazelcast.monitor.impl.MemberPartitionStateImpl;
import com.hazelcast.monitor.impl.NodeStateImpl;
import com.hazelcast.monitor.impl.WanSyncStateImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.JsonUtil;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MemberStateImpl
implements MemberState {
    private String address;
    private String uuid;
    private String cpMemberUuid;
    private Map<EndpointQualifier, Address> endpoints = new HashMap<EndpointQualifier, Address>();
    private Map<String, Long> runtimeProps = new HashMap<String, Long>();
    private Map<String, LocalMapStats> mapStats = new HashMap<String, LocalMapStats>();
    private Map<String, LocalMultiMapStats> multiMapStats = new HashMap<String, LocalMultiMapStats>();
    private Map<String, LocalQueueStats> queueStats = new HashMap<String, LocalQueueStats>();
    private Map<String, LocalTopicStats> topicStats = new HashMap<String, LocalTopicStats>();
    private Map<String, LocalTopicStats> reliableTopicStats = new HashMap<String, LocalTopicStats>();
    private Map<String, LocalPNCounterStats> pnCounterStats = new HashMap<String, LocalPNCounterStats>();
    private Map<String, LocalExecutorStats> executorStats = new HashMap<String, LocalExecutorStats>();
    private Map<String, LocalReplicatedMapStats> replicatedMapStats = new HashMap<String, LocalReplicatedMapStats>();
    private Map<String, LocalCacheStats> cacheStats = new HashMap<String, LocalCacheStats>();
    private Map<String, LocalWanStats> wanStats = new HashMap<String, LocalWanStats>();
    private Map<String, LocalFlakeIdGeneratorStats> flakeIdGeneratorStats = new HashMap<String, LocalFlakeIdGeneratorStats>();
    private Collection<ClientEndPointDTO> clients = new HashSet<ClientEndPointDTO>();
    private Map<String, String> clientStats = new HashMap<String, String>();
    private MXBeansDTO beans = new MXBeansDTO();
    private LocalMemoryStats memoryStats = new LocalMemoryStatsImpl();
    private MemberPartitionState memberPartitionState = new MemberPartitionStateImpl();
    private LocalOperationStats operationStats = new LocalOperationStatsImpl();
    private NodeState nodeState = new NodeStateImpl();
    private HotRestartState hotRestartState = new HotRestartStateImpl();
    private ClusterHotRestartStatusDTO clusterHotRestartStatus = new ClusterHotRestartStatusDTO();
    private WanSyncState wanSyncState = new WanSyncStateImpl();
    private AdvancedNetworkStatsDTO inboundNetworkStats = new AdvancedNetworkStatsDTO();
    private AdvancedNetworkStatsDTO outboundNetworkStats = new AdvancedNetworkStatsDTO();

    @Override
    public Map<String, Long> getRuntimeProps() {
        return this.runtimeProps;
    }

    public void setRuntimeProps(Map<String, Long> runtimeProps) {
        this.runtimeProps = runtimeProps;
    }

    @Override
    public LocalMapStats getLocalMapStats(String mapName) {
        return this.mapStats.get(mapName);
    }

    @Override
    public LocalMultiMapStats getLocalMultiMapStats(String mapName) {
        return this.multiMapStats.get(mapName);
    }

    @Override
    public LocalQueueStats getLocalQueueStats(String queueName) {
        return this.queueStats.get(queueName);
    }

    @Override
    public LocalTopicStats getLocalTopicStats(String topicName) {
        return this.topicStats.get(topicName);
    }

    @Override
    public LocalTopicStats getReliableLocalTopicStats(String reliableTopicName) {
        return this.reliableTopicStats.get(reliableTopicName);
    }

    @Override
    public LocalPNCounterStats getLocalPNCounterStats(String pnCounterName) {
        return this.pnCounterStats.get(pnCounterName);
    }

    @Override
    public LocalReplicatedMapStats getLocalReplicatedMapStats(String replicatedMapName) {
        return this.replicatedMapStats.get(replicatedMapName);
    }

    @Override
    public LocalExecutorStats getLocalExecutorStats(String executorName) {
        return this.executorStats.get(executorName);
    }

    @Override
    public LocalCacheStats getLocalCacheStats(String cacheName) {
        return this.cacheStats.get(cacheName);
    }

    @Override
    public LocalWanStats getLocalWanStats(String schemeName) {
        return this.wanStats.get(schemeName);
    }

    @Override
    public LocalFlakeIdGeneratorStats getLocalFlakeIdGeneratorStats(String flakeIdName) {
        return this.flakeIdGeneratorStats.get(flakeIdName);
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getCpMemberUuid() {
        return this.cpMemberUuid;
    }

    public void setCpMemberUuid(String cpMemberUuid) {
        this.cpMemberUuid = cpMemberUuid;
    }

    public Map<EndpointQualifier, Address> getEndpoints() {
        return this.endpoints;
    }

    public void setEndpoints(Map<EndpointQualifier, Address> addressMap) {
        this.endpoints = addressMap;
    }

    public void putLocalMapStats(String name, LocalMapStats localMapStats) {
        this.mapStats.put(name, localMapStats);
    }

    public void putLocalMultiMapStats(String name, LocalMultiMapStats localMultiMapStats) {
        this.multiMapStats.put(name, localMultiMapStats);
    }

    public void putLocalQueueStats(String name, LocalQueueStats localQueueStats) {
        this.queueStats.put(name, localQueueStats);
    }

    public void putLocalReplicatedMapStats(String name, LocalReplicatedMapStats localReplicatedMapStats) {
        this.replicatedMapStats.put(name, localReplicatedMapStats);
    }

    public void putLocalTopicStats(String name, LocalTopicStats localTopicStats) {
        this.topicStats.put(name, localTopicStats);
    }

    public void putLocalReliableTopicStats(String name, LocalTopicStats localTopicStats) {
        this.reliableTopicStats.put(name, localTopicStats);
    }

    public void putLocalPNCounterStats(String name, LocalPNCounterStats localPNCounterStats) {
        this.pnCounterStats.put(name, localPNCounterStats);
    }

    public void putLocalExecutorStats(String name, LocalExecutorStats localExecutorStats) {
        this.executorStats.put(name, localExecutorStats);
    }

    public void putLocalCacheStats(String name, LocalCacheStats localCacheStats) {
        this.cacheStats.put(name, localCacheStats);
    }

    public void putLocalWanStats(String name, LocalWanStats localWanStats) {
        this.wanStats.put(name, localWanStats);
    }

    public void putLocalFlakeIdStats(String name, LocalFlakeIdGeneratorStats localFlakeIdStats) {
        this.flakeIdGeneratorStats.put(name, localFlakeIdStats);
    }

    @Override
    public Collection<ClientEndPointDTO> getClients() {
        return this.clients;
    }

    @Override
    public MXBeansDTO getMXBeans() {
        return this.beans;
    }

    public void setBeans(MXBeansDTO beans) {
        this.beans = beans;
    }

    public void setClients(Collection<ClientEndPointDTO> clients) {
        this.clients = clients;
    }

    @Override
    public LocalMemoryStats getLocalMemoryStats() {
        return this.memoryStats;
    }

    public void setLocalMemoryStats(LocalMemoryStats memoryStats) {
        this.memoryStats = memoryStats;
    }

    @Override
    public LocalOperationStats getOperationStats() {
        return this.operationStats;
    }

    public void setOperationStats(LocalOperationStats operationStats) {
        this.operationStats = operationStats;
    }

    @Override
    public MemberPartitionState getMemberPartitionState() {
        return this.memberPartitionState;
    }

    @Override
    public NodeState getNodeState() {
        return this.nodeState;
    }

    public void setNodeState(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    @Override
    public HotRestartState getHotRestartState() {
        return this.hotRestartState;
    }

    public void setHotRestartState(HotRestartState hotRestartState) {
        this.hotRestartState = hotRestartState;
    }

    @Override
    public ClusterHotRestartStatusDTO getClusterHotRestartStatus() {
        return this.clusterHotRestartStatus;
    }

    public void setClusterHotRestartStatus(ClusterHotRestartStatusDTO clusterHotRestartStatus) {
        this.clusterHotRestartStatus = clusterHotRestartStatus;
    }

    @Override
    public WanSyncState getWanSyncState() {
        return this.wanSyncState;
    }

    public void setWanSyncState(WanSyncState wanSyncState) {
        this.wanSyncState = wanSyncState;
    }

    public Map<String, String> getClientStats() {
        return this.clientStats;
    }

    public void setClientStats(Map<String, String> clientStats) {
        this.clientStats = clientStats;
    }

    public AdvancedNetworkStatsDTO getInboundNetworkStats() {
        return this.inboundNetworkStats;
    }

    public void setInboundNetworkStats(AdvancedNetworkStatsDTO inboundNetworkStats) {
        this.inboundNetworkStats = inboundNetworkStats;
    }

    public AdvancedNetworkStatsDTO getOutboundNetworkStats() {
        return this.outboundNetworkStats;
    }

    public void setOutboundNetworkStats(AdvancedNetworkStatsDTO outboundNetworkStats) {
        this.outboundNetworkStats = outboundNetworkStats;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("address", this.address);
        root.add("uuid", this.uuid);
        root.add("cpMemberUuid", this.cpMemberUuid);
        JsonArray endpoints = new JsonArray();
        for (Map.Entry<EndpointQualifier, Address> entry : this.endpoints.entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.set("host", entry.getValue().getHost());
            jsonObject.set("port", entry.getValue().getPort());
            JsonObject endpoint = new JsonObject();
            endpoint.set("protocol", entry.getKey().getType().name());
            endpoint.set("address", jsonObject);
            if (entry.getKey().getIdentifier() != null) {
                endpoint.set("id", entry.getKey().getIdentifier());
            }
            endpoints.add(endpoint);
        }
        root.add("endpoints", endpoints);
        MemberStateImpl.serializeMap(root, "mapStats", this.mapStats);
        MemberStateImpl.serializeMap(root, "multiMapStats", this.multiMapStats);
        MemberStateImpl.serializeMap(root, "replicatedMapStats", this.replicatedMapStats);
        MemberStateImpl.serializeMap(root, "queueStats", this.queueStats);
        MemberStateImpl.serializeMap(root, "topicStats", this.topicStats);
        MemberStateImpl.serializeMap(root, "reliableTopicStats", this.reliableTopicStats);
        MemberStateImpl.serializeMap(root, "pnCounterStats", this.pnCounterStats);
        MemberStateImpl.serializeMap(root, "executorStats", this.executorStats);
        MemberStateImpl.serializeMap(root, "cacheStats", this.cacheStats);
        MemberStateImpl.serializeMap(root, "wanStats", this.wanStats);
        MemberStateImpl.serializeMap(root, "flakeIdStats", this.flakeIdGeneratorStats);
        JsonObject runtimePropsObject = new JsonObject();
        for (Map.Entry<String, Long> entry : this.runtimeProps.entrySet()) {
            runtimePropsObject.add(entry.getKey(), entry.getValue());
        }
        root.add("runtimeProps", runtimePropsObject);
        JsonArray jsonArray = new JsonArray();
        for (ClientEndPointDTO client : this.clients) {
            jsonArray.add(client.toJson());
        }
        root.add("clients", jsonArray);
        root.add("beans", this.beans.toJson());
        root.add("memoryStats", this.memoryStats.toJson());
        root.add("operationStats", this.operationStats.toJson());
        root.add("memberPartitionState", this.memberPartitionState.toJson());
        root.add("nodeState", this.nodeState.toJson());
        root.add("hotRestartState", this.hotRestartState.toJson());
        root.add("clusterHotRestartStatus", this.clusterHotRestartStatus.toJson());
        root.add("wanSyncState", this.wanSyncState.toJson());
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, String> entry : this.clientStats.entrySet()) {
            jsonObject.add(entry.getKey(), entry.getValue());
        }
        root.add("clientStats", jsonObject);
        root.add("inboundNetworkStats", this.inboundNetworkStats.toJson());
        root.add("outboundNetworkStats", this.outboundNetworkStats.toJson());
        return root;
    }

    private static void serializeMap(JsonObject root, String key, Map<String, ? extends JsonSerializable> map) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, ? extends JsonSerializable> e : map.entrySet()) {
            jsonObject.add(e.getKey(), e.getValue().toJson());
        }
        root.add(key, jsonObject);
    }

    @Override
    public void fromJson(JsonObject json) {
        JsonObject jsonOutboundNetworkStats;
        JsonObject jsonWanSyncState;
        JsonObject jsonClusterHotRestartStatus;
        JsonObject jsonHotRestartState;
        JsonObject jsonNodeState;
        JsonObject jsonMemberPartitionState;
        JsonObject jsonOperationStats;
        LocalInstanceStats stats;
        this.address = JsonUtil.getString(json, "address");
        this.uuid = JsonUtil.getString(json, "uuid", null);
        this.cpMemberUuid = JsonUtil.getString(json, "cpMemberUuid", null);
        JsonArray endpoints = JsonUtil.getArray(json, "endpoints");
        for (JsonValue obj : endpoints) {
            JsonObject endpoint = obj.asObject();
            String id = endpoint.getString("id", null);
            ProtocolType type = ProtocolType.valueOf(endpoint.getString("protocol", "MEMBER"));
            JsonValue addr = endpoint.get("address");
            String host = addr.asObject().getString("host", "");
            int port = addr.asObject().getInt("port", 0);
            EndpointQualifier qualifier = EndpointQualifier.resolve(type, id);
            Address address = null;
            try {
                address = new Address(host, port);
            }
            catch (UnknownHostException e) {
                EmptyStatement.ignore(e);
            }
            this.endpoints.put(qualifier, address);
        }
        for (Object next : JsonUtil.getObject(json, "mapStats")) {
            stats = new LocalMapStatsImpl();
            ((LocalMapStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.mapStats.put(((JsonObject.Member)next).getName(), (LocalMapStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "multiMapStats")) {
            stats = new LocalMultiMapStatsImpl();
            ((LocalMapStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.multiMapStats.put(((JsonObject.Member)next).getName(), (LocalMultiMapStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "replicatedMapStats", new JsonObject())) {
            stats = new LocalReplicatedMapStatsImpl();
            stats.fromJson(((JsonObject.Member)next).getValue().asObject());
            this.replicatedMapStats.put(((JsonObject.Member)next).getName(), (LocalReplicatedMapStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "queueStats")) {
            stats = new LocalQueueStatsImpl();
            ((LocalQueueStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.queueStats.put(((JsonObject.Member)next).getName(), (LocalQueueStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "topicStats")) {
            stats = new LocalTopicStatsImpl();
            ((LocalTopicStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.topicStats.put(((JsonObject.Member)next).getName(), (LocalTopicStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "reliableTopicStats")) {
            stats = new LocalTopicStatsImpl();
            ((LocalTopicStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.reliableTopicStats.put(((JsonObject.Member)next).getName(), (LocalTopicStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "pnCounterStats")) {
            stats = new LocalPNCounterStatsImpl();
            ((LocalPNCounterStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.pnCounterStats.put(((JsonObject.Member)next).getName(), (LocalPNCounterStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "executorStats")) {
            stats = new LocalExecutorStatsImpl();
            ((LocalExecutorStatsImpl)stats).fromJson(((JsonObject.Member)next).getValue().asObject());
            this.executorStats.put(((JsonObject.Member)next).getName(), (LocalExecutorStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "cacheStats", new JsonObject())) {
            stats = new LocalCacheStatsImpl();
            stats.fromJson(((JsonObject.Member)next).getValue().asObject());
            this.cacheStats.put(((JsonObject.Member)next).getName(), (LocalCacheStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "wanStats", new JsonObject())) {
            stats = new LocalWanStatsImpl();
            stats.fromJson(((JsonObject.Member)next).getValue().asObject());
            this.wanStats.put(((JsonObject.Member)next).getName(), (LocalWanStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "flakeIdStats", new JsonObject())) {
            stats = new LocalFlakeIdGeneratorStatsImpl();
            stats.fromJson(((JsonObject.Member)next).getValue().asObject());
            this.flakeIdGeneratorStats.put(((JsonObject.Member)next).getName(), (LocalFlakeIdGeneratorStats)stats);
        }
        for (Object next : JsonUtil.getObject(json, "runtimeProps")) {
            this.runtimeProps.put(((JsonObject.Member)next).getName(), ((JsonObject.Member)next).getValue().asLong());
        }
        JsonArray jsonClients = JsonUtil.getArray(json, "clients");
        for (JsonValue jsonClient : jsonClients) {
            ClientEndPointDTO client = new ClientEndPointDTO();
            client.fromJson(jsonClient.asObject());
            this.clients.add(client);
        }
        this.beans = new MXBeansDTO();
        this.beans.fromJson(JsonUtil.getObject(json, "beans"));
        JsonObject jsonMemoryStats = JsonUtil.getObject(json, "memoryStats", null);
        if (jsonMemoryStats != null) {
            this.memoryStats.fromJson(jsonMemoryStats);
        }
        if ((jsonOperationStats = JsonUtil.getObject(json, "operationStats", null)) != null) {
            this.operationStats.fromJson(jsonOperationStats);
        }
        if ((jsonMemberPartitionState = JsonUtil.getObject(json, "memberPartitionState", null)) != null) {
            this.memberPartitionState = new MemberPartitionStateImpl();
            this.memberPartitionState.fromJson(jsonMemberPartitionState);
        }
        if ((jsonNodeState = JsonUtil.getObject(json, "nodeState", null)) != null) {
            this.nodeState = new NodeStateImpl();
            this.nodeState.fromJson(jsonNodeState);
        }
        if ((jsonHotRestartState = JsonUtil.getObject(json, "hotRestartState", null)) != null) {
            this.hotRestartState = new HotRestartStateImpl();
            this.hotRestartState.fromJson(jsonHotRestartState);
        }
        if ((jsonClusterHotRestartStatus = JsonUtil.getObject(json, "clusterHotRestartStatus", null)) != null) {
            this.clusterHotRestartStatus = new ClusterHotRestartStatusDTO();
            this.clusterHotRestartStatus.fromJson(jsonClusterHotRestartStatus);
        }
        if ((jsonWanSyncState = JsonUtil.getObject(json, "wanSyncState", null)) != null) {
            this.wanSyncState = new WanSyncStateImpl();
            this.wanSyncState.fromJson(jsonWanSyncState);
        }
        for (JsonObject.Member next : JsonUtil.getObject(json, "clientStats")) {
            this.clientStats.put(next.getName(), next.getValue().asString());
        }
        JsonObject jsonInboundNetworkStats = JsonUtil.getObject(json, "inboundNetworkStats", null);
        if (jsonInboundNetworkStats != null) {
            this.inboundNetworkStats = new AdvancedNetworkStatsDTO();
            this.inboundNetworkStats.fromJson(jsonInboundNetworkStats);
        }
        if ((jsonOutboundNetworkStats = JsonUtil.getObject(json, "outboundNetworkStats", null)) != null) {
            this.outboundNetworkStats = new AdvancedNetworkStatsDTO();
            this.outboundNetworkStats.fromJson(jsonOutboundNetworkStats);
        }
    }

    public String toString() {
        return "MemberStateImpl{address=" + this.address + ", uuid=" + this.uuid + ", cpMemberUuid=" + this.cpMemberUuid + ", runtimeProps=" + this.runtimeProps + ", mapStats=" + this.mapStats + ", multiMapStats=" + this.multiMapStats + ", replicatedMapStats=" + this.replicatedMapStats + ", queueStats=" + this.queueStats + ", topicStats=" + this.topicStats + ", reliableTopicStats=" + this.reliableTopicStats + ", pnCounterStats=" + this.pnCounterStats + ", executorStats=" + this.executorStats + ", cacheStats=" + this.cacheStats + ", memoryStats=" + this.memoryStats + ", operationStats=" + this.operationStats + ", memberPartitionState=" + this.memberPartitionState + ", nodeState=" + this.nodeState + ", hotRestartState=" + this.hotRestartState + ", clusterHotRestartStatus=" + this.clusterHotRestartStatus + ", wanSyncState=" + this.wanSyncState + ", flakeIdStats=" + this.flakeIdGeneratorStats + ", clientStats=" + this.clientStats + ", inboundNetworkStats=" + this.inboundNetworkStats + ", outboundNetworkStats=" + this.outboundNetworkStats + '}';
    }
}

