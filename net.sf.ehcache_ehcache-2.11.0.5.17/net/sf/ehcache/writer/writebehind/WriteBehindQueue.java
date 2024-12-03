/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind;

import java.util.ArrayList;
import java.util.List;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.writer.writebehind.AbstractWriteBehindQueue;
import net.sf.ehcache.writer.writebehind.operations.SingleOperation;

class WriteBehindQueue
extends AbstractWriteBehindQueue {
    private List<SingleOperation> waiting = new ArrayList<SingleOperation>();

    WriteBehindQueue(CacheConfiguration config) {
        super(config);
    }

    @Override
    protected List<SingleOperation> quarantineItems() {
        List<SingleOperation> quarantined = this.waiting;
        this.waiting = new ArrayList<SingleOperation>();
        return quarantined;
    }

    @Override
    protected void addItem(SingleOperation operation) {
        this.waiting.add(operation);
    }

    @Override
    public long getQueueSize() {
        return this.waiting.size();
    }

    @Override
    protected void reinsertUnprocessedItems(List<SingleOperation> operations) {
        ArrayList<SingleOperation> newQueue = new ArrayList<SingleOperation>(operations);
        newQueue.addAll(this.waiting);
        this.waiting = newQueue;
    }
}

