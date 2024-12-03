/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchContainer;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchDataSerializerHook;
import com.hazelcast.concurrent.countdownlatch.CountDownLatchService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class CountDownLatchReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Collection<CountDownLatchContainer> data;

    public CountDownLatchReplicationOperation() {
    }

    public CountDownLatchReplicationOperation(Collection<CountDownLatchContainer> data) {
        this.data = data;
    }

    @Override
    public void run() throws Exception {
        if (this.data == null) {
            return;
        }
        CountDownLatchService service = (CountDownLatchService)this.getService();
        for (CountDownLatchContainer container : this.data) {
            service.add(container);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public int getFactoryId() {
        return CountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int len = this.data != null ? this.data.size() : 0;
        out.writeInt(len);
        if (len > 0) {
            for (CountDownLatchContainer container : this.data) {
                container.writeData(out);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        if (len > 0) {
            this.data = new ArrayList<CountDownLatchContainer>(len);
            for (int i = 0; i < len; ++i) {
                CountDownLatchContainer container = new CountDownLatchContainer();
                container.readData(in);
                this.data.add(container);
            }
        }
    }
}

