/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.TokenFilterDescriptor;

public class ShingleTokenFilterDescriptor
implements TokenFilterDescriptor {
    private final int min;
    private final int max;

    public ShingleTokenFilterDescriptor(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }
}

