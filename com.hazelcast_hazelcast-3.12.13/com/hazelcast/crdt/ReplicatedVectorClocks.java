/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ReplicatedVectorClocks {
    private ConcurrentMap<ReplicatedVectorClockId, Map<String, VectorClock>> replicatedVectorClocks = new ConcurrentHashMap<ReplicatedVectorClockId, Map<String, VectorClock>>();

    ReplicatedVectorClocks() {
    }

    public Map<String, VectorClock> getReplicatedVectorClock(String serviceName, String memberUUID) {
        ReplicatedVectorClockId id = new ReplicatedVectorClockId(serviceName, memberUUID);
        Map<String, VectorClock> clocks = (Map<String, VectorClock>)this.replicatedVectorClocks.get(id);
        return clocks != null ? clocks : Collections.emptyMap();
    }

    public void setReplicatedVectorClocks(String serviceName, String memberUUID, Map<String, VectorClock> vectorClocks) {
        this.replicatedVectorClocks.put(new ReplicatedVectorClockId(serviceName, memberUUID), Collections.unmodifiableMap(vectorClocks));
    }

    public Map<String, VectorClock> getLatestReplicatedVectorClock(String serviceName) {
        HashMap<String, VectorClock> latestVectorClocks = new HashMap<String, VectorClock>();
        for (Map.Entry clockEntry : this.replicatedVectorClocks.entrySet()) {
            ReplicatedVectorClockId id = (ReplicatedVectorClockId)clockEntry.getKey();
            Map clock = (Map)clockEntry.getValue();
            if (!id.serviceName.equals(serviceName)) continue;
            for (Map.Entry crdtReplicatedClocks : clock.entrySet()) {
                String crdtName = (String)crdtReplicatedClocks.getKey();
                VectorClock vectorClock = (VectorClock)crdtReplicatedClocks.getValue();
                VectorClock latestVectorClock = latestVectorClocks.get(crdtName);
                if (latestVectorClock != null && !vectorClock.isAfter(latestVectorClock)) continue;
                latestVectorClocks.put(crdtName, vectorClock);
            }
        }
        return latestVectorClocks;
    }

    private static class ReplicatedVectorClockId {
        final String memberUUID;
        final String serviceName;

        ReplicatedVectorClockId(String serviceName, String memberUUID) {
            this.serviceName = Preconditions.checkNotNull(serviceName, "Service name must not be null");
            this.memberUUID = Preconditions.checkNotNull(memberUUID, "Member UUID must not be null");
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReplicatedVectorClockId)) return false;
            ReplicatedVectorClockId that = (ReplicatedVectorClockId)o;
            if (!this.serviceName.equals(that.serviceName)) return false;
            if (!this.memberUUID.equals(that.memberUUID)) return false;
            return true;
        }

        public int hashCode() {
            int result = this.memberUUID.hashCode();
            result = 31 * result + this.serviceName.hashCode();
            return result;
        }
    }
}

