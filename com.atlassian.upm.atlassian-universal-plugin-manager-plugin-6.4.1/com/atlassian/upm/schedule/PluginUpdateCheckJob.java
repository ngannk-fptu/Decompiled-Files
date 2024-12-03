/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 */
package com.atlassian.upm.schedule;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.notification.PluginUpdateChecker;
import com.atlassian.upm.schedule.AbstractUpmScheduledJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class PluginUpdateCheckJob
extends AbstractUpmScheduledJob {
    private static final int BUSINESS_HOURS_START = 6;
    private static final int BUSINESS_HOURS_END = 20;
    private final PluginUpdateChecker updateChecker;

    public PluginUpdateCheckJob(PluginUpdateChecker updateChecker, UpmScheduler scheduler) {
        super(scheduler);
        this.updateChecker = Objects.requireNonNull(updateChecker, "updateChecker");
    }

    @Override
    public DateTime getStartTime() {
        return PluginUpdateCheckJob.getStartTimeAtRandomTimeOutsideHours(6, 20);
    }

    @Override
    public Option<Duration> getInterval() {
        return Option.some(Duration.standardDays((long)1L));
    }

    @Override
    public void executeInternal(UpmScheduler.RunMode runMode) throws Exception {
        boolean triggeredByUser = runMode == UpmScheduler.RunMode.TRIGGERED_BY_USER;
        boolean installAutoUpdates = !triggeredByUser && runMode != UpmScheduler.RunMode.TRIGGERED_BY_UPM_ENABLEMENT;
        this.updateChecker.checkForUpdates(PluginUpdateChecker.UpdateCheckOptions.options().userInitiated(triggeredByUser).updateNotifications(true).installAutoUpdates(installAutoUpdates));
    }
}

