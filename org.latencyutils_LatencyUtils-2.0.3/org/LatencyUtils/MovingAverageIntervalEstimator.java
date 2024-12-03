/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

import java.util.concurrent.atomic.AtomicLong;
import org.LatencyUtils.IntervalEstimator;

public class MovingAverageIntervalEstimator
extends IntervalEstimator {
    protected final long[] intervalEndTimes;
    protected final int windowMagnitude;
    protected final int windowLength;
    protected final int windowMask;
    protected AtomicLong count = new AtomicLong(0L);

    public MovingAverageIntervalEstimator(int requestedWindowLength) {
        this.windowMagnitude = (int)Math.ceil(Math.log(requestedWindowLength) / Math.log(2.0));
        this.windowLength = (int)Math.pow(2.0, this.windowMagnitude);
        this.windowMask = this.windowLength - 1;
        this.intervalEndTimes = new long[this.windowLength];
        for (int i = 0; i < this.intervalEndTimes.length; ++i) {
            this.intervalEndTimes[i] = Long.MIN_VALUE;
        }
    }

    @Override
    public void recordInterval(long when) {
        this.recordIntervalAndReturnWindowPosition(when);
    }

    int recordIntervalAndReturnWindowPosition(long when) {
        long countAtSwapTime = this.count.getAndIncrement();
        int positionToSwap = (int)(countAtSwapTime & (long)this.windowMask);
        this.intervalEndTimes[positionToSwap] = when;
        return positionToSwap;
    }

    @Override
    public long getEstimatedInterval(long when) {
        long windowTimeSpan;
        long sampledCountPre;
        long sampledCount = this.count.get();
        if (sampledCount < (long)this.windowLength) {
            return Long.MAX_VALUE;
        }
        do {
            sampledCountPre = sampledCount;
            int earliestWindowPosition = (int)(sampledCount & (long)this.windowMask);
            int latestWindowPosition = (int)(sampledCount + (long)this.windowLength - 1L & (long)this.windowMask);
            long windowStartTime = this.intervalEndTimes[earliestWindowPosition];
            long windowEndTime = Math.max(this.intervalEndTimes[latestWindowPosition], when);
            windowTimeSpan = windowEndTime - windowStartTime;
        } while ((sampledCount = this.count.get()) != sampledCountPre || windowTimeSpan < 0L);
        long averageInterval = windowTimeSpan / (long)(this.windowLength - 1);
        return Math.max(averageInterval, 1L);
    }

    protected int getCurrentPosition() {
        return (int)(this.count.get() & (long)this.windowMask);
    }
}

