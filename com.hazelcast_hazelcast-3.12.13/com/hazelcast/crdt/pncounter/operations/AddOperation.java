/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt.pncounter.operations;

import com.hazelcast.cluster.impl.VectorClock;
import com.hazelcast.crdt.pncounter.PNCounterImpl;
import com.hazelcast.crdt.pncounter.PNCounterService;
import com.hazelcast.crdt.pncounter.operations.AbstractPNCounterOperation;
import com.hazelcast.crdt.pncounter.operations.CRDTTimestampedLong;
import com.hazelcast.monitor.impl.LocalPNCounterStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.MutatingOperation;
import java.io.IOException;

public class AddOperation
extends AbstractPNCounterOperation
implements MutatingOperation {
    private VectorClock observedTimestamps;
    private boolean getBeforeUpdate;
    private long delta;
    private CRDTTimestampedLong response;

    public AddOperation(String name, long delta, boolean getBeforeUpdate, VectorClock observedClock) {
        super(name);
        this.delta = delta;
        this.getBeforeUpdate = getBeforeUpdate;
        this.observedTimestamps = observedClock;
    }

    public AddOperation() {
    }

    @Override
    public void run() throws Exception {
        PNCounterImpl counter = this.getPNCounter(this.observedTimestamps);
        this.response = this.getBeforeUpdate ? counter.getAndAdd(this.delta, this.observedTimestamps) : counter.addAndGet(this.delta, this.observedTimestamps);
        this.updateStatistics();
    }

    private void updateStatistics() {
        PNCounterService service = (PNCounterService)this.getService();
        LocalPNCounterStatsImpl stats = service.getLocalPNCounterStats(this.name);
        if (this.delta > 0L) {
            stats.incrementIncrementOperationCount();
        } else if (this.delta < 0L) {
            stats.incrementDecrementOperationCount();
        }
        stats.setValue(this.getBeforeUpdate ? this.response.getValue() + this.delta : this.response.getValue());
    }

    @Override
    public CRDTTimestampedLong getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.getBeforeUpdate);
        out.writeLong(this.delta);
        out.writeObject(this.observedTimestamps);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.getBeforeUpdate = in.readBoolean();
        this.delta = in.readLong();
        this.observedTimestamps = (VectorClock)in.readObject();
    }

    @Override
    public int getId() {
        return 3;
    }
}

