/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.TokenizerDescriptor;

public final class NGramTokenizerDescriptor
implements TokenizerDescriptor {
    private final int min;
    private final int max;
    private final boolean leadingEdgesOnly;

    public NGramTokenizerDescriptor(int min, int max) {
        this(min, max, false);
    }

    public NGramTokenizerDescriptor(int min, int max, boolean leadingEdgesOnly) {
        this.min = min;
        this.max = max;
        this.leadingEdgesOnly = leadingEdgesOnly;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean isLeadingEdgesOnly() {
        return this.leadingEdgesOnly;
    }
}

