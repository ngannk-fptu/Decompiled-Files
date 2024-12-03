/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface CounterSnapshot {
    public @NonNull String getName();

    public long getValue();
}

