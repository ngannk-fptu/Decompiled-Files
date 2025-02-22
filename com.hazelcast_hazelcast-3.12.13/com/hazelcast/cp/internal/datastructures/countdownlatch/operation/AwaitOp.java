/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CallerAware;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.countdownlatch.AwaitInvocationKey;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AbstractCountDownLatchOp;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.UUID;

public class AwaitOp
extends AbstractCountDownLatchOp
implements CallerAware,
IndeterminateOperationStateAware {
    private UUID invocationUid;
    private long timeoutMillis;
    private Address callerAddress;
    private long callId;

    public AwaitOp() {
    }

    public AwaitOp(String name, UUID invocationUid, long timeoutMillis) {
        super(name);
        this.invocationUid = invocationUid;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        AwaitInvocationKey key;
        RaftCountDownLatchService service = (RaftCountDownLatchService)this.getService();
        if (service.await(groupId, this.name, key = new AwaitInvocationKey(commitIndex, this.invocationUid, this.callerAddress, this.callId), this.timeoutMillis)) {
            return true;
        }
        return this.timeoutMillis > 0L ? PostponedResponse.INSTANCE : Boolean.valueOf(false);
    }

    @Override
    public void setCaller(Address callerAddress, long callId) {
        this.callerAddress = callerAddress;
        this.callId = callId;
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        UUIDSerializationUtil.writeUUID(out, this.invocationUid);
        out.writeLong(this.timeoutMillis);
        out.writeObject(this.callerAddress);
        out.writeLong(this.callId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.invocationUid = UUIDSerializationUtil.readUUID(in);
        this.timeoutMillis = in.readLong();
        this.callerAddress = (Address)in.readObject();
        this.callId = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", invocationUid=").append(this.invocationUid).append(", timeoutMillis=").append(this.timeoutMillis).append(", callerAddress=").append(this.callerAddress).append(", callId=").append(this.callId);
    }
}

