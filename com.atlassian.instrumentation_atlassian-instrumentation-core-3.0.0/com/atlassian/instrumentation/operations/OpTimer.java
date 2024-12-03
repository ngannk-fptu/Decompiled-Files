/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpSnapshot;

public interface OpTimer {
    public String getName();

    public OpSnapshot snapshot();

    public OpSnapshot endWithTime(long var1);

    public OpSnapshot end(long var1);

    public OpSnapshot end();

    public OpSnapshot end(HeisenburgResultSetCalculator var1);

    public static interface HeisenburgResultSetCalculator {
        public long calculate();
    }

    public static interface OnEndCallback {
        public void onEndCalled(OpSnapshot var1);
    }
}

