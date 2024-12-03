/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.impl.LocalTopicStatsImpl;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.topic.impl.reliable.ReliableTopicProxy;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.MapUtil;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReliableTopicService
implements ManagedService,
RemoteService,
StatisticsAwareService {
    public static final String SERVICE_NAME = "hz:impl:reliableTopicService";
    private final ConcurrentMap<String, LocalTopicStatsImpl> statsMap = new ConcurrentHashMap<String, LocalTopicStatsImpl>();
    private final ConstructorFunction<String, LocalTopicStatsImpl> localTopicStatsConstructorFunction = new ConstructorFunction<String, LocalTopicStatsImpl>(){

        @Override
        public LocalTopicStatsImpl createNew(String mapName) {
            return new LocalTopicStatsImpl();
        }
    };
    private final NodeEngine nodeEngine;

    public ReliableTopicService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public DistributedObject createDistributedObject(String objectName) {
        ReliableTopicConfig topicConfig = this.nodeEngine.getConfig().findReliableTopicConfig(objectName);
        return new ReliableTopicProxy(objectName, this.nodeEngine, this, topicConfig);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        this.statsMap.remove(objectName);
    }

    public LocalTopicStatsImpl getLocalTopicStats(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.statsMap, name, this.statsMap, this.localTopicStatsConstructorFunction);
    }

    public Map<String, LocalTopicStats> getStats() {
        Map<String, LocalTopicStats> topicStats = MapUtil.createHashMap(this.statsMap.size());
        for (Map.Entry queueStat : this.statsMap.entrySet()) {
            topicStats.put((String)queueStat.getKey(), (LocalTopicStats)queueStat.getValue());
        }
        return topicStats;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
    }

    @Override
    public void reset() {
        this.statsMap.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }
}

