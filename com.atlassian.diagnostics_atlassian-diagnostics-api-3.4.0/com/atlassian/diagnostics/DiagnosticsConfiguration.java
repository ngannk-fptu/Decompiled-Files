/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import java.time.Duration;
import javax.annotation.Nonnull;

public interface DiagnosticsConfiguration {
    @Nonnull
    public Duration getAlertRetentionPeriod();

    @Nonnull
    public Duration getAlertTruncationInterval();

    public boolean isEnabled();

    @Nonnull
    public Duration getThreadDumpProducerCooldown();

    @Nonnull
    public String getNodeName();
}

