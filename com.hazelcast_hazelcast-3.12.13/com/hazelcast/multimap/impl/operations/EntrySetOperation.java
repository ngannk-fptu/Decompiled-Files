/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.multimap.impl.operations.EntrySetResponse;
import com.hazelcast.spi.ReadonlyOperation;

public class EntrySetOperation
extends AbstractMultiMapOperation
implements ReadonlyOperation {
    public EntrySetOperation() {
    }

    public EntrySetOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
        this.response = new EntrySetResponse(container.copyCollections(), this.getNodeEngine());
    }

    @Override
    public int getId() {
        return 10;
    }
}

