/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import javax.annotation.Nonnull;

@Internal
public interface Timer {
    @Nonnull
    default public Ticker start(String ... callParameters) {
        return this.start((Object[])callParameters);
    }

    @Nonnull
    public Ticker start(Object ... var1);
}

