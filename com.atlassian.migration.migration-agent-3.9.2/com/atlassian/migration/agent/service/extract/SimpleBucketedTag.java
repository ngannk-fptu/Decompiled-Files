/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.extract;

public class SimpleBucketedTag {
    private final String label;
    private final int low;
    private final int high;

    public SimpleBucketedTag(int low, int high) {
        this.label = this.genLabel(low, high);
        this.low = low;
        this.high = high;
    }

    public String getLabel() {
        return this.label;
    }

    public int getLow() {
        return this.low;
    }

    public int getHigh() {
        return this.high;
    }

    private String genLabel(int low, int high) {
        if (low == 0) {
            return "<" + high;
        }
        if (high == Integer.MAX_VALUE) {
            return ">" + low;
        }
        return low + "-" + high;
    }
}

