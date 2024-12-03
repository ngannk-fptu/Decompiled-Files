/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.schedule;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ScheduledJobConfiguration
implements Serializable {
    private AtomicBoolean enabled = new AtomicBoolean(true);
    private AtomicReference<String> cronSchedule = new AtomicReference();
    private AtomicReference<Long> repeatInterval = new AtomicReference();

    public boolean isEnabled() {
        return this.enabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public @Nullable String getCronSchedule() {
        return this.cronSchedule.get();
    }

    public void setCronSchedule(@Nullable String cronSchedule) {
        this.cronSchedule.set(cronSchedule);
    }

    public @Nullable Long getRepeatInterval() {
        return this.repeatInterval.get();
    }

    public void setRepeatInterval(@Nullable Long repeatInterval) {
        this.repeatInterval.set(repeatInterval);
    }
}

