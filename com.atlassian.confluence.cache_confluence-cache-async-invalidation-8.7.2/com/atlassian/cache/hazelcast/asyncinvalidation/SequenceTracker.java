/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.NotThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NotThreadSafe
final class SequenceTracker<S> {
    private static final Logger log = LoggerFactory.getLogger(SequenceTracker.class);
    private final Map<S, SequenceNumber> sequenceNumbers = new HashMap<S, SequenceNumber>();

    SequenceTracker() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean verifyNextInSequence(S source, SequenceNumber sequenceNumber) {
        try {
            boolean bl = this.isNextInSequence(source, sequenceNumber);
            return bl;
        }
        finally {
            this.resetSequence(source, sequenceNumber);
        }
    }

    public boolean verifyMinimumSequenceNumber(S source, SequenceNumber minimumSequenceNumber) {
        SequenceNumber current = this.getCurrentSequenceNumber(source);
        log.debug("Checking last-known sequence number {} from {} against expected minimum {}", new Object[]{current, source, minimumSequenceNumber});
        boolean rejected = minimumSequenceNumber.isAfter(current);
        if (rejected) {
            this.resetSequence(source, minimumSequenceNumber);
            return false;
        }
        return true;
    }

    private boolean isNextInSequence(S source, SequenceNumber sequenceNumber) {
        SequenceNumber lastKnownSequenceNumber = this.getCurrentSequenceNumber(source);
        if (sequenceNumber.isNextAfter(lastKnownSequenceNumber)) {
            log.debug("Invalidation sequence number {} is in sequence with last known {} from {}", new Object[]{sequenceNumber, lastKnownSequenceNumber, source});
            return true;
        }
        log.warn("Invalidation sequence number {} is out sequence with last known {} from {}", new Object[]{sequenceNumber, lastKnownSequenceNumber, source});
        return false;
    }

    SequenceNumber getCurrentSequenceNumber(S source) {
        return this.sequenceNumbers.getOrDefault(source, SequenceNumber.of(0L));
    }

    void resetSequence(S source, SequenceNumber sequenceNumber) {
        this.sequenceNumbers.put(source, sequenceNumber);
    }
}

