/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.operations.AbstractAtomicLongOperation;
import com.hazelcast.core.IFunction;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class ApplyOperation<R>
extends AbstractAtomicLongOperation
implements MutatingOperation {
    private IFunction<Long, R> function;
    private R returnValue;

    public ApplyOperation() {
    }

    public ApplyOperation(String name, IFunction<Long, R> function) {
        super(name);
        this.function = function;
    }

    @Override
    public void run() throws Exception {
        AtomicLongContainer container = this.getLongContainer();
        this.returnValue = this.function.apply(container.get());
    }

    public R getResponse() {
        return this.returnValue;
    }

    @Override
    public int getId() {
        return 4;
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

