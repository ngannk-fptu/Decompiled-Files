/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.ReplicatedMapEventPublishingService;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.client.ReplicatedMapEntries;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.partition.IPartitionService;
import java.io.IOException;
import java.util.Collection;

public class PutAllOperation
extends AbstractNamedSerializableOperation
implements MutatingOperation {
    private String name;
    private ReplicatedMapEntries entries;

    public PutAllOperation() {
    }

    public PutAllOperation(String name, ReplicatedMapEntries entries) {
        this.name = name;
        this.entries = entries;
    }

    @Override
    public void run() throws Exception {
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.name, true, this.getPartitionId());
        int partitionId = this.getPartitionId();
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        ReplicatedMapEventPublishingService eventPublishingService = service.getEventPublishingService();
        for (int i = 0; i < this.entries.size(); ++i) {
            Data key = this.entries.getKey(i);
            Data value = this.entries.getValue(i);
            if (partitionId != partitionService.getPartitionId(key)) continue;
            Object putResult = store.put(key, value);
            Data oldValue = this.getNodeEngine().toData(putResult);
            eventPublishingService.fireEntryListenerEvent(key, oldValue, value, this.name, this.getCallerAddress());
            VersionResponsePair response = new VersionResponsePair(putResult, store.getVersion());
            this.publishReplicationMessage(key, value, response);
        }
    }

    private void publishReplicationMessage(Data key, Data value, VersionResponsePair response) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        Collection<Member> members = this.getNodeEngine().getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        for (Member member : members) {
            Address address = member.getAddress();
            if (address.equals(this.getNodeEngine().getThisAddress())) continue;
            Operation op = new ReplicateUpdateOperation(this.name, key, value, 0L, response, false, this.getCallerAddress()).setPartitionId(this.getPartitionId()).setValidateTarget(false);
            operationService.invokeOnTarget(this.getServiceName(), op, address);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeObject(this.entries);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.entries = (ReplicatedMapEntries)in.readObject();
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

