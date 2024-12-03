/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Immutable
final class SequenceNumber
implements Comparable<SequenceNumber>,
Serializable {
    private final long value;

    private SequenceNumber(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("SequenceNumber cannot be <0");
        }
        this.value = value;
    }

    public static SequenceNumber of(long sequenceNumber) {
        return new SequenceNumber(sequenceNumber);
    }

    public boolean isAfter(SequenceNumber other) {
        return this.value > other.value;
    }

    boolean isNextAfter(SequenceNumber other) {
        return this.value == other.value + 1L;
    }

    static Generator newSequenceNumberGenerator() {
        final AtomicLong atomicLong = new AtomicLong(0L);
        return new Generator(){

            @Override
            public SequenceNumber getNext() {
                return SequenceNumber.of(atomicLong.incrementAndGet());
            }

            @Override
            public SequenceNumber getCurrent() {
                return SequenceNumber.of(atomicLong.get());
            }
        };
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SequenceNumber that = (SequenceNumber)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public int compareTo(SequenceNumber o) {
        return Long.compare(this.value, o.value);
    }

    static interface Generator {
        public SequenceNumber getNext();

        public SequenceNumber getCurrent();
    }
}

