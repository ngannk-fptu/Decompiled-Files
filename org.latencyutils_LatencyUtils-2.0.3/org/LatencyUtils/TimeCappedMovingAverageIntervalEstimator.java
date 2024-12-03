/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLongArray;
import org.LatencyUtils.MovingAverageIntervalEstimator;
import org.LatencyUtils.PauseDetector;
import org.LatencyUtils.PauseDetectorListener;

public class TimeCappedMovingAverageIntervalEstimator
extends MovingAverageIntervalEstimator {
    private final long baseTimeCap;
    private final PauseTracker pauseTracker;
    private long timeCap;
    private volatile long timeOfLastEstimatedInterval = 0L;
    private static final int maxPausesToTrack = 32;
    private AtomicLongArray pauseStartTimes = new AtomicLongArray(32);
    private AtomicLongArray pauseLengths = new AtomicLongArray(32);
    private int earliestPauseIndex = 0;
    private int nextPauseRecordingIndex = 0;

    public TimeCappedMovingAverageIntervalEstimator(int requestedWindowLength, long timeCap) {
        this(requestedWindowLength, timeCap, null);
    }

    public TimeCappedMovingAverageIntervalEstimator(int requestedWindowLength, long timeCap, PauseDetector pauseDetector) {
        super(requestedWindowLength);
        this.baseTimeCap = timeCap;
        this.timeCap = timeCap;
        this.pauseTracker = pauseDetector != null ? new PauseTracker(pauseDetector, this) : null;
        for (int i = 0; i < 32; ++i) {
            this.pauseStartTimes.set(i, Long.MAX_VALUE);
            this.pauseLengths.set(i, 0L);
        }
    }

    @Override
    public void recordInterval(long when) {
        super.recordIntervalAndReturnWindowPosition(when);
    }

    @Override
    public synchronized long getEstimatedInterval(long when) {
        long windowTimeSpan;
        long sampledCountPre;
        this.timeOfLastEstimatedInterval = when;
        this.eliminateStalePauses(when);
        long sampledCount = this.count.get();
        if (sampledCount < (long)this.windowLength) {
            return Long.MAX_VALUE;
        }
        int numberOfWindowPositionsOutsideOfTimeCap = this.determineNumberOfWindowPositionsOutsideOfTimeCap(when);
        do {
            sampledCountPre = sampledCount;
            int latestWindowPosition = (int)(sampledCount + (long)this.windowLength - 1L & (long)this.windowMask);
            long windowStartTime = this.determineEarliestQualifyingTimeInWindow(when);
            if (windowStartTime == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long windowEndTime = Math.max(this.intervalEndTimes[latestWindowPosition], when);
            windowTimeSpan = windowEndTime - windowStartTime;
        } while ((sampledCount = this.count.get()) != sampledCountPre || windowTimeSpan < 0L);
        long totalPauseTimeInWindow = this.timeCap - this.baseTimeCap;
        int positionDelta = this.windowLength - numberOfWindowPositionsOutsideOfTimeCap - 1;
        if (positionDelta <= 0) {
            return Long.MAX_VALUE;
        }
        long averageInterval = (windowTimeSpan - totalPauseTimeInWindow) / (long)positionDelta;
        if (averageInterval <= 0L) {
            return Long.MAX_VALUE;
        }
        return averageInterval;
    }

    private synchronized void recordPause(long pauseLength, long pauseEndTime) {
        if (this.pauseStartTimes.get(this.nextPauseRecordingIndex) != Long.MAX_VALUE) {
            this.timeCap -= this.pauseLengths.get(this.nextPauseRecordingIndex);
            this.earliestPauseIndex = (this.nextPauseRecordingIndex + 1) % 32;
        }
        this.timeCap += pauseLength;
        this.pauseStartTimes.set(this.nextPauseRecordingIndex, pauseEndTime - pauseLength);
        this.pauseLengths.set(this.nextPauseRecordingIndex, pauseLength);
        this.nextPauseRecordingIndex = (this.nextPauseRecordingIndex + 1) % 32;
    }

    public void stop() {
        if (this.pauseTracker != null) {
            this.pauseTracker.stop();
        }
    }

    public String toString() {
        long when = this.timeOfLastEstimatedInterval;
        this.eliminateStalePauses(when);
        int numberOfWindowPositionsOutsideOfTimeCap = this.determineNumberOfWindowPositionsOutsideOfTimeCap(when);
        long windowStartTime = this.determineEarliestQualifyingTimeInWindow(when);
        long windowTimeSpan = when - windowStartTime;
        long totalPauseTimeInWindow = this.timeCap - this.baseTimeCap;
        int positionDelta = this.windowLength - numberOfWindowPositionsOutsideOfTimeCap - 1;
        long averageInterval = Long.MAX_VALUE;
        if (positionDelta > 0) {
            averageInterval = (windowTimeSpan - totalPauseTimeInWindow) / (long)positionDelta;
        }
        return "IntervalEstimator: \nEstimated Interval: " + this.getEstimatedInterval(when) + " (calculated at time " + when + ")\n" + "Time cap: " + this.timeCap + ", count = " + this.count.get() + ", currentPosition = " + this.getCurrentPosition() + "\n" + "timeCapStartTime = " + (when - this.timeCap) + ", numberOfWindowPositionsSkipped = " + numberOfWindowPositionsOutsideOfTimeCap + "\n" + "windowStartTime = " + windowStartTime + ", windowTimeSpan = " + windowTimeSpan + ", positionDelta = " + positionDelta + "\n" + "totalPauseTimeInWindow = " + totalPauseTimeInWindow + ", averageInterval = " + averageInterval + "\n";
    }

    private void eliminateStalePauses(long when) {
        long earliestQualifyingTimeInWindow;
        long newEarliestQualifyingTimeInWindow = this.determineEarliestQualifyingTimeInWindow(when);
        do {
            earliestQualifyingTimeInWindow = newEarliestQualifyingTimeInWindow;
            long timeCapStartTime = when - this.timeCap;
            long earliestPauseTimeToConsider = Math.max(timeCapStartTime, earliestQualifyingTimeInWindow);
            long earliestPauseStartTime = this.pauseStartTimes.get(this.earliestPauseIndex);
            while (earliestPauseStartTime < earliestPauseTimeToConsider) {
                this.timeCap -= this.pauseLengths.get(this.earliestPauseIndex);
                timeCapStartTime = when - this.timeCap;
                earliestPauseTimeToConsider = Math.max(timeCapStartTime, earliestQualifyingTimeInWindow);
                this.pauseStartTimes.set(this.earliestPauseIndex, Long.MAX_VALUE);
                this.pauseLengths.set(this.earliestPauseIndex, 0L);
                this.earliestPauseIndex = (this.earliestPauseIndex + 1) % 32;
                earliestPauseStartTime = this.pauseStartTimes.get(this.earliestPauseIndex);
            }
        } while (earliestQualifyingTimeInWindow != (newEarliestQualifyingTimeInWindow = this.determineEarliestQualifyingTimeInWindow(when)));
    }

    private long determineEarliestQualifyingTimeInWindow(long when) {
        int numberOfWindowPositionsOutsideOfTimeCap = this.determineNumberOfWindowPositionsOutsideOfTimeCap(when);
        if (numberOfWindowPositionsOutsideOfTimeCap == this.windowLength) {
            return Long.MAX_VALUE;
        }
        int earliestQualifyingWindowPosition = this.getCurrentPosition() + numberOfWindowPositionsOutsideOfTimeCap & this.windowMask;
        return this.intervalEndTimes[earliestQualifyingWindowPosition];
    }

    private int determineNumberOfWindowPositionsOutsideOfTimeCap(long when) {
        long timeCapStartTime;
        int currentPosition = this.getCurrentPosition();
        if (this.intervalEndTimes[currentPosition] >= (timeCapStartTime = when - this.timeCap)) {
            return 0;
        }
        int lowOffset = 0;
        int highOffset = this.windowLength;
        while (lowOffset < highOffset) {
            int currentGuessAtFirstQualifyingIndexOffset = lowOffset + highOffset >>> 1;
            int index = currentPosition + currentGuessAtFirstQualifyingIndexOffset & this.windowMask;
            long guessValue = this.intervalEndTimes[index];
            if (guessValue < timeCapStartTime) {
                lowOffset = currentGuessAtFirstQualifyingIndexOffset + 1;
                continue;
            }
            highOffset = currentGuessAtFirstQualifyingIndexOffset;
        }
        return lowOffset;
    }

    private static class PauseTracker
    extends WeakReference<TimeCappedMovingAverageIntervalEstimator>
    implements PauseDetectorListener {
        final PauseDetector pauseDetector;

        PauseTracker(PauseDetector pauseDetector, TimeCappedMovingAverageIntervalEstimator estimator) {
            super(estimator);
            this.pauseDetector = pauseDetector;
            pauseDetector.addListener(this, true);
        }

        public void stop() {
            this.pauseDetector.removeListener(this);
        }

        @Override
        public void handlePauseEvent(long pauseLength, long pauseEndTime) {
            TimeCappedMovingAverageIntervalEstimator estimator = (TimeCappedMovingAverageIntervalEstimator)this.get();
            if (estimator != null) {
                estimator.recordPause(pauseLength, pauseEndTime);
            } else {
                this.stop();
            }
        }
    }
}

