/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.distribution.pause;

import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.time.Duration;

public class ClockDriftPauseDetector
implements PauseDetector {
    private final Duration sleepInterval;
    private final Duration pauseThreshold;

    public ClockDriftPauseDetector(Duration sleepInterval, Duration pauseThreshold) {
        this.sleepInterval = sleepInterval;
        this.pauseThreshold = pauseThreshold;
    }

    public Duration getSleepInterval() {
        return this.sleepInterval;
    }

    public Duration getPauseThreshold() {
        return this.pauseThreshold;
    }
}

