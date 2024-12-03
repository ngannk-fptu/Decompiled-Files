/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import java.time.Clock;
import javax.annotation.Nonnull;

public interface ClockFactory {
    @Nonnull
    public Clock makeClock();
}

