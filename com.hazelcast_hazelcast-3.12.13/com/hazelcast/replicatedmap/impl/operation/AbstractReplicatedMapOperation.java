/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.operation.AbstractNamedSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateOperation;
import com.hazelcast.replicatedmap.impl.operation.ReplicateUpdateToCallerOperation;
import com.hazelcast.replicatedmap.impl.operation.VersionResponsePair;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractReplicatedMapOperation
extends AbstractNamedSerializableOperation {
    protected String name;
    protected Data key;
    protected Data value;
    protected long ttl;
    protected transient VersionResponsePair response;

    protected void sendReplicationOperation(boolean isRemove) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        Collection<Address> members = this.getMemberAddresses();
        for (Address address : members) {
            this.invoke(isRemove, operationService, address, this.name, this.key, this.value, this.ttl, this.response);
        }
    }

    protected Collection<Address> getMemberAddresses() {
        Address thisAddress = this.getNodeEngine().getThisAddress();
        Collection<Member> members = this.getNodeEngine().getClusterService().getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
        ArrayList<Address> addresses = new ArrayList<Address>();
        for (Member member : members) {
            Address address = member.getAddress();
            if (address.equals(this.getCallerAddress()) || address.equals(thisAddress)) continue;
            addresses.add(address);
        }
        return addresses;
    }

    private void invoke(boolean isRemove, OperationService operationService, Address address, String name, Data key, Data value, long ttl, VersionResponsePair response) {
        Operation op = new ReplicateUpdateOperation(name, key, value, ttl, response, isRemove, this.getCallerAddress()).setPartitionId(this.getPartitionId()).setValidateTarget(false);
        operationService.createInvocationBuilder(this.getServiceName(), op, address).setTryCount(3).invoke();
    }

    protected void sendUpdateCallerOperation(boolean isRemove) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        Operation op = new ReplicateUpdateToCallerOperation(this.name, this.getCallId(), this.key, this.value, this.response, this.ttl, isRemove).setPartitionId(this.getPartitionId()).setValidateTarget(false).setServiceName(this.getServiceName());
        operationService.createInvocationBuilder(this.getServiceName(), op, this.getCallerAddress()).setTryCount(3).invoke();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getResponse() {
        if (this.getNodeEngine().getThisAddress().equals(this.getCallerAddress())) {
            return this.response;
        }
        return new NormalResponse(this.response, this.getCallId(), 1, this.isUrgent());
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

