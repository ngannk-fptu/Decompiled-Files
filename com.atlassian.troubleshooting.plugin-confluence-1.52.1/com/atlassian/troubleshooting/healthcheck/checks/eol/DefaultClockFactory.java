/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import java.time.Clock;
import javax.annotation.Nonnull;

public class DefaultClockFactory
implements ClockFactory {
    @Override
    @Nonnull
    public Clock makeClock() {
        return Clock.systemUTC();
    }
}

