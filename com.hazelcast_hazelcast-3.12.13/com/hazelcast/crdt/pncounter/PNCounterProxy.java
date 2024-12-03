/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.crdt.pncounter.PNCounter;
import com.hazelcast.crdt.pncounter.PNCounterService;
import com.hazelcast.crdt.pncounter.operations.AddOperation;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.crdt.pncounter.operations.GetOperation;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class PNCounterProxy
extends AbstractDistributedObject<PNCounterService>
implements PNCounter {
    private static final AtomicReferenceFieldUpdater<PNCounterProxy, VectorClock> OBSERVED_TIMESTAMPS_UPDATER = AtomicReferenceFieldUpdater.newUpdater(PNCounterProxy.class, VectorClock.class, "observedClock");
    private static final List<Address> EMPTY_ADDRESS_LIST = Collections.emptyList();
    private final String name;
    private final ILogger logger;
    private volatile Address currentTargetReplicaAddress;
    private final Object targetSelectionMutex = new Object();
    private int operationTryCount = -1;
    private volatile VectorClock observedClock;

    PNCounterProxy(String name, NodeEngine nodeEngine, PNCounterService service) {
        super(nodeEngine, service);
        this.name = name;
        this.logger = nodeEngine.getLogger(PNCounterProxy.class);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }

    @Override
    public long get() {
        return this.invoke(new GetOperation(this.name, this.observedClock));
    }

    @Override
    public long getAndAdd(long delta) {
        return this.invoke(new AddOperation(this.name, delta, true, this.observedClock));
    }

    @Override
    public long addAndGet(long delta) {
        return this.invoke(new AddOperation(this.name, delta, false, this.observedClock));
    }

    @Override
    public long getAndSubtract(long delta) {
        return this.invoke(new AddOperation(this.name, -delta, true, this.observedClock));
    }

    @Override
    public long subtractAndGet(long delta) {
        return this.invoke(new AddOperation(this.name, -delta, false, this.observedClock));
    }

    @Override
    public long decrementAndGet() {
        return this.invoke(new AddOperation(this.name, -1L, false, this.observedClock));
    }

    @Override
    public long incrementAndGet() {
        return this.invoke(new AddOperation(this.name, 1L, false, this.observedClock));
    }

    @Override
    public long getAndDecrement() {
        return this.invoke(new AddOperation(this.name, -1L, true, this.observedClock));
    }

    @Override
    public long getAndIncrement() {
        return this.invoke(new AddOperation(this.name, 1L, true, this.observedClock));
    }

    @Override
    public void reset() {
        this.observedClock = null;
    }

    private long invoke(Operation operation) {
        if (this.getNodeEngine().getClusterService().getClusterVersion().isLessThan(Versions.V3_10)) {
            throw new UnsupportedOperationException("PNCounter operations are not supported when cluster version is less than 3.10");
        }
        return this.invokeInternal(operation, EMPTY_ADDRESS_LIST, null);
    }

    private long invokeInternal(Operation operation, List<Address> excludedAddresses, HazelcastException lastException) {
        Address target = this.getCRDTOperationTarget(excludedAddresses);
        if (target == null) {
            throw lastException != null ? lastException : new NoDataMemberInClusterException("Cannot invoke operations on a CRDT because the cluster does not contain any data members");
        }
        try {
            InvocationBuilder builder = this.getNodeEngine().getOperationService().createInvocationBuilder("hz:impl:PNCounterService", operation, target);
            if (this.operationTryCount > 0) {
                builder.setTryCount(this.operationTryCount);
            }
            InternalCompletableFuture future = builder.invoke();
            CRDTTimestampedLong result = (CRDTTimestampedLong)future.join();
            this.updateObservedReplicaTimestamps(result.getVectorClock());
            return result.getValue();
        }
        catch (HazelcastException e) {
            this.logger.fine("Exception occurred while invoking operation on target " + target + ", choosing different target", e);
            if (excludedAddresses == EMPTY_ADDRESS_LIST) {
                excludedAddresses = new ArrayList<Address>();
            }
            excludedAddresses.add(target);
            return this.invokeInternal(operation, excludedAddresses, e);
        }
    }

    private void updateObservedReplicaTimestamps(VectorClock receivedVectorClock) {
        VectorClock currentClock;
        while (!((currentClock = this.observedClock) != null && currentClock.isAfter(receivedVectorClock) || OBSERVED_TIMESTAMPS_UPDATER.compareAndSet(this, currentClock, receivedVectorClock))) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Address getCRDTOperationTarget(List<Address> excludedAddresses) {
        if (this.currentTargetReplicaAddress != null && !excludedAddresses.contains(this.currentTargetReplicaAddress)) {
            return this.currentTargetReplicaAddress;
        }
        Object object = this.targetSelectionMutex;
        synchronized (object) {
            if (this.currentTargetReplicaAddress == null || excludedAddresses.contains(this.currentTargetReplicaAddress)) {
                this.currentTargetReplicaAddress = this.chooseTargetReplica(excludedAddresses);
            }
        }
        return this.currentTargetReplicaAddress;
    }

    private Address chooseTargetReplica(List<Address> excludedAddresses) {
        List<Address> replicaAddresses = this.getReplicaAddresses(excludedAddresses);
        if (replicaAddresses.isEmpty()) {
            return null;
        }
        Address localAddress = this.getNodeEngine().getLocalMember().getAddress();
        if (replicaAddresses.contains(localAddress)) {
            return localAddress;
        }
        int randomReplicaIndex = ThreadLocalRandomProvider.get().nextInt(replicaAddresses.size());
        return replicaAddresses.get(randomReplicaIndex);
    }

    private List<Address> getReplicaAddresses(Collection<Address> excludedAddresses) {
        Collection<Member> dataMembers = this.getNodeEngine().getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        int maxConfiguredReplicaCount = this.getNodeEngine().getConfig().findPNCounterConfig(this.name).getReplicaCount();
        int currentReplicaCount = Math.min(maxConfiguredReplicaCount, dataMembers.size());
        ArrayList<Address> replicaAddresses = new ArrayList<Address>(currentReplicaCount);
        Iterator<Member> dataMemberIterator = dataMembers.iterator();
        for (int i = 0; i < currentReplicaCount; ++i) {
            Address dataMemberAddress = dataMemberIterator.next().getAddress();
            if (excludedAddresses.contains(dataMemberAddress)) continue;
            replicaAddresses.add(dataMemberAddress);
        }
        return replicaAddresses;
    }

    public Address getCurrentTargetReplicaAddress() {
        return this.currentTargetReplicaAddress;
    }

    public void setOperationTryCount(int operationTryCount) {
        this.operationTryCount = operationTryCount;
    }

    @Override
    public String toString() {
        return "PNCounter{name='" + this.name + "'}";
    }
}

