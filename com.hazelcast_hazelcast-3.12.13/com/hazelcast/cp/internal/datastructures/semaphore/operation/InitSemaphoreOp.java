/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.semaphore.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.semaphore.RaftSemaphoreService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class InitSemaphoreOp
extends RaftOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private String name;
    private int permits;

    public InitSemaphoreOp() {
    }

    public InitSemaphoreOp(String name, int permits) {
        this.name = name;
        this.permits = permits;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftSemaphoreService service = (RaftSemaphoreService)this.getService();
        return service.initSemaphore(groupId, this.name, this.permits);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return false;
    }

    @Override
    protected String getServiceName() {
        return "hz:raft:semaphoreService";
    }

    @Override
    public int getFactoryId() {
        return RaftSemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.permits);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.permits = in.readInt();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", name=").append(this.name).append(", permits=").append(this.permits);
    }
}

