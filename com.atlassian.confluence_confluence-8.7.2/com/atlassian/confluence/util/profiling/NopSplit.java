/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.Split;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
class NopSplit
implements Split {
    static final NopSplit INSTANCE = new NopSplit();

    NopSplit() {
    }

    @Override
    public @NonNull Split stop() {
        return this;
    }
}

