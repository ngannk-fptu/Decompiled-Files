/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.core.Member;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.core.Partition;
import com.hazelcast.core.PartitionService;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.SafeStateCheckOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class PartitionServiceProxy
implements PartitionService {
    private final NodeEngineImpl nodeEngine;
    private final InternalPartitionServiceImpl partitionService;
    private final Map<Integer, Partition> partitionMap;
    private final Set<Partition> partitionSet;
    private final Random random = new Random();
    private final ILogger logger;
    private final FutureUtil.ExceptionHandler exceptionHandler = new FutureUtil.ExceptionHandler(){

        @Override
        public void handleException(Throwable e) {
            PartitionServiceProxy.this.logger.warning("Error while querying cluster's safe state", e);
        }
    };

    public PartitionServiceProxy(NodeEngineImpl nodeEngine, InternalPartitionServiceImpl partitionService) {
        this.nodeEngine = nodeEngine;
        this.partitionService = partitionService;
        int partitionCount = partitionService.getPartitionCount();
        Map<Integer, PartitionProxy> map = MapUtil.createHashMap(partitionCount);
        TreeSet<PartitionProxy> set = new TreeSet<PartitionProxy>();
        for (int i = 0; i < partitionCount; ++i) {
            PartitionProxy partition = new PartitionProxy(i);
            set.add(partition);
            map.put(i, partition);
        }
        this.partitionMap = Collections.unmodifiableMap(map);
        this.partitionSet = Collections.unmodifiableSet(set);
        this.logger = nodeEngine.getLogger(PartitionServiceProxy.class);
    }

    @Override
    public String randomPartitionKey() {
        return Integer.toString(this.random.nextInt(this.partitionService.getPartitionCount()));
    }

    @Override
    public Set<Partition> getPartitions() {
        return this.partitionSet;
    }

    @Override
    public Partition getPartition(Object key) {
        int partitionId = this.partitionService.getPartitionId(key);
        return this.partitionMap.get(partitionId);
    }

    @Override
    public String addMigrationListener(MigrationListener migrationListener) {
        return this.partitionService.addMigrationListener(migrationListener);
    }

    @Override
    public boolean removeMigrationListener(String registrationId) {
        return this.partitionService.removeMigrationListener(registrationId);
    }

    @Override
    public String addPartitionLostListener(PartitionLostListener partitionLostListener) {
        return this.partitionService.addPartitionLostListener(partitionLostListener);
    }

    @Override
    public boolean removePartitionLostListener(String registrationId) {
        return this.partitionService.removePartitionLostListener(registrationId);
    }

    @Override
    public boolean isClusterSafe() {
        Set<Member> members = this.nodeEngine.getClusterService().getMembers();
        if (members == null || members.isEmpty()) {
            return true;
        }
        ArrayList futures = new ArrayList(members.size());
        for (Member member : members) {
            Address target = member.getAddress();
            SafeStateCheckOperation operation = new SafeStateCheckOperation();
            InternalCompletableFuture future = this.nodeEngine.getOperationService().invokeOnTarget("hz:core:partitionService", operation, target);
            futures.add(future);
        }
        int maxWaitTime = this.getMaxWaitTime();
        Collection<Boolean> results = FutureUtil.returnWithDeadline(futures, maxWaitTime, TimeUnit.SECONDS, this.exceptionHandler);
        if (results.size() != futures.size()) {
            return false;
        }
        for (Boolean result : results) {
            if (result.booleanValue()) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isMemberSafe(Member member) {
        boolean safe;
        if (member == null) {
            throw new NullPointerException("Parameter member should not be null");
        }
        MemberImpl localMember = this.nodeEngine.getLocalMember();
        if (((Object)localMember).equals(member)) {
            return this.isLocalMemberSafe();
        }
        Address target = member.getAddress();
        SafeStateCheckOperation operation = new SafeStateCheckOperation();
        InternalCompletableFuture future = this.nodeEngine.getOperationService().invokeOnTarget("hz:core:partitionService", operation, target);
        try {
            Object result = future.get(10L, TimeUnit.SECONDS);
            safe = (Boolean)result;
        }
        catch (Throwable t) {
            safe = false;
            this.logger.warning("Error while querying member's safe state [" + member + "]", t);
        }
        return safe;
    }

    @Override
    public boolean isLocalMemberSafe() {
        if (!this.nodeActive()) {
            return true;
        }
        return this.partitionService.isMemberStateSafe();
    }

    @Override
    public boolean forceLocalMemberToBeSafe(long timeout, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException();
        }
        if (timeout < 1L) {
            throw new IllegalArgumentException();
        }
        if (!this.nodeActive()) {
            return true;
        }
        return this.partitionService.getPartitionReplicaStateChecker().triggerAndWaitForReplicaSync(timeout, unit);
    }

    private boolean nodeActive() {
        return this.nodeEngine.getNode().getState() != NodeState.SHUT_DOWN;
    }

    private int getMaxWaitTime() {
        return this.nodeEngine.getProperties().getSeconds(GroupProperty.GRACEFUL_SHUTDOWN_MAX_WAIT);
    }

    private class PartitionProxy
    implements Partition,
    Comparable {
        final int partitionId;

        PartitionProxy(int partitionId) {
            this.partitionId = partitionId;
        }

        @Override
        public int getPartitionId() {
            return this.partitionId;
        }

        @Override
        public Member getOwner() {
            Address address = PartitionServiceProxy.this.partitionService.getPartitionOwner(this.partitionId);
            if (address == null) {
                return null;
            }
            return PartitionServiceProxy.this.nodeEngine.getClusterService().getMember(address);
        }

        public int compareTo(Object o) {
            PartitionProxy partition = (PartitionProxy)o;
            int otherPartitionId = partition.partitionId;
            return this.partitionId < otherPartitionId ? -1 : (this.partitionId == otherPartitionId ? 0 : 1);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            PartitionProxy partition = (PartitionProxy)o;
            return this.partitionId == partition.partitionId;
        }

        public int hashCode() {
            return this.partitionId;
        }

        public String toString() {
            return "Partition [" + this.partitionId + "], owner=" + this.getOwner();
        }
    }
}

