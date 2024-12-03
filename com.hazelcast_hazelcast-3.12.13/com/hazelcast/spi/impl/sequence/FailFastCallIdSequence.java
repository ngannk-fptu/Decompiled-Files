/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.spi.impl.sequence.AbstractCallIdSequence;

public class FailFastCallIdSequence
extends AbstractCallIdSequence {
    public FailFastCallIdSequence(int maxConcurrentInvocations) {
        super(maxConcurrentInvocations);
    }

    @Override
    protected void handleNoSpaceLeft() {
        throw new HazelcastOverloadException("Maximum invocation count is reached. maxConcurrentInvocations = " + this.getMaxConcurrentInvocations());
    }
}

