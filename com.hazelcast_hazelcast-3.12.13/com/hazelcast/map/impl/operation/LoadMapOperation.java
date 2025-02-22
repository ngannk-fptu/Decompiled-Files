/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class LoadMapOperation
extends MapOperation
implements MutatingOperation {
    private boolean replaceExistingValues;

    public LoadMapOperation() {
    }

    public LoadMapOperation(String name, boolean replaceExistingValues) {
        super(name);
        this.replaceExistingValues = replaceExistingValues;
    }

    @Override
    public void run() throws Exception {
        this.recordStore.loadAll(this.replaceExistingValues);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.replaceExistingValues);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.replaceExistingValues = in.readBoolean();
    }

    @Override
    public int getId() {
        return 16;
    }
}

