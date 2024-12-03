/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.impl;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.management.events.AddWanConfigIgnoredEvent;
import com.hazelcast.internal.management.events.WanConsistencyCheckIgnoredEvent;
import com.hazelcast.internal.management.events.WanSyncIgnoredEvent;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.WanSyncState;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.wan.AddWanConfigResult;
import com.hazelcast.wan.WanReplicationEndpoint;
import com.hazelcast.wan.WanReplicationPublisher;
import com.hazelcast.wan.WanReplicationService;
import com.hazelcast.wan.impl.DistributedServiceWanEventCounters;
import com.hazelcast.wan.impl.WanEventCounters;
import com.hazelcast.wan.impl.WanReplicationPublisherDelegate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WanReplicationServiceImpl
implements WanReplicationService {
    private final Node node;
    private final WanEventCounters receivedWanEventCounters = new WanEventCounters();
    private final WanEventCounters sentWanEventCounters = new WanEventCounters();
    private final ConcurrentHashMap<String, WanReplicationPublisherDelegate> wanReplications = this.initializeWanReplicationPublisherMapping();
    private final ConstructorFunction<String, WanReplicationPublisherDelegate> publisherDelegateConstructorFunction = new ConstructorFunction<String, WanReplicationPublisherDelegate>(){

        @Override
        public WanReplicationPublisherDelegate createNew(String name) {
            WanReplicationConfig wanReplicationConfig = WanReplicationServiceImpl.this.node.getConfig().getWanReplicationConfig(name);
            if (wanReplicationConfig == null) {
                return null;
            }
            List<WanPublisherConfig> publisherConfigs = wanReplicationConfig.getWanPublisherConfigs();
            return new WanReplicationPublisherDelegate(name, WanReplicationServiceImpl.this.createPublishers(wanReplicationConfig, publisherConfigs));
        }
    };

    public WanReplicationServiceImpl(Node node) {
        this.node = node;
    }

    @Override
    public WanReplicationPublisher getWanReplicationPublisher(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.wanReplications, name, this, this.publisherDelegateConstructorFunction);
    }

    private WanReplicationEndpoint[] createPublishers(WanReplicationConfig wanReplicationConfig, List<WanPublisherConfig> publisherConfigs) {
        WanReplicationEndpoint[] targetEndpoints = new WanReplicationEndpoint[publisherConfigs.size()];
        int count = 0;
        for (WanPublisherConfig publisherConfig : publisherConfigs) {
            WanReplicationEndpoint target = ClassLoaderUtil.getOrCreate((WanReplicationEndpoint)publisherConfig.getImplementation(), this.node.getConfigClassLoader(), publisherConfig.getClassName());
            if (target == null) {
                throw new InvalidConfigurationException("Either 'implementation' or 'className' attribute need to be set in WanPublisherConfig");
            }
            target.init(this.node, wanReplicationConfig, publisherConfig);
            targetEndpoints[count++] = target;
        }
        return targetEndpoints;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdown() {
        WanReplicationServiceImpl wanReplicationServiceImpl = this;
        synchronized (wanReplicationServiceImpl) {
            for (WanReplicationPublisherDelegate wanReplication : this.wanReplications.values()) {
                WanReplicationEndpoint[] endpoints = wanReplication.getEndpoints();
                if (endpoints == null) continue;
                for (WanReplicationEndpoint endpoint : endpoints) {
                    if (endpoint == null) continue;
                    endpoint.shutdown();
                }
            }
            this.wanReplications.clear();
        }
    }

    @Override
    public void pause(String wanReplicationName, String targetGroupName) {
        throw new UnsupportedOperationException("Pausing WAN replication is not supported.");
    }

    @Override
    public void stop(String wanReplicationName, String targetGroupName) {
        throw new UnsupportedOperationException("Stopping WAN replication is not supported");
    }

    @Override
    public void resume(String wanReplicationName, String targetGroupName) {
        throw new UnsupportedOperationException("Resuming WAN replication is not supported");
    }

    @Override
    public void checkWanReplicationQueues(String name) {
    }

    @Override
    public void syncMap(String wanReplicationName, String targetGroupName, String mapName) {
        this.node.getManagementCenterService().log(WanSyncIgnoredEvent.enterpriseOnly(wanReplicationName, targetGroupName, mapName));
        throw new UnsupportedOperationException("WAN sync for map is not supported.");
    }

    @Override
    public void syncAllMaps(String wanReplicationName, String targetGroupName) {
        this.node.getManagementCenterService().log(WanSyncIgnoredEvent.enterpriseOnly(wanReplicationName, targetGroupName, null));
        throw new UnsupportedOperationException("WAN sync is not supported.");
    }

    @Override
    public void consistencyCheck(String wanReplicationName, String targetGroupName, String mapName) {
        this.node.getManagementCenterService().log(new WanConsistencyCheckIgnoredEvent(wanReplicationName, targetGroupName, mapName, "Consistency check is supported for enterprise clusters only."));
        throw new UnsupportedOperationException("Consistency check is not supported.");
    }

    @Override
    public void clearQueues(String wanReplicationName, String targetGroupName) {
        throw new UnsupportedOperationException("Clearing WAN replication queues is not supported.");
    }

    @Override
    public AddWanConfigResult addWanReplicationConfig(WanReplicationConfig wanConfig) {
        this.node.getManagementCenterService().log(AddWanConfigIgnoredEvent.enterpriseOnly(wanConfig.getName()));
        throw new UnsupportedOperationException("Adding new WAN config is not supported.");
    }

    @Override
    public void addWanReplicationConfigLocally(WanReplicationConfig wanConfig) {
        throw new UnsupportedOperationException("Adding new WAN config is not supported.");
    }

    @Override
    public Map<String, LocalWanStats> getStats() {
        return null;
    }

    private ConcurrentHashMap<String, WanReplicationPublisherDelegate> initializeWanReplicationPublisherMapping() {
        return new ConcurrentHashMap<String, WanReplicationPublisherDelegate>(2);
    }

    @Override
    public WanSyncState getWanSyncState() {
        return null;
    }

    @Override
    public DistributedServiceWanEventCounters getReceivedEventCounters(String serviceName) {
        return this.receivedWanEventCounters.getWanEventCounter("", "", serviceName);
    }

    @Override
    public DistributedServiceWanEventCounters getSentEventCounters(String wanReplicationName, String targetGroupName, String serviceName) {
        return this.sentWanEventCounters.getWanEventCounter(wanReplicationName, targetGroupName, serviceName);
    }

    @Override
    public void removeWanEventCounters(String serviceName, String objectName) {
        this.receivedWanEventCounters.removeCounter(serviceName, objectName);
        this.sentWanEventCounters.removeCounter(serviceName, objectName);
    }
}

