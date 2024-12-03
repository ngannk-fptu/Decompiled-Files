/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public abstract class AbstractMultiMapOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
ServiceNamespaceAware,
IdentifiedDataSerializable {
    protected String name;
    protected transient Object response;
    private transient MultiMapContainer container;

    protected AbstractMultiMapOperation() {
    }

    protected AbstractMultiMapOperation(String name) {
        this.name = name;
    }

    @Override
    public final Object getResponse() {
        return this.response;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public String getName() {
        return this.name;
    }

    public final void publishEvent(EntryEventType eventType, Data key, Object newValue, Object oldValue) {
        MultiMapService multiMapService = (MultiMapService)this.getService();
        multiMapService.publishEntryEvent(this.name, eventType, key, newValue, oldValue);
    }

    public final Object toObject(Object obj) {
        return this.getNodeEngine().toObject(obj);
    }

    public final Data toData(Object obj) {
        return this.getNodeEngine().toData(obj);
    }

    public final MultiMapContainer getOrCreateContainer() {
        if (this.container == null) {
            MultiMapService service = (MultiMapService)this.getService();
            this.container = service.getOrCreateCollectionContainer(this.getPartitionId(), this.name);
        }
        return this.container;
    }

    public final MultiMapContainer getOrCreateContainerWithoutAccess() {
        if (this.container == null) {
            MultiMapService service = (MultiMapService)this.getService();
            this.container = service.getOrCreateCollectionContainerWithoutAccess(this.getPartitionId(), this.name);
        }
        return this.container;
    }

    public final MultiMapConfig.ValueCollectionType getValueCollectionType(MultiMapContainer container) {
        Preconditions.checkNotNull(container, "Argument container should not be null");
        MultiMapConfig config = container.getConfig();
        return config.getValueCollectionType();
    }

    public final boolean isBinary() {
        return this.getOrCreateContainer().getConfig().isBinary();
    }

    public final int getSyncBackupCount() {
        return this.getOrCreateContainer().getConfig().getBackupCount();
    }

    public final int getAsyncBackupCount() {
        return this.getOrCreateContainer().getConfig().getAsyncBackupCount();
    }

    @Override
    public ObjectNamespace getServiceNamespace() {
        MultiMapContainer container = this.getOrCreateContainerWithoutAccess();
        return container.getObjectNamespace();
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
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

