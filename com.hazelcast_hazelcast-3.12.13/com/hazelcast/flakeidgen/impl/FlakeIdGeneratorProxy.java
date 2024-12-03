/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.flakeidgen.impl.AutoBatcher;
import com.hazelcast.flakeidgen.impl.FlakeIdGeneratorService;
import com.hazelcast.flakeidgen.impl.IdBatch;
import com.hazelcast.flakeidgen.impl.NewIdBatchOperation;
import com.hazelcast.flakeidgen.impl.NodeIdOutOfRangeException;
import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FlakeIdGeneratorProxy
extends AbstractDistributedObject<FlakeIdGeneratorService>
implements FlakeIdGenerator {
    public static final int BITS_TIMESTAMP = 41;
    public static final int BITS_SEQUENCE = 6;
    public static final int BITS_NODE_ID = 16;
    public static final long INCREMENT = 65536L;
    static final long ALLOWED_FUTURE_MILLIS = 15000L;
    static final long EPOCH_START = 1514764800000L;
    static final long NODE_ID_UPDATE_INTERVAL_NS = TimeUnit.SECONDS.toNanos(2L);
    private static final int NODE_ID_NOT_YET_SET = -1;
    private static final int NODE_ID_OUT_OF_RANGE = -2;
    private final String name;
    private final long epochStart;
    private final long nodeIdOffset;
    private volatile int nodeId = -1;
    private volatile long nextNodeIdUpdate = Long.MIN_VALUE;
    private final ILogger logger;
    private final AtomicLong generatedValue = new AtomicLong(Long.MIN_VALUE);
    private volatile Member randomMember;
    private AutoBatcher batcher;
    private final Set<String> outOfRangeMembers = Collections.newSetFromMap(new ConcurrentHashMap());

    FlakeIdGeneratorProxy(String name, NodeEngine nodeEngine, FlakeIdGeneratorService service) {
        super(nodeEngine, service);
        this.name = name;
        this.logger = nodeEngine.getLogger(this.getClass());
        FlakeIdGeneratorConfig config = nodeEngine.getConfig().findFlakeIdGeneratorConfig(this.getName());
        this.epochStart = 1514764800000L - (config.getIdOffset() >> 22);
        this.nodeIdOffset = config.getNodeIdOffset();
        this.batcher = new AutoBatcher(config.getPrefetchCount(), config.getPrefetchValidityMillis(), new AutoBatcher.IdBatchSupplier(){

            @Override
            public IdBatch newIdBatch(int batchSize) {
                IdBatchAndWaitTime result = FlakeIdGeneratorProxy.this.newIdBatch(batchSize);
                if (result.waitTimeMillis > 0L) {
                    try {
                        Thread.sleep(result.waitTimeMillis);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw ExceptionUtil.rethrow(e);
                    }
                }
                return result.idBatch;
            }
        });
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Created FlakeIdGeneratorProxy, name='" + name + "'");
        }
    }

    @Override
    public long newId() {
        return this.batcher.newId();
    }

    @Override
    public boolean init(long id) {
        long reserve = TimeUnit.HOURS.toMillis(1L) << 22;
        return this.newId() >= id + reserve;
    }

    public IdBatchAndWaitTime newIdBatch(int batchSize) {
        int nodeId = this.getNodeId();
        if (nodeId >= 0) {
            return this.newIdBaseLocal(Clock.currentTimeMillis(), nodeId, batchSize);
        }
        while (true) {
            NewIdBatchOperation op = new NewIdBatchOperation(this.name, batchSize);
            Member target = this.getRandomMember();
            InternalCompletableFuture future = this.getNodeEngine().getOperationService().invokeOnTarget(this.getServiceName(), op, target.getAddress());
            try {
                long base = (Long)future.join();
                return new IdBatchAndWaitTime(new IdBatch(base, 65536L, batchSize), 0L);
            }
            catch (NodeIdOutOfRangeException e) {
                this.outOfRangeMembers.add(target.getUuid());
                this.randomMember = null;
                continue;
            }
            break;
        }
    }

    IdBatchAndWaitTime newIdBaseLocal(int batchSize) {
        return this.newIdBaseLocal(Clock.currentTimeMillis(), this.getNodeId(), batchSize);
    }

    IdBatchAndWaitTime newIdBaseLocal(long now, int nodeId, int batchSize) {
        long base;
        long oldGeneratedValue;
        Preconditions.checkPositive(batchSize, "batchSize");
        if (nodeId == -2) {
            throw new NodeIdOutOfRangeException("NodeID overflow, this member cannot generate IDs");
        }
        assert ((nodeId & 0xFFFF0000) == 0) : "nodeId out of range: " + nodeId;
        if ((now -= this.epochStart) < -2199023255552L || now >= 0x20000000000L) {
            throw new HazelcastException("Current time out of allowed range");
        }
        now <<= 6;
        while (!this.generatedValue.compareAndSet(oldGeneratedValue = this.generatedValue.get(), (base = Math.max(now, oldGeneratedValue)) + (long)batchSize)) {
        }
        long waitTime = Math.max(0L, (base + (long)batchSize - now >> 6) - 15000L);
        base = base << 16 | (long)nodeId;
        ((FlakeIdGeneratorService)this.getService()).updateStatsForBatch(this.name, batchSize);
        return new IdBatchAndWaitTime(new IdBatch(base, 65536L, batchSize), waitTime);
    }

    private int getNodeId() {
        return this.getNodeId(System.nanoTime());
    }

    int getNodeId(long nanoTime) {
        int nodeId = this.nodeId;
        if (nodeId != -2 && this.nextNodeIdUpdate <= nanoTime) {
            int newNodeId = this.getNodeEngine().getClusterService().getMemberListJoinVersion();
            assert (newNodeId >= 0) : "newNodeId=" + newNodeId;
            newNodeId = (int)((long)newNodeId + this.nodeIdOffset);
            this.nextNodeIdUpdate = nanoTime + NODE_ID_UPDATE_INTERVAL_NS;
            if (newNodeId != nodeId) {
                nodeId = newNodeId;
                if ((nodeId & 0xFFFF0000) != 0) {
                    this.outOfRangeMembers.add(this.getNodeEngine().getClusterService().getLocalMember().getUuid());
                    this.logger.severe("Node ID is out of range (" + nodeId + "), this member won't be able to generate IDs. Cluster restart is recommended.");
                    nodeId = -2;
                }
                this.nodeId = nodeId;
                if (this.logger.isFineEnabled()) {
                    this.logger.fine("Node ID assigned to '" + this.name + "': " + nodeId);
                }
            }
        }
        return nodeId;
    }

    private Member getRandomMember() {
        Member member = this.randomMember;
        if (member == null) {
            Set<Member> members = this.getNodeEngine().getClusterService().getMembers();
            ArrayList<Member> filteredMembers = new ArrayList<Member>(members.size());
            for (Member m : members) {
                if (this.outOfRangeMembers.contains(m.getUuid())) continue;
                filteredMembers.add(m);
            }
            if (filteredMembers.isEmpty()) {
                throw new HazelcastException("All members have node ID out of range. Cluster restart is required");
            }
            this.randomMember = member = (Member)filteredMembers.get(ThreadLocalRandomProvider.get().nextInt(filteredMembers.size()));
        }
        return member;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:flakeIdGeneratorService";
    }

    public static class IdBatchAndWaitTime {
        public final IdBatch idBatch;
        public final long waitTimeMillis;

        IdBatchAndWaitTime(IdBatch idBatch, long waitTimeMillis) {
            this.idBatch = idBatch;
            this.waitTimeMillis = waitTimeMillis;
        }
    }
}

