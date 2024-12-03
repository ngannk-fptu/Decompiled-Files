/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

import com.hazelcast.spi.impl.sequence.CallIdSequence;
import com.hazelcast.spi.impl.sequence.CallIdSequenceWithBackpressure;
import com.hazelcast.spi.impl.sequence.CallIdSequenceWithoutBackpressure;
import com.hazelcast.spi.impl.sequence.FailFastCallIdSequence;

public final class CallIdFactory {
    private CallIdFactory() {
    }

    public static CallIdSequence newCallIdSequence(boolean isBackPressureEnabled, int maxAllowedConcurrentInvocations, long backoffTimeoutMs) {
        if (!isBackPressureEnabled) {
            return new CallIdSequenceWithoutBackpressure();
        }
        if (backoffTimeoutMs <= 0L) {
            return new FailFastCallIdSequence(maxAllowedConcurrentInvocations);
        }
        return new CallIdSequenceWithBackpressure(maxAllowedConcurrentInvocations, backoffTimeoutMs);
    }
}

