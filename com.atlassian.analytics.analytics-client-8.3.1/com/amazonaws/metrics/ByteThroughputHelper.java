/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.metrics;

import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.metrics.ByteThroughputProvider;
import com.amazonaws.metrics.ServiceMetricCollector;
import com.amazonaws.metrics.ThroughputMetricType;
import java.util.concurrent.TimeUnit;

class ByteThroughputHelper
extends ByteThroughputProvider {
    private static final int REPORT_INTERVAL_SECS = 10;

    ByteThroughputHelper(ThroughputMetricType type) {
        super(type);
    }

    long startTiming() {
        if (TimeUnit.NANOSECONDS.toSeconds(this.getDurationNano()) > 10L) {
            this.reportMetrics();
        }
        return System.nanoTime();
    }

    void reportMetrics() {
        if (this.getByteCount() > 0) {
            Object col = AwsSdkMetrics.getServiceMetricCollector();
            ((ServiceMetricCollector)col).collectByteThroughput(this);
            this.reset();
        }
    }

    @Override
    public void increment(int bytesDelta, long startTimeNano) {
        super.increment(bytesDelta, startTimeNano);
    }
}

