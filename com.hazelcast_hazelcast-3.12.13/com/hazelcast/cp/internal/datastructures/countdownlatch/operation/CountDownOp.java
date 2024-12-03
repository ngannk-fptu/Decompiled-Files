/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AbstractCountDownLatchOp;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;
import java.util.UUID;

public class CountDownOp
extends AbstractCountDownLatchOp
implements IndeterminateOperationStateAware {
    private UUID invocationUid;
    private int expectedRound;

    public CountDownOp() {
    }

    public CountDownOp(String name, UUID invocationUid, int expectedRound) {
        super(name);
        this.invocationUid = invocationUid;
        this.expectedRound = expectedRound;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftCountDownLatchService service = (RaftCountDownLatchService)this.getService();
        return service.countDown(groupId, this.name, this.invocationUid, this.expectedRound);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        UUIDSerializationUtil.writeUUID(out, this.invocationUid);
        out.writeInt(this.expectedRound);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.invocationUid = UUIDSerializationUtil.readUUID(in);
        this.expectedRound = in.readInt();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", invocationUid=").append(this.invocationUid).append(", expectedRound=").append(this.expectedRound);
    }
}

