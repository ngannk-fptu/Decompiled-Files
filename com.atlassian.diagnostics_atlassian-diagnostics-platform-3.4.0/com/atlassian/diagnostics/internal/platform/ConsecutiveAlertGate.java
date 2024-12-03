/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.diagnostics.internal.platform;

import com.atlassian.diagnostics.internal.platform.DiagnosticAlert;
import com.atlassian.diagnostics.internal.platform.monitor.DurationUtils;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ConsecutiveAlertGate {
    private final Duration timeLimit;
    private final Clock clock;
    private Instant lastExecution;

    ConsecutiveAlertGate(Supplier<Duration> consecutiveAlertDurationThreshold, Clock clock) {
        this.timeLimit = consecutiveAlertDurationThreshold.get();
        this.clock = clock;
        this.lastExecution = clock.instant();
    }

    public boolean shouldRaiseAlert(DiagnosticAlert alert) {
        if (alert.isRaised() && this.hasAlertTimeLimitBeenReached()) {
            this.lastExecution = this.clock.instant();
            return true;
        }
        return false;
    }

    private boolean hasAlertTimeLimitBeenReached() {
        return DurationUtils.durationOf(Duration.between(this.lastExecution, this.clock.instant())).isGreaterThanOrEqualTo(this.timeLimit);
    }
}

