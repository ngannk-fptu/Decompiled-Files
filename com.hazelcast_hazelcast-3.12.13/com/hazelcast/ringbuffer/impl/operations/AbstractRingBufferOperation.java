/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.RingbufferDataSerializerHook;
import com.hazelcast.ringbuffer.impl.RingbufferService;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespaceAware;
import java.io.IOException;

public abstract class AbstractRingBufferOperation
extends Operation
implements NamedOperation,
IdentifiedDataSerializable,
PartitionAwareOperation,
ServiceNamespaceAware {
    protected String name;

    public AbstractRingBufferOperation() {
    }

    public AbstractRingBufferOperation(String name) {
        this.name = name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:ringbufferService";
    }

    @Override
    public String getName() {
        return this.name;
    }

    RingbufferContainer getRingBufferContainer() {
        RingbufferService service = (RingbufferService)this.getService();
        ObjectNamespace ns = RingbufferService.getRingbufferNamespace(this.name);
        RingbufferContainer ringbuffer = service.getContainerOrNull(this.getPartitionId(), ns);
        if (ringbuffer == null) {
            ringbuffer = service.getOrCreateContainer(this.getPartitionId(), ns, service.getRingbufferConfig(this.name));
        }
        ringbuffer.cleanup();
        return ringbuffer;
    }

    @Override
    public void logError(Throwable e) {
        if (e instanceof StaleSequenceException) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest(e.getMessage(), e);
            } else if (logger.isFineEnabled()) {
                logger.fine(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        } else {
            super.logError(e);
        }
    }

    @Override
    public int getFactoryId() {
        return RingbufferDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.name = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }

    @Override
    public ObjectNamespace getServiceNamespace() {
        return this.getRingBufferContainer().getNamespace();
    }
}

