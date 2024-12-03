/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.Map;

@Immutable(containerOf={"T"})
final class SequenceSnapshot<T>
implements Serializable {
    private final ImmutableMap<T, SequenceNumber> sequenceNumbers;

    SequenceSnapshot(Map<T, SequenceNumber> sequenceNumbers) {
        this.sequenceNumbers = ImmutableMap.copyOf(sequenceNumbers);
    }

    public Map<T, SequenceNumber> getSequenceNumbers() {
        return this.sequenceNumbers;
    }

    public String toString() {
        return this.sequenceNumbers.toString();
    }
}

