/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.Progress;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultProgress
implements Progress {
    private final AtomicInteger count;
    private final int total;

    public DefaultProgress(int total) {
        this(0, total);
    }

    public DefaultProgress(int initial, int total) {
        if (total < -1) {
            throw new IllegalArgumentException("total cannot be less than 0, specify -1 to indicate no total is available");
        }
        if (initial < 0) {
            throw new IllegalArgumentException("initial value cannot be less than 0");
        }
        this.count = new AtomicInteger(initial);
        this.total = total;
    }

    @Override
    public int getCount() {
        return this.count.get();
    }

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public int getPercentComplete() {
        if (this.total == -1) {
            return -1;
        }
        if (this.count.get() >= this.total) {
            return 100;
        }
        int calculatedPercentage = (int)(100.0f * (float)this.count.get() / (float)this.total);
        if (this.count.get() < this.total && calculatedPercentage == 100) {
            calculatedPercentage = 99;
        }
        return calculatedPercentage;
    }

    @Override
    public int increment() {
        return this.count.incrementAndGet();
    }

    @Override
    public int increment(int delta) {
        return this.count.addAndGet(delta);
    }
}

