/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicatedMapDataSerializerHook;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ClearOperation
extends AbstractNamedSerializableOperation
implements MutatingOperation {
    private String mapName;
    private boolean replicateClear;
    private long version;
    private transient int response;

    public ClearOperation() {
    }

    public ClearOperation(String mapName, boolean replicateClear) {
        this(mapName, replicateClear, 0L);
    }

    public ClearOperation(String mapName, boolean replicateClear, long version) {
        this.mapName = mapName;
        this.replicateClear = replicateClear;
        this.version = version;
    }

    @Override
    public void run() throws Exception {
        if (this.getNodeEngine().getLocalMember().isLiteMember()) {
            return;
        }
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        ReplicatedRecordStore store = service.getReplicatedRecordStore(this.mapName, false, this.getPartitionId());
        if (store == null) {
            return;
        }
        this.response = store.getStorage().size();
        if (this.replicateClear) {
            store.clear();
            this.replicateClearOperation(this.version);
        } else {
            store.clearWithVersion(this.version);
        }
    }

    private void replicateClearOperation(long version) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        Collection<Address> members = this.getMemberAddresses();
        for (Address address : members) {
            Operation op = new ClearOperation(this.mapName, false, version).setPartitionId(this.getPartitionId()).setValidateTarget(false);
            operationService.createInvocationBuilder(this.getServiceName(), op, address).setTryCount(3).invoke();
        }
    }

    protected Collection<Address> getMemberAddresses() {
        Address thisAddress = this.getNodeEngine().getThisAddress();
        Collection<Member> members = this.getNodeEngine().getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        ArrayList<Address> addresses = new ArrayList<Address>();
        for (Member member : members) {
            Address address = member.getAddress();
            if (address.equals(thisAddress)) continue;
            addresses.add(address);
        }
        return addresses;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:replicatedMapService";
    }

    @Override
    public int getFactoryId() {
        return ReplicatedMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.mapName);
        out.writeBoolean(this.replicateClear);
        out.writeLong(this.version);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mapName = in.readUTF();
        this.replicateClear = in.readBoolean();
        this.version = in.readLong();
    }

    @Override
    public String getName() {
        return this.mapName;
    }
}

