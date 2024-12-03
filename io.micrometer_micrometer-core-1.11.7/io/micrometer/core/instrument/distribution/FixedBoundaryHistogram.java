/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.distribution.CountAtBucket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLongArray;

class FixedBoundaryHistogram {
    final AtomicLongArray values;
    private final double[] buckets;
    private final boolean isCumulativeBucketCounts;

    FixedBoundaryHistogram(double[] buckets, boolean isCumulativeBucketCounts) {
        this.buckets = buckets;
        this.values = new AtomicLongArray(buckets.length);
        this.isCumulativeBucketCounts = isCumulativeBucketCounts;
    }

    long countAtValue(double value) {
        int index = Arrays.binarySearch(this.buckets, value);
        if (index < 0) {
            return 0L;
        }
        return this.values.get(index);
    }

    void reset() {
        for (int i = 0; i < this.values.length(); ++i) {
            this.values.set(i, 0L);
        }
    }

    void record(long value) {
        int index = this.leastLessThanOrEqualTo(value);
        if (index > -1) {
            this.values.incrementAndGet(index);
        }
    }

    int leastLessThanOrEqualTo(double key) {
        int low = 0;
        int high = this.buckets.length - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            double value = this.buckets[mid];
            if (value < key) {
                low = mid + 1;
                continue;
            }
            if (value > key) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return low < this.buckets.length ? low : -1;
    }

    Iterator<CountAtBucket> countsAtValues(final Iterator<Double> values) {
        return new Iterator<CountAtBucket>(){
            private double cumulativeCount = 0.0;

            @Override
            public boolean hasNext() {
                return values.hasNext();
            }

            @Override
            public CountAtBucket next() {
                double value = (Double)values.next();
                double count = FixedBoundaryHistogram.this.countAtValue(value);
                if (FixedBoundaryHistogram.this.isCumulativeBucketCounts) {
                    this.cumulativeCount += count;
                    return new CountAtBucket(value, this.cumulativeCount);
                }
                return new CountAtBucket(value, count);
            }
        };
    }
}

