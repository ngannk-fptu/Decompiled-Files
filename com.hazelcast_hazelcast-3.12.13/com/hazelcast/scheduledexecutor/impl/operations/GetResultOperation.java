/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorWaitNotifyKey;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class GetResultOperation<V>
extends AbstractSchedulerOperation
implements BlockingOperation,
ReadonlyOperation {
    private String taskName;
    private ScheduledTaskHandler handler;
    private Object result;

    public GetResultOperation() {
    }

    public GetResultOperation(ScheduledTaskHandler handler) {
        super(handler.getSchedulerName());
        this.taskName = handler.getTaskName();
        this.handler = handler;
    }

    @Override
    public void run() throws Exception {
        this.result = this.getContainer().get(this.taskName);
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new ScheduledExecutorWaitNotifyKey(this.getSchedulerName(), this.handler.toUrn());
    }

    @Override
    public boolean shouldWait() {
        return this.getContainer().shouldParkGetResult(this.taskName);
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(new HazelcastException());
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.taskName);
        out.writeUTF(this.handler.toUrn());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.taskName = in.readUTF();
        this.handler = ScheduledTaskHandler.of(in.readUTF());
    }
}

