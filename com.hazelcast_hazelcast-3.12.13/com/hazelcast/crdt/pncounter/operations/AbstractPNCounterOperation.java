/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter.operations;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.ConsistencyLostException;
import com.hazelcast.core.Member;
import com.hazelcast.crdt.CRDTDataSerializerHook;
import com.hazelcast.crdt.TargetNotReplicaException;
import com.hazelcast.crdt.pncounter.PNCounterImpl;
import com.hazelcast.crdt.pncounter.PNCounterService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractPNCounterOperation
extends Operation
implements IdentifiedDataSerializable,
NamedOperation {
    protected String name;
    private PNCounterImpl counter;

    AbstractPNCounterOperation() {
    }

    AbstractPNCounterOperation(String name) {
        this.name = name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:PNCounterService";
    }

    PNCounterImpl getPNCounter(VectorClock observedTimestamps) {
        if (this.counter != null) {
            return this.counter;
        }
        PNCounterService service = (PNCounterService)this.getService();
        if (observedTimestamps != null && !observedTimestamps.isEmpty() && !service.containsCounter(this.name)) {
            throw new ConsistencyLostException("This replica cannot provide the session guarantees for the PN counter since it's state is stale");
        }
        int maxConfiguredReplicaCount = this.getNodeEngine().getConfig().findPNCounterConfig(this.name).getReplicaCount();
        if (!this.isCRDTReplica(maxConfiguredReplicaCount)) {
            throw new TargetNotReplicaException("This member is not a CRDT replica for the " + this.name + " + PN counter");
        }
        this.counter = service.getCounter(this.name);
        return this.counter;
    }

    private boolean isCRDTReplica(int configuredReplicaCount) {
        Collection<Member> dataMembers = this.getNodeEngine().getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        Iterator<Member> dataMemberIterator = dataMembers.iterator();
        Address thisAddress = this.getNodeEngine().getThisAddress();
        for (int i = 0; i < Math.min(configuredReplicaCount, dataMembers.size()); ++i) {
            Address dataMemberAddress = dataMemberIterator.next().getAddress();
            if (!thisAddress.equals(dataMemberAddress)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }

    @Override
    public int getFactoryId() {
        return CRDTDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }
}

