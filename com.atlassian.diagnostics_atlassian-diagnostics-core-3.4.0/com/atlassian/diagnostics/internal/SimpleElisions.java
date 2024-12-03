/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Elisions
 *  com.atlassian.diagnostics.Interval
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Elisions;
import com.atlassian.diagnostics.Interval;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleElisions
implements Elisions {
    private final int count;
    private final Interval interval;

    public SimpleElisions(@Nonnull Interval interval, int count) {
        Preconditions.checkArgument((count > 0 ? 1 : 0) != 0, (Object)"count must be greater than 0");
        this.count = count;
        this.interval = Objects.requireNonNull(interval, "interval");
    }

    public int getCount() {
        return this.count;
    }

    @Nonnull
    public Interval getInterval() {
        return this.interval;
    }
}

