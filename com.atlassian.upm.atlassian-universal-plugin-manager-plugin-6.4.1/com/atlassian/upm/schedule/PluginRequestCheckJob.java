/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 */
package com.atlassian.upm.schedule;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.schedule.AbstractUpmScheduledJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.util.Objects;
import org.joda.time.Duration;

public class PluginRequestCheckJob
extends AbstractUpmScheduledJob {
    private final PluginRequestNotificationChecker updateChecker;

    public PluginRequestCheckJob(PluginRequestNotificationChecker updateChecker, UpmScheduler scheduler) {
        super(scheduler);
        this.updateChecker = Objects.requireNonNull(updateChecker, "updateChecker");
    }

    @Override
    public Option<Duration> getInterval() {
        return Option.some(Duration.standardHours((long)1L));
    }

    @Override
    public void executeInternal(UpmScheduler.RunMode runMode) throws Exception {
        this.updateChecker.updatePluginRequestNotifications();
    }
}

