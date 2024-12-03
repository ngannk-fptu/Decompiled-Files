/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection.operations;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.collection.impl.collection.CollectionEvent;
import com.hazelcast.collection.impl.collection.CollectionEventFilter;
import com.hazelcast.collection.impl.collection.CollectionService;
import com.hazelcast.collection.impl.list.ListContainer;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.core.ItemEventType;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import java.io.IOException;
import java.util.Collection;

public abstract class CollectionOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
IdentifiedDataSerializable {
    protected String name;
    protected transient Object response;
    private transient CollectionContainer container;

    protected CollectionOperation() {
    }

    protected CollectionOperation(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected final ListContainer getOrCreateListContainer() {
        if (this.container == null) {
            ListService service = (ListService)this.getService();
            try {
                this.container = service.getOrCreateContainer(this.name, this instanceof BackupOperation);
            }
            catch (Exception e) {
                throw new RetryableHazelcastException(e);
            }
        }
        return (ListContainer)this.container;
    }

    protected final CollectionContainer getOrCreateContainer() {
        if (this.container == null) {
            CollectionService service = (CollectionService)this.getService();
            try {
                this.container = service.getOrCreateContainer(this.name, this instanceof BackupOperation);
            }
            catch (Exception e) {
                throw new RetryableHazelcastException(e);
            }
        }
        return this.container;
    }

    protected void publishEvent(ItemEventType eventType, Data data) {
        EventService eventService = this.getNodeEngine().getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations(this.getServiceName(), this.name);
        Address address = this.getNodeEngine().getThisAddress();
        for (EventRegistration registration : registrations) {
            CollectionEventFilter filter = (CollectionEventFilter)registration.getFilter();
            boolean includeValue = filter.isIncludeValue();
            CollectionEvent event = new CollectionEvent(this.name, includeValue ? data : null, eventType, address);
            eventService.publishEvent(this.getServiceName(), registration, (Object)event, this.name.hashCode());
        }
    }

    public boolean hasEnoughCapacity(int delta) {
        return this.getOrCreateContainer().hasEnoughCapacity(delta);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

