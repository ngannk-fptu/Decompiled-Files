/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter.operations;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.crdt.pncounter.operations.AbstractPNCounterOperation;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class GetOperation
extends AbstractPNCounterOperation
implements ReadonlyOperation {
    private VectorClock observedTimestamps;
    private CRDTTimestampedLong response;

    public GetOperation(String name, VectorClock observedClock) {
        super(name);
        this.observedTimestamps = observedClock;
    }

    public GetOperation() {
    }

    @Override
    public void run() throws Exception {
        this.response = this.getPNCounter(this.observedTimestamps).get(this.observedTimestamps);
    }

    @Override
    public CRDTTimestampedLong getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.observedTimestamps);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.observedTimestamps = (VectorClock)in.readObject();
    }

    @Override
    public int getId() {
        return 4;
    }
}

