/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.HdrHistogram.AbstractHistogram
 *  org.HdrHistogram.AtomicHistogram
 *  org.HdrHistogram.Histogram
 *  org.HdrHistogram.WriterReaderPhaser
 */
package org.LatencyUtils;

import java.lang.ref.WeakReference;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AtomicHistogram;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.WriterReaderPhaser;
import org.LatencyUtils.IntervalEstimator;
import org.LatencyUtils.PauseDetector;
import org.LatencyUtils.PauseDetectorListener;
import org.LatencyUtils.SimplePauseDetector;
import org.LatencyUtils.TimeCappedMovingAverageIntervalEstimator;
import org.LatencyUtils.TimeServices;

public class LatencyStats {
    private static Builder defaultBuilder = new Builder();
    private static final TimeServices.ScheduledExecutor latencyStatsScheduledExecutor = new TimeServices.ScheduledExecutor();
    private static PauseDetector defaultPauseDetector;
    private final long lowestTrackableLatency;
    private final long highestTrackableLatency;
    private final int numberOfSignificantValueDigits;
    private volatile AtomicHistogram activeRecordingHistogram;
    private Histogram activePauseCorrectionsHistogram;
    private AtomicHistogram inactiveRawDataHistogram;
    private Histogram inactivePauseCorrectionsHistogram;
    private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
    private final PauseTracker pauseTracker;
    private final IntervalEstimator intervalEstimator;
    private final PauseDetector pauseDetector;

    public static void setDefaultPauseDetector(PauseDetector pauseDetector) {
        defaultPauseDetector = pauseDetector;
    }

    public static PauseDetector getDefaultPauseDetector() {
        return defaultPauseDetector;
    }

    public LatencyStats() {
        this(defaultBuilder.lowestTrackableLatency, defaultBuilder.highestTrackableLatency, defaultBuilder.numberOfSignificantValueDigits, defaultBuilder.intervalEstimatorWindowLength, defaultBuilder.intervalEstimatorTimeCap, defaultBuilder.pauseDetector);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public LatencyStats(long lowestTrackableLatency, long highestTrackableLatency, int numberOfSignificantValueDigits, int intervalEstimatorWindowLength, long intervalEstimatorTimeCap, PauseDetector pauseDetector) {
        if (pauseDetector == null) {
            if (defaultPauseDetector == null) {
                Class<LatencyStats> clazz = LatencyStats.class;
                // MONITORENTER : org.LatencyUtils.LatencyStats.class
                if (defaultPauseDetector == null) {
                    defaultPauseDetector = new SimplePauseDetector();
                }
                // MONITOREXIT : clazz
            }
            this.pauseDetector = defaultPauseDetector;
        } else {
            this.pauseDetector = pauseDetector;
        }
        this.lowestTrackableLatency = lowestTrackableLatency;
        this.highestTrackableLatency = highestTrackableLatency;
        this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
        this.activeRecordingHistogram = new AtomicHistogram(lowestTrackableLatency, highestTrackableLatency, numberOfSignificantValueDigits);
        this.inactiveRawDataHistogram = new AtomicHistogram(lowestTrackableLatency, highestTrackableLatency, numberOfSignificantValueDigits);
        this.activePauseCorrectionsHistogram = new Histogram(lowestTrackableLatency, highestTrackableLatency, numberOfSignificantValueDigits);
        this.inactivePauseCorrectionsHistogram = new Histogram(lowestTrackableLatency, highestTrackableLatency, numberOfSignificantValueDigits);
        this.intervalEstimator = new TimeCappedMovingAverageIntervalEstimator(intervalEstimatorWindowLength, intervalEstimatorTimeCap, this.pauseDetector);
        this.pauseTracker = new PauseTracker(this.pauseDetector, this);
        long now = System.currentTimeMillis();
        this.activeRecordingHistogram.setStartTimeStamp(now);
        this.activePauseCorrectionsHistogram.setStartTimeStamp(now);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recordLatency(long latency) {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            this.trackRecordingInterval();
            this.activeRecordingHistogram.recordValue(latency);
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    public synchronized Histogram getIntervalHistogram() {
        Histogram intervalHistogram = new Histogram(this.lowestTrackableLatency, this.highestTrackableLatency, this.numberOfSignificantValueDigits);
        this.getIntervalHistogramInto(intervalHistogram);
        return intervalHistogram;
    }

    public synchronized void getIntervalHistogramInto(Histogram targetHistogram) {
        try {
            this.recordingPhaser.readerLock();
            this.updateHistograms();
            this.inactiveRawDataHistogram.copyInto((AbstractHistogram)targetHistogram);
            targetHistogram.add((AbstractHistogram)this.inactivePauseCorrectionsHistogram);
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    public synchronized void addIntervalHistogramTo(Histogram toHistogram) {
        try {
            this.recordingPhaser.readerLock();
            this.updateHistograms();
            toHistogram.add((AbstractHistogram)this.inactiveRawDataHistogram);
            toHistogram.add((AbstractHistogram)this.inactivePauseCorrectionsHistogram);
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    public synchronized Histogram getLatestUncorrectedIntervalHistogram() {
        try {
            this.recordingPhaser.readerLock();
            Histogram intervalHistogram = new Histogram(this.lowestTrackableLatency, this.highestTrackableLatency, this.numberOfSignificantValueDigits);
            this.getLatestUncorrectedIntervalHistogramInto(intervalHistogram);
            Histogram histogram = intervalHistogram;
            return histogram;
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    public synchronized void getLatestUncorrectedIntervalHistogramInto(Histogram targetHistogram) {
        try {
            this.recordingPhaser.readerLock();
            this.inactiveRawDataHistogram.copyInto((AbstractHistogram)targetHistogram);
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    public synchronized void stop() {
        this.pauseTracker.stop();
        latencyStatsScheduledExecutor.shutdown();
    }

    public IntervalEstimator getIntervalEstimator() {
        return this.intervalEstimator;
    }

    public PauseDetector getPauseDetector() {
        return this.pauseDetector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void recordDetectedPause(long pauseLength, long pauseEndTime) {
        long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
        try {
            long estimatedInterval = this.intervalEstimator.getEstimatedInterval(pauseEndTime);
            long observedLatencyMinbar = pauseLength - estimatedInterval;
            if (observedLatencyMinbar >= estimatedInterval) {
                this.activePauseCorrectionsHistogram.recordValueWithExpectedInterval(observedLatencyMinbar, estimatedInterval);
            }
        }
        finally {
            this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
        }
    }

    private void trackRecordingInterval() {
        long now = TimeServices.nanoTime();
        this.intervalEstimator.recordInterval(now);
    }

    private void swapRecordingHistograms() {
        AtomicHistogram tempHistogram = this.inactiveRawDataHistogram;
        this.inactiveRawDataHistogram = this.activeRecordingHistogram;
        this.activeRecordingHistogram = tempHistogram;
    }

    private void swapPauseCorrectionHistograms() {
        Histogram tempHistogram = this.inactivePauseCorrectionsHistogram;
        this.inactivePauseCorrectionsHistogram = this.activePauseCorrectionsHistogram;
        this.activePauseCorrectionsHistogram = tempHistogram;
    }

    private synchronized void swapHistograms() {
        this.swapRecordingHistograms();
        this.swapPauseCorrectionHistograms();
    }

    private synchronized void updateHistograms() {
        try {
            this.recordingPhaser.readerLock();
            this.inactiveRawDataHistogram.reset();
            this.inactivePauseCorrectionsHistogram.reset();
            this.swapHistograms();
            long now = System.currentTimeMillis();
            this.activeRecordingHistogram.setStartTimeStamp(now);
            this.activePauseCorrectionsHistogram.setStartTimeStamp(now);
            this.inactiveRawDataHistogram.setEndTimeStamp(now);
            this.inactivePauseCorrectionsHistogram.setEndTimeStamp(now);
            this.recordingPhaser.flipPhase();
        }
        finally {
            this.recordingPhaser.readerUnlock();
        }
    }

    private static class PauseTracker
    extends WeakReference<LatencyStats>
    implements PauseDetectorListener {
        final PauseDetector pauseDetector;

        PauseTracker(PauseDetector pauseDetector, LatencyStats latencyStats) {
            super(latencyStats);
            this.pauseDetector = pauseDetector;
            pauseDetector.addListener(this);
        }

        public void stop() {
            this.pauseDetector.removeListener(this);
        }

        @Override
        public void handlePauseEvent(long pauseLength, long pauseEndTime) {
            LatencyStats latencyStats = (LatencyStats)this.get();
            if (latencyStats != null) {
                latencyStats.recordDetectedPause(pauseLength, pauseEndTime);
            } else {
                this.stop();
            }
        }
    }

    public static class Builder {
        private long lowestTrackableLatency = 1000L;
        private long highestTrackableLatency = 3600000000000L;
        private int numberOfSignificantValueDigits = 2;
        private int intervalEstimatorWindowLength = 1024;
        private long intervalEstimatorTimeCap = 10000000000L;
        private PauseDetector pauseDetector = null;

        public static Builder create() {
            return new Builder();
        }

        public Builder lowestTrackableLatency(long lowestTrackableLatency) {
            this.lowestTrackableLatency = lowestTrackableLatency;
            return this;
        }

        public Builder highestTrackableLatency(long highestTrackableLatency) {
            this.highestTrackableLatency = highestTrackableLatency;
            return this;
        }

        public Builder numberOfSignificantValueDigits(int numberOfSignificantValueDigits) {
            this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
            return this;
        }

        public Builder intervalEstimatorWindowLength(int intervalEstimatorWindowLength) {
            this.intervalEstimatorWindowLength = intervalEstimatorWindowLength;
            return this;
        }

        public Builder intervalEstimatorTimeCap(long intervalEstimatorTimeCap) {
            this.intervalEstimatorTimeCap = intervalEstimatorTimeCap;
            return this;
        }

        public Builder pauseDetector(PauseDetector pauseDetector) {
            this.pauseDetector = pauseDetector;
            return this;
        }

        public LatencyStats build() {
            return new LatencyStats(this.lowestTrackableLatency, this.highestTrackableLatency, this.numberOfSignificantValueDigits, this.intervalEstimatorWindowLength, this.intervalEstimatorTimeCap, this.pauseDetector);
        }
    }
}

