/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.executor.impl.operations;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.executor.impl.DistributedExecutorService;
import com.hazelcast.executor.impl.ExecutorDataSerializerHook;
import com.hazelcast.executor.impl.RunnableAdapter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.concurrent.Callable;

abstract class AbstractCallableTaskOperation
extends Operation
implements NamedOperation,
IdentifiedDataSerializable {
    protected String name;
    protected String uuid;
    private Data callableData;

    public AbstractCallableTaskOperation() {
    }

    public AbstractCallableTaskOperation(String name, String uuid, Data callableData) {
        this.name = name;
        this.uuid = uuid;
        this.callableData = callableData;
    }

    @Override
    public final CallStatus call() {
        return new OffloadImpl();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:executorService";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.uuid);
        out.writeData(this.callableData);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.uuid = in.readUTF();
        this.callableData = in.readData();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }

    @Override
    public int getFactoryId() {
        return ExecutorDataSerializerHook.F_ID;
    }

    private class OffloadImpl
    extends Offload {
        OffloadImpl() {
            super(AbstractCallableTaskOperation.this);
        }

        @Override
        public void start() {
            Callable callable = this.loadCallable();
            DistributedExecutorService service = (DistributedExecutorService)AbstractCallableTaskOperation.this.getService();
            service.execute(AbstractCallableTaskOperation.this.name, AbstractCallableTaskOperation.this.uuid, callable, AbstractCallableTaskOperation.this);
        }

        private Callable loadCallable() {
            ManagedContext managedContext = this.serializationService.getManagedContext();
            Callable callable = (Callable)this.serializationService.toObject(AbstractCallableTaskOperation.this.callableData);
            if (callable instanceof RunnableAdapter) {
                RunnableAdapter adapter = (RunnableAdapter)callable;
                Runnable runnable = (Runnable)managedContext.initialize(adapter.getRunnable());
                adapter.setRunnable(runnable);
            } else {
                callable = (Callable)managedContext.initialize(callable);
            }
            return callable;
        }
    }
}

