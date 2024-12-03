/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.util.profiling.Counter;

@Internal
class NopCounter
implements Counter {
    static final NopCounter INSTANCE = new NopCounter();

    NopCounter() {
    }

    @Override
    public Counter increase() {
        return this;
    }

    @Override
    public Counter increase(long amount) {
        return this;
    }
}

