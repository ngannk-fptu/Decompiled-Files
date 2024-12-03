/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform;

import com.atlassian.diagnostics.internal.platform.ConsecutiveAlertGate;
import java.time.Clock;
import java.time.Duration;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class ConsecutiveAlertGateFactory {
    public ConsecutiveAlertGate createAlertGate(@Nonnull Supplier<Duration> consecutiveAlertDurationThreshold, @Nonnull Clock clock) {
        return new ConsecutiveAlertGate(consecutiveAlertDurationThreshold, clock);
    }
}

