/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Interval
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Interval;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleInterval
implements Interval {
    private final Instant end;
    private final Instant start;

    public SimpleInterval(Instant start, Instant end) {
        this.end = Objects.requireNonNull(end, "end");
        this.start = Objects.requireNonNull(start, "start");
    }

    @Nonnull
    public Instant getEnd() {
        return this.end;
    }

    @Nonnull
    public Instant getStart() {
        return this.start;
    }
}

