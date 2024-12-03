/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.config.Config;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.crdt.CRDTReplicationAwareService;
import com.hazelcast.crdt.CRDTReplicationContainer;
import com.hazelcast.crdt.MutationDisallowedException;
import com.hazelcast.crdt.pncounter.PNCounterImpl;
import com.hazelcast.crdt.pncounter.PNCounterProxy;
import com.hazelcast.crdt.pncounter.PNCounterReplicationOperation;
import com.hazelcast.internal.util.Memoizer;
import com.hazelcast.monitor.LocalPNCounterStats;
import com.hazelcast.monitor.impl.LocalPNCounterStatsImpl;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.UuidUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PNCounterService
implements ManagedService,
RemoteService,
CRDTReplicationAwareService<PNCounterImpl>,
QuorumAwareService,
StatisticsAwareService<LocalPNCounterStats> {
    public static final String SERVICE_NAME = "hz:impl:PNCounterService";
    private final ConcurrentMap<String, PNCounterImpl> counters = new ConcurrentHashMap<String, PNCounterImpl>();
    private final ConstructorFunction<String, PNCounterImpl> counterConstructorFn = new ConstructorFunction<String, PNCounterImpl>(){

        @Override
        public PNCounterImpl createNew(String name) {
            if (PNCounterService.this.isShuttingDown) {
                throw new MutationDisallowedException("Cannot create a new PN counter named " + name + " because this instance is shutting down!");
            }
            return new PNCounterImpl(UuidUtil.newUnsecureUuidString(), name);
        }
    };
    private final Memoizer<String, Object> quorumConfigCache = new Memoizer<String, Object>(new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            PNCounterConfig counterConfig = PNCounterService.this.nodeEngine.getConfig().findPNCounterConfig(name);
            String quorumName = counterConfig.getQuorumName();
            return quorumName == null ? Memoizer.NULL_OBJECT : quorumName;
        }
    });
    private final ConcurrentMap<String, LocalPNCounterStatsImpl> statsMap = new ConcurrentHashMap<String, LocalPNCounterStatsImpl>();
    private Map unmodifiableStatsMap = Collections.unmodifiableMap(this.statsMap);
    private final ConstructorFunction<String, LocalPNCounterStatsImpl> statsConstructorFunction = new ConstructorFunction<String, LocalPNCounterStatsImpl>(){

        @Override
        public LocalPNCounterStatsImpl createNew(String name) {
            return new LocalPNCounterStatsImpl();
        }
    };
    private final Object newCounterCreationMutex = new Object();
    private volatile boolean isShuttingDown;
    private NodeEngine nodeEngine;

    public PNCounterImpl getCounter(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.counters, name, this.newCounterCreationMutex, this.counterConstructorFn);
    }

    public boolean containsCounter(String name) {
        return this.counters.containsKey(name);
    }

    public LocalPNCounterStatsImpl getLocalPNCounterStats(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.statsMap, name, this.statsMap, this.statsConstructorFunction);
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
        this.counters.clear();
        this.statsMap.clear();
    }

    @Override
    public PNCounterProxy createDistributedObject(String objectName) {
        return new PNCounterProxy(objectName, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String objectName) {
        this.counters.remove(objectName);
        this.statsMap.remove(objectName);
        this.quorumConfigCache.remove(objectName);
    }

    @Override
    public CRDTReplicationContainer prepareReplicationOperation(Map<String, VectorClock> previouslyReplicatedVectorClocks, int targetIndex) {
        HashMap<String, VectorClock> currentVectorClocks = new HashMap<String, VectorClock>();
        HashMap<String, PNCounterImpl> counters = new HashMap<String, PNCounterImpl>();
        Config config = this.nodeEngine.getConfig();
        for (Map.Entry counterEntry : this.counters.entrySet()) {
            String counterName = (String)counterEntry.getKey();
            PNCounterImpl counter = (PNCounterImpl)counterEntry.getValue();
            if (targetIndex >= config.findPNCounterConfig(counterName).getReplicaCount()) continue;
            VectorClock counterCurrentVectorClock = counter.getCurrentVectorClock();
            VectorClock counterPreviousVectorClock = previouslyReplicatedVectorClocks.get(counterName);
            if (counterPreviousVectorClock == null || counterCurrentVectorClock.isAfter(counterPreviousVectorClock)) {
                counters.put(counterName, counter);
            }
            currentVectorClocks.put(counterName, counterCurrentVectorClock);
        }
        return counters.isEmpty() ? null : new CRDTReplicationContainer(new PNCounterReplicationOperation((Map<String, PNCounterImpl>)counters), currentVectorClocks);
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

    @Override
    public void merge(String name, PNCounterImpl value) {
        PNCounterImpl counter = this.getCounter(name);
        counter.merge(value);
        long counterValue = counter.get(null).getValue();
        this.getLocalPNCounterStats(name).setValue(counterValue);
    }

    @Override
    public CRDTReplicationContainer prepareMigrationOperation(int maxConfiguredReplicaCount) {
        HashMap<String, VectorClock> currentVectorClocks = new HashMap<String, VectorClock>();
        HashMap<String, PNCounterImpl> counters = new HashMap<String, PNCounterImpl>();
        Config config = this.nodeEngine.getConfig();
        for (Map.Entry counterEntry : this.counters.entrySet()) {
            String counterName = (String)counterEntry.getKey();
            PNCounterImpl counter = (PNCounterImpl)counterEntry.getValue();
            if (config.findPNCounterConfig(counterName).getReplicaCount() >= maxConfiguredReplicaCount) continue;
            counters.put(counterName, counter);
            currentVectorClocks.put(counterName, counter.getCurrentVectorClock());
        }
        return counters.isEmpty() ? null : new CRDTReplicationContainer(new PNCounterReplicationOperation((Map<String, PNCounterImpl>)counters), currentVectorClocks);
    }

    @Override
    public boolean clearCRDTState(Map<String, VectorClock> vectorClocks) {
        boolean allCleared = true;
        for (Map.Entry<String, VectorClock> vectorClockEntry : vectorClocks.entrySet()) {
            String counterName = vectorClockEntry.getKey();
            VectorClock vectorClock = vectorClockEntry.getValue();
            PNCounterImpl counter = (PNCounterImpl)this.counters.get(counterName);
            if (counter == null) continue;
            if (counter.markMigrated(vectorClock)) {
                this.counters.remove(counterName);
                this.statsMap.remove(counterName);
                continue;
            }
            allCleared = false;
        }
        return allCleared;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void prepareToSafeShutdown() {
        Iterator iterator = this.newCounterCreationMutex;
        synchronized (iterator) {
            this.isShuttingDown = true;
        }
        for (PNCounterImpl counter : this.counters.values()) {
            counter.markMigrated();
        }
    }

    @Override
    public String getQuorumName(String name) {
        return (String)this.quorumConfigCache.getOrCalculate(name);
    }

    @Override
    public Map<String, LocalPNCounterStats> getStats() {
        return this.unmodifiableStatsMap;
    }
}

