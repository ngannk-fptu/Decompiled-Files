/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution;

import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.Histogram;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.DoubleAdder;

public class FixedBoundaryVictoriaMetricsHistogram
implements Histogram {
    private static final IdxOffset UPPER = new IdxOffset(-1, 2);
    private static final IdxOffset LOWER = new IdxOffset(-1, 1);
    private static final IdxOffset ZERO = new IdxOffset(-1, 0);
    private static final int E10MIN = -9;
    private static final int E10MAX = 18;
    private static final int DECIMAL_MULTIPLIER = 2;
    private static final int BUCKET_SIZE = 18;
    private static final int BUCKETS_COUNT = 27;
    private static final double DECIMAL_PRECISION = 0.005;
    private static final String[] VMRANGES = new String[489];
    private static final double[] UPPER_BOUNDS;
    final AtomicReferenceArray<AtomicLongArray> values;
    final AtomicLong zeros = new AtomicLong();
    final AtomicLong lower = new AtomicLong();
    final AtomicLong upper = new AtomicLong();
    final DoubleAdder sum = new DoubleAdder();

    public FixedBoundaryVictoriaMetricsHistogram() {
        this.values = new AtomicReferenceArray(27);
    }

    @Override
    public void recordLong(long value) {
        this.recordDouble(value);
    }

    @Override
    public void recordDouble(double value) {
        if (Double.isNaN(value) || value < 0.0) {
            return;
        }
        IdxOffset inxs = FixedBoundaryVictoriaMetricsHistogram.getBucketIdxAndOffset(value);
        this.sum.add(value);
        if (inxs.bucketIdx < 0) {
            if (inxs.offset == 0) {
                this.zeros.incrementAndGet();
            } else if (inxs.offset == 1) {
                this.lower.incrementAndGet();
            } else {
                this.upper.incrementAndGet();
            }
            return;
        }
        AtomicLongArray hb = this.values.get(inxs.bucketIdx);
        if (hb == null && !this.values.compareAndSet(inxs.bucketIdx, null, hb = new AtomicLongArray(18))) {
            hb = this.values.get(inxs.bucketIdx);
        }
        hb.incrementAndGet(inxs.offset);
    }

    private static IdxOffset getBucketIdxAndOffset(double value) {
        if (value < 0.0) {
            throw new RuntimeException(String.format("BUG: v must be positive; got %f", value));
        }
        if (value == 0.0) {
            return ZERO;
        }
        if (Double.POSITIVE_INFINITY == value) {
            return UPPER;
        }
        int e10 = (int)Math.floor(Math.log10(value));
        int bucketIdx = e10 - -9;
        if (bucketIdx < 0) {
            return LOWER;
        }
        double pow = Math.pow(10.0, e10);
        if (bucketIdx >= 27) {
            if (bucketIdx == 27 && Math.abs(pow - value) < 0.005) {
                return new IdxOffset(26, 17);
            }
            return UPPER;
        }
        double m = (value / pow - 1.0) * 2.0;
        int offset = (int)m;
        if (offset < 0) {
            offset = 0;
        } else if (offset >= 18) {
            offset = 17;
        }
        if (Math.abs((double)offset - m) < 0.005 && --offset < 0) {
            if (--bucketIdx < 0) {
                return LOWER;
            }
            offset = 17;
        }
        return new IdxOffset(bucketIdx, offset);
    }

    private static int getRangeIndex(int index, int offset) {
        if (index < 0) {
            if (offset > 2) {
                throw new RuntimeException(String.format("BUG: offset must be in range [0...2] for negative bucketIdx; got %d", offset));
            }
            return offset;
        }
        return 3 + index * 18 + offset;
    }

    public static String getRangeTagValue(double value) {
        IdxOffset idxOffset = FixedBoundaryVictoriaMetricsHistogram.getBucketIdxAndOffset(value);
        return VMRANGES[FixedBoundaryVictoriaMetricsHistogram.getRangeIndex(idxOffset.bucketIdx, idxOffset.offset)];
    }

    private List<CountAtBucket> nonZeroBuckets() {
        long upperSnap;
        long lowerSnap;
        ArrayList<CountAtBucket> buckets = new ArrayList<CountAtBucket>();
        long zeroSnap = this.zeros.get();
        if (zeroSnap > 0L) {
            buckets.add(new CountAtBucket(UPPER_BOUNDS[FixedBoundaryVictoriaMetricsHistogram.getRangeIndex(FixedBoundaryVictoriaMetricsHistogram.ZERO.bucketIdx, FixedBoundaryVictoriaMetricsHistogram.ZERO.offset)], (double)zeroSnap));
        }
        if ((lowerSnap = this.lower.get()) > 0L) {
            buckets.add(new CountAtBucket(UPPER_BOUNDS[FixedBoundaryVictoriaMetricsHistogram.getRangeIndex(FixedBoundaryVictoriaMetricsHistogram.LOWER.bucketIdx, FixedBoundaryVictoriaMetricsHistogram.LOWER.offset)], (double)lowerSnap));
        }
        if ((upperSnap = this.upper.get()) > 0L) {
            buckets.add(new CountAtBucket(UPPER_BOUNDS[FixedBoundaryVictoriaMetricsHistogram.getRangeIndex(FixedBoundaryVictoriaMetricsHistogram.UPPER.bucketIdx, FixedBoundaryVictoriaMetricsHistogram.UPPER.offset)], (double)upperSnap));
        }
        for (int i = 0; i < this.values.length(); ++i) {
            AtomicLongArray bucket = this.values.get(i);
            if (bucket == null) continue;
            for (int j = 0; j < bucket.length(); ++j) {
                long cnt = bucket.get(j);
                if (cnt <= 0L) continue;
                buckets.add(new CountAtBucket(UPPER_BOUNDS[FixedBoundaryVictoriaMetricsHistogram.getRangeIndex(i, j)], (double)cnt));
            }
        }
        return buckets;
    }

    @Override
    public HistogramSnapshot takeSnapshot(long count, double total, double max) {
        return new HistogramSnapshot(count, total, max, null, this.nonZeroBuckets().toArray(new CountAtBucket[0]), this::outputSummary);
    }

    private void outputSummary(PrintStream printStream, double bucketScaling) {
        printStream.format("%14s %10s\n\n", "Bucket", "TotalCount");
        for (CountAtBucket bucket : this.nonZeroBuckets()) {
            printStream.format(Locale.US, "%14.1f %10d\n", bucket.bucket() / bucketScaling, bucket.count());
        }
        printStream.write(10);
    }

    static {
        FixedBoundaryVictoriaMetricsHistogram.VMRANGES[0] = "0...0";
        FixedBoundaryVictoriaMetricsHistogram.VMRANGES[1] = String.format(Locale.US, "0...%.1fe%d", 1.0, -9);
        FixedBoundaryVictoriaMetricsHistogram.VMRANGES[2] = String.format(Locale.US, "%.1fe%d...+Inf", 1.0, 18);
        UPPER_BOUNDS = new double[489];
        FixedBoundaryVictoriaMetricsHistogram.UPPER_BOUNDS[0] = 0.0;
        FixedBoundaryVictoriaMetricsHistogram.UPPER_BOUNDS[1] = BigDecimal.TEN.pow(-9, MathContext.DECIMAL128).doubleValue();
        FixedBoundaryVictoriaMetricsHistogram.UPPER_BOUNDS[2] = Double.POSITIVE_INFINITY;
        int idx = 3;
        String start = String.format(Locale.US, "%.1fe%d", 1.0, -9);
        for (int bucketIdx = 0; bucketIdx < 27; ++bucketIdx) {
            for (int offset = 0; offset < 18; ++offset) {
                int e10 = -9 + bucketIdx;
                double m = 1.0 + (double)(offset + 1) / 2.0;
                if (Math.abs(m - 10.0) < 0.005) {
                    m = 1.0;
                    ++e10;
                }
                String end = String.format(Locale.US, "%.1fe%d", m, e10);
                FixedBoundaryVictoriaMetricsHistogram.VMRANGES[idx] = start + "..." + end;
                FixedBoundaryVictoriaMetricsHistogram.UPPER_BOUNDS[idx] = BigDecimal.valueOf(m).setScale(1, RoundingMode.HALF_UP).multiply(BigDecimal.TEN.pow(e10, MathContext.DECIMAL128)).doubleValue();
                ++idx;
                start = end;
            }
        }
    }

    private static class IdxOffset {
        final int bucketIdx;
        final int offset;

        IdxOffset(int bucketIdx, int offset) {
            this.bucketIdx = bucketIdx;
            this.offset = offset;
        }
    }
}

