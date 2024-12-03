/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 */
package com.atlassian.failurecache.failures;

import com.atlassian.failurecache.util.date.Clock;
import com.google.common.collect.ComparisonChain;
import java.util.Date;

public final class FailureEntry
implements Comparable<FailureEntry> {
    public static final FailureEntry NULL_ENTRY = new FailureEntry(new Date(0L), 0);
    private final Date failureExpiry;
    private final int failureCount;

    public FailureEntry(Date failureExpiry, int failureCount) {
        this.failureExpiry = failureExpiry;
        this.failureCount = failureCount;
    }

    public Date getFailureExpiry() {
        return this.failureExpiry;
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public boolean isFailingNow(Clock clock) {
        return this.getFailureExpiry().after(clock.getCurrentDate());
    }

    @Override
    public int compareTo(FailureEntry other) {
        if (other == null) {
            return -1;
        }
        return ComparisonChain.start().compare((Comparable)this.getFailureExpiry(), (Comparable)other.getFailureExpiry()).result();
    }
}

