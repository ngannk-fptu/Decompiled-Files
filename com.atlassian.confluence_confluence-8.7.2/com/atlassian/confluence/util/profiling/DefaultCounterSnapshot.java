/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.CounterSnapshot;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
class DefaultCounterSnapshot
implements CounterSnapshot {
    private final String name;
    private final long value;

    DefaultCounterSnapshot(String name, long value) {
        this.name = (String)Preconditions.checkNotNull((Object)name);
        this.value = value;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public long getValue() {
        return this.value;
    }
}

