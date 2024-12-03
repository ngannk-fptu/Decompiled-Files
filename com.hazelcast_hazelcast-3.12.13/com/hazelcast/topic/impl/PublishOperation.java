/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.topic.impl.TopicDataSerializerHook;
import com.hazelcast.topic.impl.TopicEvent;
import com.hazelcast.topic.impl.TopicService;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

public class PublishOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable {
    private Data message;

    public PublishOperation() {
    }

    public PublishOperation(String name, Data message) {
        super(name);
        this.message = message;
    }

    @Override
    public void beforeRun() throws Exception {
        TopicService service = (TopicService)this.getService();
        service.incrementPublishes(this.name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() throws Exception {
        TopicService service = (TopicService)this.getService();
        TopicEvent topicEvent = new TopicEvent(this.name, this.message, this.getCallerAddress());
        EventService eventService = this.getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:impl:topicService", this.name);
        Lock lock = service.getOrderLock(this.name);
        lock.lock();
        try {
            eventService.publishEvent("hz:impl:topicService", registrations, (Object)topicEvent, this.name.hashCode());
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public int getFactoryId() {
        return TopicDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:topicService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeData(this.message);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.message = in.readData();
    }
}

