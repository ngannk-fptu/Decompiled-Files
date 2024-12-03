/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Interval;
import javax.annotation.Nonnull;

public interface Elisions {
    public int getCount();

    @Nonnull
    public Interval getInterval();
}

