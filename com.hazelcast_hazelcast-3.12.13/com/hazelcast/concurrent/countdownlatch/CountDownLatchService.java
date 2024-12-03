/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchContainer;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchProxy;
import com.hazelcast.concurrent.countdownlatch.operations.CountDownLatchReplicationOperation;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.MigrationAwareService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionMigrationEvent;
import com.hazelcast.spi.PartitionReplicationEvent;
import com.hazelcast.spi.QuorumAwareService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.partition.MigrationEndpoint;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.ContextMutexFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CountDownLatchService
implements ManagedService,
RemoteService,
MigrationAwareService,
QuorumAwareService {
    public static final String SERVICE_NAME = "hz:impl:countDownLatchService";
    private static final Object NULL_OBJECT = new Object();
    private final ConcurrentMap<String, CountDownLatchContainer> containers = new ConcurrentHashMap<String, CountDownLatchContainer>();
    private NodeEngine nodeEngine;
    private final ConcurrentMap<String, Object> quorumConfigCache = new ConcurrentHashMap<String, Object>();
    private final ContextMutexFactory quorumConfigCacheMutexFactory = new ContextMutexFactory();
    private final ConstructorFunction<String, Object> quorumConfigConstructor = new ConstructorFunction<String, Object>(){

        @Override
        public Object createNew(String name) {
            CountDownLatchConfig countDownLatchConfig = CountDownLatchService.this.nodeEngine.getConfig().findCountDownLatchConfig(name);
            String quorumName = countDownLatchConfig.getQuorumName();
            return quorumName == null ? NULL_OBJECT : quorumName;
        }
    };

    public int getCount(String name) {
        CountDownLatchContainer latch = (CountDownLatchContainer)this.containers.get(name);
        return latch != null ? latch.getCount() : 0;
    }

    public boolean setCount(String name, int count) {
        if (count < 0) {
            this.containers.remove(name);
            return false;
        }
        CountDownLatchContainer latch = (CountDownLatchContainer)this.containers.get(name);
        if (latch == null) {
            latch = new CountDownLatchContainer(name);
            this.containers.put(name, latch);
        }
        return latch.setCount(count);
    }

    public void setCountDirect(String name, int count) {
        if (count < 0) {
            this.containers.remove(name);
        } else {
            CountDownLatchContainer latch = (CountDownLatchContainer)this.containers.get(name);
            if (latch == null) {
                latch = new CountDownLatchContainer(name);
                this.containers.put(name, latch);
            }
            latch.setCountDirect(count);
        }
    }

    public void countDown(String name) {
        CountDownLatchContainer latch = (CountDownLatchContainer)this.containers.get(name);
        if (latch != null && latch.countDown() == 0) {
            this.containers.remove(name);
        }
    }

    public boolean shouldWait(String name) {
        CountDownLatchContainer latch = (CountDownLatchContainer)this.containers.get(name);
        return latch != null && latch.getCount() > 0;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void reset() {
        this.containers.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.containers.clear();
    }

    @Override
    public CountDownLatchProxy createDistributedObject(String name) {
        return new CountDownLatchProxy(name, this.nodeEngine);
    }

    @Override
    public void destroyDistributedObject(String name) {
        this.containers.remove(name);
        this.quorumConfigCache.remove(name);
    }

    @Override
    public void beforeMigration(PartitionMigrationEvent partitionMigrationEvent) {
    }

    @Override
    public Operation prepareReplicationOperation(PartitionReplicationEvent event) {
        if (event.getReplicaIndex() > 1) {
            return null;
        }
        LinkedList<CountDownLatchContainer> data = new LinkedList<CountDownLatchContainer>();
        for (Map.Entry latchEntry : this.containers.entrySet()) {
            String name = (String)latchEntry.getKey();
            if (this.getPartitionId(name) != event.getPartitionId()) continue;
            CountDownLatchContainer value = (CountDownLatchContainer)latchEntry.getValue();
            data.add(value);
        }
        return data.isEmpty() ? null : new CountDownLatchReplicationOperation(data);
    }

    @Override
    public void commitMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.SOURCE) {
            int partitionId = event.getPartitionId();
            int thresholdReplicaIndex = event.getNewReplicaIndex();
            if (thresholdReplicaIndex == -1 || thresholdReplicaIndex > 1) {
                this.clearPartitionReplica(partitionId);
            }
        }
    }

    @Override
    public void rollbackMigration(PartitionMigrationEvent event) {
        if (event.getMigrationEndpoint() == MigrationEndpoint.DESTINATION) {
            int partitionId = event.getPartitionId();
            int thresholdReplicaIndex = event.getCurrentReplicaIndex();
            if (thresholdReplicaIndex == -1 || thresholdReplicaIndex > 1) {
                this.clearPartitionReplica(partitionId);
            }
        }
    }

    private int getPartitionId(String name) {
        String partitionKey = StringPartitioningStrategy.getPartitionKey(name);
        return this.nodeEngine.getPartitionService().getPartitionId(partitionKey);
    }

    private void clearPartitionReplica(int partitionId) {
        Iterator iter = this.containers.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            if (this.getPartitionId(name) != partitionId) continue;
            iter.remove();
        }
    }

    public CountDownLatchContainer getCountDownLatchContainer(String name) {
        return (CountDownLatchContainer)this.containers.get(name);
    }

    public boolean containsLatch(String name) {
        return this.containers.containsKey(name);
    }

    public void add(CountDownLatchContainer latch) {
        String name = latch.getName();
        this.containers.put(name, latch);
    }

    @Override
    public String getQuorumName(String name) {
        Object quorumName = ConcurrencyUtil.getOrPutSynchronized(this.quorumConfigCache, name, this.quorumConfigCacheMutexFactory, this.quorumConfigConstructor);
        return quorumName == NULL_OBJECT ? null : (String)quorumName;
    }
}

