/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.RaftDataServiceDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class DestroyRaftObjectOp
extends RaftOp
implements IdentifiedDataSerializable {
    private String serviceName;
    private String objectName;

    public DestroyRaftObjectOp() {
    }

    public DestroyRaftObjectOp(String serviceName, String objectName) {
        this.serviceName = serviceName;
        this.objectName = objectName;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftRemoteService service = (RaftRemoteService)this.getService();
        return service.destroyRaftObject(groupId, this.objectName);
    }

    @Override
    protected String getServiceName() {
        return this.serviceName;
    }

    @Override
    public int getFactoryId() {
        return RaftDataServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.serviceName);
        out.writeUTF(this.objectName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.serviceName = in.readUTF();
        this.objectName = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", serviceName=").append(this.serviceName).append(", objectName=").append(this.objectName);
    }
}

