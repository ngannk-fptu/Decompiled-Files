/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.util.NavigableSet;
import java.util.TreeSet;

public class PercentileHistogramBuckets {
    private static final int DIGITS = 2;
    private static final NavigableSet<Double> PERCENTILE_BUCKETS = new TreeSet<Double>();

    public static NavigableSet<Double> buckets(DistributionStatisticConfig distributionStatisticConfig) {
        return PERCENTILE_BUCKETS.subSet(distributionStatisticConfig.getMinimumExpectedValueAsDouble(), true, distributionStatisticConfig.getMaximumExpectedValueAsDouble(), true);
    }

    static {
        PERCENTILE_BUCKETS.add(1.0);
        PERCENTILE_BUCKETS.add(2.0);
        PERCENTILE_BUCKETS.add(3.0);
        for (int exp = 2; exp < 64; exp += 2) {
            long current;
            long delta = current / 3L;
            long next = (current << 2) - delta;
            for (current = 1L << exp; current < next; current += delta) {
                PERCENTILE_BUCKETS.add(Double.valueOf(current));
            }
        }
        PERCENTILE_BUCKETS.add(Double.POSITIVE_INFINITY);
    }
}

