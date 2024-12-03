/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorProxy;
import com.hazelcast.monitor.LocalFlakeIdGeneratorStats;
import com.hazelcast.monitor.impl.LocalFlakeIdGeneratorStatsImpl;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class FlakeIdGeneratorService
implements ManagedService,
RemoteService,
StatisticsAwareService<LocalFlakeIdGeneratorStats> {
    public static final String SERVICE_NAME = "hz:impl:flakeIdGeneratorService";
    private NodeEngine nodeEngine;
    private final ConcurrentHashMap<String, LocalFlakeIdGeneratorStatsImpl> statsMap = new ConcurrentHashMap();
    private final ConstructorFunction<String, LocalFlakeIdGeneratorStatsImpl> localFlakeIdStatsConstructorFunction = new ConstructorFunction<String, LocalFlakeIdGeneratorStatsImpl>(){

        @Override
        public LocalFlakeIdGeneratorStatsImpl createNew(String key) {
            return new LocalFlakeIdGeneratorStatsImpl();
        }
    };

    public FlakeIdGeneratorService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void reset() {
        this.statsMap.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    @Override
    public DistributedObject createDistributedObject(String name) {
        return new FlakeIdGeneratorProxy(name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.statsMap.remove(name);
    }

    @Override
    public Map<String, LocalFlakeIdGeneratorStats> getStats() {
        return new HashMap<String, LocalFlakeIdGeneratorStats>(this.statsMap);
    }

    public void updateStatsForBatch(String name, int batchSize) {
        this.getLocalFlakeIdStats(name).update(batchSize);
    }

    private LocalFlakeIdGeneratorStatsImpl getLocalFlakeIdStats(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.statsMap, name, this.localFlakeIdStatsConstructorFunction);
    }
}

