/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceSegment;
import com.hazelcast.spi.impl.eventservice.impl.operations.AbstractRegistrationOperation;
import java.io.IOException;

public class DeregistrationOperation
extends AbstractRegistrationOperation {
    private String topic;
    private String id;

    public DeregistrationOperation() {
    }

    public DeregistrationOperation(String topic, String id, int memberListVersion) {
        super(memberListVersion);
        this.topic = topic;
        this.id = id;
    }

    @Override
    protected void runInternal() throws Exception {
        EventServiceImpl eventService = (EventServiceImpl)this.getNodeEngine().getEventService();
        EventServiceSegment segment = eventService.getSegment(this.getServiceName(), false);
        if (segment != null) {
            segment.removeRegistration(this.topic, this.id);
        }
    }

    @Override
    public Object getResponse() {
        return true;
    }

    @Override
    protected void writeInternalImpl(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.topic);
        out.writeUTF(this.id);
    }

    @Override
    protected void readInternalImpl(ObjectDataInput in) throws IOException {
        this.topic = in.readUTF();
        this.id = in.readUTF();
    }

    @Override
    public int getId() {
        return 10;
    }
}

