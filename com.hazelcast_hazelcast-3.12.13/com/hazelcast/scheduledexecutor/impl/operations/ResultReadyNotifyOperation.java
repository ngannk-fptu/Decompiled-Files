/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorWaitNotifyKey;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.WaitNotifyKey;
import java.io.IOException;

public class ResultReadyNotifyOperation<V>
extends AbstractSchedulerOperation
implements Notifier {
    private ScheduledTaskHandler handler;

    public ResultReadyNotifyOperation() {
    }

    public ResultReadyNotifyOperation(ScheduledTaskHandler handler) {
        super(handler.getSchedulerName());
        this.handler = handler;
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }

    @Override
    public WaitNotifyKey getNotifiedKey() {
        return new ScheduledExecutorWaitNotifyKey(this.getSchedulerName(), this.handler.toUrn());
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.handler.toUrn());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.handler = ScheduledTaskHandler.of(in.readUTF());
    }
}

