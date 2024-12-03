/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.cp.internal.session.RaftSessionServiceDataSerializerHook;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CreateSessionOp
extends RaftOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Address endpoint;
    private String endpointName;
    private CPSession.CPSessionOwnerType endpointType;
    private long creationTime;

    public CreateSessionOp() {
    }

    public CreateSessionOp(Address endpoint, String endpointName, CPSession.CPSessionOwnerType endpointType) {
        this.endpoint = endpoint;
        this.endpointName = endpointName;
        this.endpointType = endpointType;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSessionService service = (RaftSessionService)this.getService();
        return service.createNewSession(groupId, this.endpoint, this.endpointName, this.endpointType);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public String getServiceName() {
        return "hz:core:raftSession";
    }

    @Override
    public int getFactoryId() {
        return RaftSessionServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.endpoint);
        boolean containsEndpointName = this.endpointName != null;
        out.writeBoolean(containsEndpointName);
        if (containsEndpointName) {
            out.writeUTF(this.endpointName);
        }
        out.writeUTF(this.endpointType.name());
        out.writeLong(this.creationTime);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.endpoint = (Address)in.readObject();
        boolean containsEndpointName = in.readBoolean();
        if (containsEndpointName) {
            this.endpointName = in.readUTF();
        }
        this.endpointType = CPSession.CPSessionOwnerType.valueOf(in.readUTF());
        this.creationTime = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", endpoint=").append(this.endpoint).append(", endpointName=").append(this.endpointName).append(", endpointType=").append((Object)this.endpointType);
    }
}

