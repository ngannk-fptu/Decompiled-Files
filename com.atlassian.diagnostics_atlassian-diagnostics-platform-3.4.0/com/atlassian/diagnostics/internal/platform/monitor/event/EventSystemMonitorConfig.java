/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.AlertTrigger;
import java.time.Duration;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EventSystemMonitorConfig {
    @Nonnull
    public Duration getSlowListenerAlertDuration(@Nullable AlertTrigger var1);

    @Nonnull
    public Duration getEventDroppedAlertThreadDumpCoolDown();

    @Nonnull
    default public Optional<ThreadGroup> getEventThreadGroup() {
        return Optional.empty();
    }
}

