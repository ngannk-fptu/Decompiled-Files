/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

import java.text.DecimalFormat;

public class Percentage {
    private final int count;
    private final int total;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#00.0");

    private Percentage(int count, int total) {
        this.count = count;
        this.total = total;
    }

    public String toString() {
        return DECIMAL_FORMAT.format((float)this.count * 100.0f / (float)this.total);
    }

    public static Percentage get(int count, int total) {
        return new Percentage(count, total);
    }
}

