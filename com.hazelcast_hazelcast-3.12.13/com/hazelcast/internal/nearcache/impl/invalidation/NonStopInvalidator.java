/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.IFunction;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.NodeEngine;

public class NonStopInvalidator
extends Invalidator {
    public NonStopInvalidator(String serviceName, IFunction<EventRegistration, Boolean> eventFilter, NodeEngine nodeEngine) {
        super(serviceName, eventFilter, nodeEngine);
    }

    @Override
    protected void invalidateInternal(Invalidation invalidation, int orderKey) {
        this.sendImmediately(invalidation, orderKey);
    }
}

