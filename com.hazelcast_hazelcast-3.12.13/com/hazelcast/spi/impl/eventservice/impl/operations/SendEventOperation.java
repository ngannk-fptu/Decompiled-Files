/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.eventservice.impl.EventEnvelope;
import com.hazelcast.spi.impl.eventservice.impl.EventProcessor;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import java.io.IOException;

public class SendEventOperation
extends Operation
implements AllowedDuringPassiveState,
IdentifiedDataSerializable {
    private EventEnvelope eventEnvelope;
    private int orderKey;

    public SendEventOperation() {
    }

    public SendEventOperation(EventEnvelope eventEnvelope, int orderKey) {
        this.eventEnvelope = eventEnvelope;
        this.orderKey = orderKey;
    }

    @Override
    public void run() throws Exception {
        EventServiceImpl eventService = (EventServiceImpl)this.getNodeEngine().getEventService();
        eventService.executeEventCallback(new EventProcessor(eventService, this.eventEnvelope, this.orderKey));
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        this.eventEnvelope.writeData(out);
        out.writeInt(this.orderKey);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.eventEnvelope = new EventEnvelope();
        this.eventEnvelope.readData(in);
        this.orderKey = in.readInt();
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 13;
    }
}

