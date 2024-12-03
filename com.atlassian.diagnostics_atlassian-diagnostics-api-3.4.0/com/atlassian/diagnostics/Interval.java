/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import java.time.Instant;
import javax.annotation.Nonnull;

public interface Interval {
    @Nonnull
    public Instant getEnd();

    @Nonnull
    public Instant getStart();
}

