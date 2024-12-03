/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.OperationControl;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CallsPerMember
implements LiveOperations {
    private final Address localAddress;
    private final Map<Address, CategorizedCallIds> callIdsByMember = new HashMap<Address, CategorizedCallIds>();

    public CallsPerMember(Address localAddress) {
        this.localAddress = Preconditions.checkNotNull(localAddress, "local address can't be null");
    }

    @Override
    public void add(Address address, long callId) {
        if (callId == 0L) {
            return;
        }
        if (address == null) {
            address = this.localAddress;
        }
        this.getOrCreateCallIdsForMember((Address)address).liveOps.add(callId);
    }

    public void addOpToCancel(Address address, long callId) {
        this.getOrCreateCallIdsForMember((Address)address).opsToCancel.add(callId);
    }

    public Set<Address> addresses() {
        return this.callIdsByMember.keySet();
    }

    public OperationControl toOpControl(Address address) {
        CategorizedCallIds callIds = this.callIdsByMember.get(address);
        if (callIds == null) {
            throw new IllegalArgumentException("Address not recognized as a member of this cluster: " + address);
        }
        return new OperationControl(CallsPerMember.toArray(callIds.liveOps), CallsPerMember.toArray(callIds.opsToCancel));
    }

    public void clear() {
        this.callIdsByMember.clear();
    }

    public void ensureMember(Address address) {
        this.getOrCreateCallIdsForMember(address);
    }

    public CategorizedCallIds getOrCreateCallIdsForMember(Address address) {
        CategorizedCallIds callIds = this.callIdsByMember.get(address);
        if (callIds == null) {
            callIds = new CategorizedCallIds();
            this.callIdsByMember.put(address, callIds);
        }
        return callIds;
    }

    private static long[] toArray(List<Long> longs) {
        long[] array = new long[longs.size()];
        for (int k = 0; k < array.length; ++k) {
            array[k] = longs.get(k);
        }
        return array;
    }

    private static final class CategorizedCallIds {
        final List<Long> liveOps = new ArrayList<Long>();
        final List<Long> opsToCancel = new ArrayList<Long>();

        private CategorizedCallIds() {
        }
    }
}

