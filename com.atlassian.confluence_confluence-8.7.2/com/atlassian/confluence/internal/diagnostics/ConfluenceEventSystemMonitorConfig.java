/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitorConfig
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitorConfig;
import java.time.Duration;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceEventSystemMonitorConfig
implements EventSystemMonitorConfig {
    private static final int SLOW_LISTENER_SECS = Integer.getInteger("diagnostics.event.slow.listener.secs", 60);
    private static final int THREAD_DUMP_SECS = Integer.getInteger("diagnostics.event.thread.dump.secs", 10);
    private final ThreadGroup eventThreadGroup;

    public ConfluenceEventSystemMonitorConfig(ThreadGroup eventThreadGroup) {
        this.eventThreadGroup = eventThreadGroup;
    }

    public @NonNull Duration getSlowListenerAlertDuration(@Nullable AlertTrigger alertTrigger) {
        return Duration.ofSeconds(SLOW_LISTENER_SECS);
    }

    public @NonNull Duration getEventDroppedAlertThreadDumpCoolDown() {
        return Duration.ofSeconds(THREAD_DUMP_SECS);
    }

    public @NonNull Optional<ThreadGroup> getEventThreadGroup() {
        return Optional.of(this.eventThreadGroup);
    }
}

