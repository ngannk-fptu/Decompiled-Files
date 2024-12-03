/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapContainer;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.AbstractMultiMapOperation;
import com.hazelcast.spi.ReadonlyOperation;

public class SizeOperation
extends AbstractMultiMapOperation
implements ReadonlyOperation {
    public SizeOperation() {
    }

    public SizeOperation(String name) {
        super(name);
    }

    @Override
    public void run() throws Exception {
        MultiMapContainer container = this.getOrCreateContainer();
        this.response = container.size();
        ((MultiMapService)this.getService()).getLocalMultiMapStatsImpl(this.name).incrementOtherOperations();
    }

    @Override
    public int getId() {
        return 25;
    }
}

