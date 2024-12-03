/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

public class HttpConnectionPoolMetric {
    private final int numIdle;
    private final int numActive;
    private final int numMax;

    public HttpConnectionPoolMetric(int numMax, int numCurrent, int numActive) {
        this.numIdle = Math.subtractExact(numCurrent, numActive);
        this.numActive = numActive;
        this.numMax = numMax;
    }

    public int getNumIdle() {
        return this.numIdle;
    }

    public int getNumActive() {
        return this.numActive;
    }

    public int getNumMax() {
        return this.numMax;
    }
}

