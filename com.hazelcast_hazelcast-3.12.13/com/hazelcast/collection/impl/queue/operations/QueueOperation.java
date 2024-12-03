/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.QueueEvent;
import com.hazelcast.collection.impl.queue.QueueEventFilter;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import java.util.Collection;

public abstract class QueueOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
NamedOperation {
    protected transient Object response;
    private transient QueueContainer container;

    protected QueueOperation() {
    }

    protected QueueOperation(String name) {
        super(name);
    }

    protected QueueOperation(String name, long timeoutMillis) {
        this(name);
        this.setWaitTimeout(timeoutMillis);
    }

    protected final QueueContainer getContainer() {
        return this.container;
    }

    private void initializeContainer() {
        QueueService queueService = (QueueService)this.getService();
        try {
            this.container = queueService.getOrCreateContainer(this.name, this instanceof BackupOperation);
        }
        catch (Exception e) {
            throw new RetryableHazelcastException(e);
        }
    }

    @Override
    public final Object getResponse() {
        return this.response;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public void afterRun() throws Exception {
    }

    @Override
    public void beforeRun() throws Exception {
        this.initializeContainer();
    }

    public boolean hasListener() {
        EventService eventService = this.getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(this.getServiceName(), this.name);
        return registrations.size() > 0;
    }

    public void publishEvent(ItemEventType eventType, Data data) {
        EventService eventService = this.getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(this.getServiceName(), this.name);
        Address thisAddress = this.getNodeEngine().getThisAddress();
        for (EventRegistration registration : registrations) {
            QueueEventFilter filter = (QueueEventFilter)registration.getFilter();
            QueueEvent event = new QueueEvent(this.name, filter.isIncludeValue() ? data : null, eventType, thisAddress);
            eventService.publishEvent(this.getServiceName(), registration, (Object)event, this.name.hashCode());
        }
    }

    protected QueueService getQueueService() {
        return (QueueService)this.getService();
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }
}

