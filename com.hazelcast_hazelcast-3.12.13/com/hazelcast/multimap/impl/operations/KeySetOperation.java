/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.spi.ReadonlyOperation;

public class KeySetOperation
extends AbstractMultiMapOperation
implements ReadonlyOperation {
    public KeySetOperation() {
    }

    public KeySetOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
        this.response = new MultiMapResponse(container.keySet(), this.getValueCollectionType(container));
    }

    @Override
    public int getId() {
        return 14;
    }
}

