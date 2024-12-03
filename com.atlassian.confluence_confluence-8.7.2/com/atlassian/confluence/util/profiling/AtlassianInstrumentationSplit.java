/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.instrumentation.operations.OpTimer
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.instrumentation.operations.OpTimer;
import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
@Internal
class AtlassianInstrumentationSplit
implements Split {
    private final OpTimer timer;

    AtlassianInstrumentationSplit(OpTimer timer) {
        this.timer = (OpTimer)Preconditions.checkNotNull((Object)timer);
    }

    @Override
    public @NonNull Split stop() {
        this.timer.end();
        return this;
    }
}

