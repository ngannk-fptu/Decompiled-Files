/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.spi;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

@SuppressFBWarnings(value={"EI"}, justification="The priority is minimizing garbage. The caller guarantees not to mutate the long[] arrays.")
public final class OperationControl
implements IdentifiedDataSerializable {
    private long[] runningOperations;
    private long[] operationsToCancel;

    public OperationControl() {
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public OperationControl(long[] runningOperations, long[] operationsToCancel) {
        this.runningOperations = runningOperations;
        this.operationsToCancel = operationsToCancel;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public long[] runningOperations() {
        return this.runningOperations;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public long[] operationsToCancel() {
        return this.operationsToCancel;
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLongArray(this.runningOperations);
        out.writeLongArray(this.operationsToCancel);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.runningOperations = in.readLongArray();
        this.operationsToCancel = in.readLongArray();
    }
}

