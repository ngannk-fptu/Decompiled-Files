/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.instrumentation.Counter
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.util.profiling.Counter;
import com.google.common.base.Preconditions;

@Internal
class AtlassianInstrumentationCounter
implements Counter {
    private final com.atlassian.instrumentation.Counter delegate;

    public AtlassianInstrumentationCounter(com.atlassian.instrumentation.Counter delegate) {
        this.delegate = (com.atlassian.instrumentation.Counter)Preconditions.checkNotNull((Object)delegate);
    }

    @Override
    public Counter increase() {
        this.delegate.incrementAndGet();
        return this;
    }

    @Override
    public Counter increase(long amount) {
        this.delegate.addAndGet(amount);
        return this;
    }
}

