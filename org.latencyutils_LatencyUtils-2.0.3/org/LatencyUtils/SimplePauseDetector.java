/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.LatencyUtils.PauseDetector;
import org.LatencyUtils.TimeServices;

public class SimplePauseDetector
extends PauseDetector {
    private static final long DEFAULT_SleepInterval = 1000000L;
    private static final long DEFAULT_PauseNotificationThreshold = 1000000L;
    private static final int DEFAULT_NumberOfDetectorThreads = 3;
    private static final boolean DEFAULT_Verbose = false;
    private final long sleepInterval;
    private final long pauseNotificationThreshold;
    final AtomicLong consensusLatestTime = new AtomicLong();
    private volatile long stallThreadMask = 0L;
    private volatile long stopThreadMask = 0L;
    private final SimplePauseDetectorThread[] detectors;
    private boolean verbose;

    public SimplePauseDetector(long sleepInterval, long pauseNotificationThreshold, int numberOfDetectorThreads) {
        this(sleepInterval, pauseNotificationThreshold, numberOfDetectorThreads, false);
    }

    public SimplePauseDetector(long sleepInterval, long pauseNotificationThreshold, int numberOfDetectorThreads, boolean verbose) {
        this.sleepInterval = sleepInterval;
        this.pauseNotificationThreshold = pauseNotificationThreshold;
        this.verbose = verbose;
        this.detectors = new SimplePauseDetectorThread[numberOfDetectorThreads];
        for (int i = 0; i < numberOfDetectorThreads; ++i) {
            this.detectors[i] = new SimplePauseDetectorThread(i);
            this.detectors[i].start();
        }
    }

    public SimplePauseDetector() {
        this(1000000L, 1000000L, 3, false);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void shutdown() {
        this.stopThreadMask = -1L;
        for (SimplePauseDetectorThread detector : this.detectors) {
            detector.interrupt();
        }
        super.shutdown();
    }

    public void stallDetectorThreads(long threadNumberMask, long stallLength) throws InterruptedException {
        long savedMask = this.stallThreadMask;
        this.stallThreadMask = threadNumberMask;
        long startTime = TimeServices.nanoTime();
        long endTime = startTime + stallLength;
        long remainingTime = stallLength;
        while (remainingTime > 0L) {
            long timeDelta = Math.min(remainingTime, this.pauseNotificationThreshold / 2L);
            TimeServices.moveTimeForward(timeDelta);
            TimeUnit.NANOSECONDS.sleep(50000L);
            remainingTime = endTime - TimeServices.nanoTime();
        }
        this.stallThreadMask = savedMask;
    }

    public void skipConsensusTimeTo(long newConsensusTime) {
        this.consensusLatestTime.set(newConsensusTime);
    }

    private class SimplePauseDetectorThread
    extends Thread {
        volatile long observedLasUpdateTime;
        final int threadNumber;
        final long threadMask;

        SimplePauseDetectorThread(int threadNumber) {
            if (threadNumber < 0 || threadNumber > 63) {
                throw new IllegalArgumentException("threadNumber must be between 0 and 63.");
            }
            this.threadNumber = threadNumber;
            this.threadMask = 1 << threadNumber;
            this.setDaemon(true);
            this.setName("SimplePauseDetectorThread_" + threadNumber);
        }

        @Override
        public void run() {
            long now;
            long shortestObservedTimeAroundLoop = Long.MAX_VALUE;
            this.observedLasUpdateTime = SimplePauseDetector.this.consensusLatestTime.get();
            long prevNow = now = TimeServices.nanoTime();
            SimplePauseDetector.this.consensusLatestTime.compareAndSet(this.observedLasUpdateTime, now);
            while ((SimplePauseDetector.this.stopThreadMask & this.threadMask) == 0L) {
                if (SimplePauseDetector.this.sleepInterval != 0L) {
                    TimeServices.sleepNanos(SimplePauseDetector.this.sleepInterval);
                }
                while ((SimplePauseDetector.this.stallThreadMask & this.threadMask) != 0L) {
                }
                this.observedLasUpdateTime = SimplePauseDetector.this.consensusLatestTime.get();
                now = TimeServices.nanoTime();
                shortestObservedTimeAroundLoop = Math.min(now - prevNow, shortestObservedTimeAroundLoop);
                while (now > this.observedLasUpdateTime) {
                    if (SimplePauseDetector.this.consensusLatestTime.compareAndSet(this.observedLasUpdateTime, now)) {
                        long deltaTimeNs = now - this.observedLasUpdateTime;
                        long hiccupTime = Math.max(deltaTimeNs - shortestObservedTimeAroundLoop, 0L);
                        if (hiccupTime <= SimplePauseDetector.this.pauseNotificationThreshold) continue;
                        if (SimplePauseDetector.this.verbose) {
                            System.out.println("SimplePauseDetector thread " + this.threadNumber + ": sending pause notification message: pause of " + hiccupTime + " nsec detected at nanoTime: " + now);
                        }
                        SimplePauseDetector.this.notifyListeners(hiccupTime, now);
                        continue;
                    }
                    this.observedLasUpdateTime = SimplePauseDetector.this.consensusLatestTime.get();
                }
                prevNow = now;
            }
            if (SimplePauseDetector.this.verbose) {
                System.out.println("SimplePauseDetector thread " + this.threadNumber + " terminating...");
            }
        }
    }
}

