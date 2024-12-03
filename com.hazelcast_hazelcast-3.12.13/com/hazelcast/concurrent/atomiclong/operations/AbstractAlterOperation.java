/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.operations.AtomicLongBackupAwareOperation;
import com.hazelcast.concurrent.atomiclong.operations.SetBackupOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public abstract class AbstractAlterOperation
extends AtomicLongBackupAwareOperation
implements MutatingOperation {
    protected IFunction<Long, Long> function;
    protected long response;
    protected long backup;

    public AbstractAlterOperation() {
    }

    public AbstractAlterOperation(String name, IFunction<Long, Long> function) {
        super(name);
        this.function = function;
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public Operation getBackupOperation() {
        return new SetBackupOperation(this.name, this.backup);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.function);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.function = (IFunction)in.readObject();
    }
}

