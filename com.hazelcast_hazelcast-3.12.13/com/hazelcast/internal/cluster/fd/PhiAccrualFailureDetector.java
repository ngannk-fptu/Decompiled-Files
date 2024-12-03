/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

import com.hazelcast.internal.cluster.fd.FailureDetector;
import com.hazelcast.util.Preconditions;
import java.util.LinkedList;

public class PhiAccrualFailureDetector
implements FailureDetector {
    static final long NO_HEARTBEAT_TIMESTAMP = -1L;
    private final double threshold;
    private final double minStdDeviationMillis;
    private final long acceptableHeartbeatPauseMillis;
    private final HeartbeatHistory heartbeatHistory;
    private volatile long lastHeartbeatMillis = -1L;

    public PhiAccrualFailureDetector(double threshold, int maxSampleSize, double minStdDeviationMillis, long acceptableHeartbeatPauseMillis, long firstHeartbeatEstimateMillis) {
        this.threshold = Preconditions.checkPositive(threshold, "Threshold must be positive: " + threshold);
        this.minStdDeviationMillis = Preconditions.checkPositive(minStdDeviationMillis, "Minimum standard deviation must be positive: " + minStdDeviationMillis);
        this.acceptableHeartbeatPauseMillis = Preconditions.checkNotNegative(acceptableHeartbeatPauseMillis, "Acceptable heartbeat pause millis must be >= 0: " + acceptableHeartbeatPauseMillis);
        Preconditions.checkPositive(firstHeartbeatEstimateMillis, "First heartbeat value must be > 0: " + firstHeartbeatEstimateMillis);
        this.heartbeatHistory = new HeartbeatHistory(maxSampleSize);
        this.firstHeartbeat(firstHeartbeatEstimateMillis);
    }

    private void firstHeartbeat(long firstHeartbeatEstimateMillis) {
        long stdDeviationMillis = firstHeartbeatEstimateMillis / 4L;
        this.heartbeatHistory.add(firstHeartbeatEstimateMillis - stdDeviationMillis);
        this.heartbeatHistory.add(firstHeartbeatEstimateMillis + stdDeviationMillis);
    }

    private double ensureValidStdDeviation(double stdDeviationMillis) {
        return Math.max(stdDeviationMillis, this.minStdDeviationMillis);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private double phi(long timestampMillis) {
        double stdDeviationMillis;
        double meanMillis;
        long timeDiffMillis;
        HeartbeatHistory heartbeatHistory = this.heartbeatHistory;
        synchronized (heartbeatHistory) {
            long lastTimestampMillis = this.lastHeartbeatMillis;
            if (lastTimestampMillis == -1L) {
                return 0.0;
            }
            timeDiffMillis = timestampMillis - lastTimestampMillis;
            meanMillis = this.heartbeatHistory.mean();
            stdDeviationMillis = this.ensureValidStdDeviation(this.heartbeatHistory.stdDeviation());
        }
        return PhiAccrualFailureDetector.phi(timeDiffMillis, meanMillis + (double)this.acceptableHeartbeatPauseMillis, stdDeviationMillis);
    }

    private static double phi(long timeDiffMillis, double meanMillis, double stdDeviationMillis) {
        double y = ((double)timeDiffMillis - meanMillis) / stdDeviationMillis;
        double e = Math.exp(-y * (1.5976 + 0.070566 * y * y));
        if ((double)timeDiffMillis > meanMillis) {
            return -Math.log10(e / (1.0 + e));
        }
        return -Math.log10(1.0 - 1.0 / (1.0 + e));
    }

    @Override
    public boolean isAlive(long timestampMillis) {
        double phi = this.phi(timestampMillis);
        return phi < this.threshold;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void heartbeat(long timestampMillis) {
        HeartbeatHistory heartbeatHistory = this.heartbeatHistory;
        synchronized (heartbeatHistory) {
            long lastTimestampMillis = this.getAndSetLastHeartbeat(timestampMillis);
            if (lastTimestampMillis == -1L) {
                return;
            }
            if (this.isAlive(timestampMillis)) {
                this.heartbeatHistory.add(timestampMillis - lastTimestampMillis);
            }
        }
    }

    private long getAndSetLastHeartbeat(long timestampMillis) {
        long lastTimestampMillis = this.lastHeartbeatMillis;
        this.lastHeartbeatMillis = timestampMillis;
        return lastTimestampMillis;
    }

    @Override
    public long lastHeartbeat() {
        return this.lastHeartbeatMillis;
    }

    @Override
    public double suspicionLevel(long timestamp) {
        return this.phi(timestamp);
    }

    private static class HeartbeatHistory {
        private final int maxSampleSize;
        private final LinkedList<Long> intervals = new LinkedList();
        private long intervalSum;
        private long squaredIntervalSum;

        HeartbeatHistory(int maxSampleSize) {
            if (maxSampleSize < 1) {
                throw new IllegalArgumentException("Sample size must be >= 1 : " + maxSampleSize);
            }
            this.maxSampleSize = maxSampleSize;
        }

        double mean() {
            return (double)this.intervalSum / (double)this.intervals.size();
        }

        double variance() {
            double mean = this.mean();
            return (double)this.squaredIntervalSum / (double)this.intervals.size() - mean * mean;
        }

        double stdDeviation() {
            return Math.sqrt(this.variance());
        }

        void add(long interval) {
            if (this.intervals.size() >= this.maxSampleSize) {
                this.dropOldest();
            }
            this.intervals.add(interval);
            this.intervalSum += interval;
            this.squaredIntervalSum += HeartbeatHistory.pow2(interval);
        }

        private void dropOldest() {
            long dropped = this.intervals.pollFirst();
            this.intervalSum -= dropped;
            this.squaredIntervalSum -= HeartbeatHistory.pow2(dropped);
        }

        private static long pow2(long x) {
            return x * x;
        }
    }
}

