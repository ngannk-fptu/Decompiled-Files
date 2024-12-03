/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.notification.MapReduceNotification;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class FireNotificationOperation
extends ProcessingOperation {
    private MapReduceNotification notification;

    public FireNotificationOperation() {
    }

    public FireNotificationOperation(MapReduceNotification notification) {
        super(notification.getName(), notification.getJobId());
        this.notification = notification;
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        mapReduceService.dispatchEvent(this.notification);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.notification);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.notification = (MapReduceNotification)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }
}

