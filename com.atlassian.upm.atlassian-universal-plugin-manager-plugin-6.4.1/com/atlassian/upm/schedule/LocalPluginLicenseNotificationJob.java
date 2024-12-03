/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 */
package com.atlassian.upm.schedule;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.PluginLicenseNotificationChecker;
import com.atlassian.upm.schedule.AbstractUpmScheduledJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.util.Objects;
import org.joda.time.Duration;

public class LocalPluginLicenseNotificationJob
extends AbstractUpmScheduledJob {
    private final PluginLicenseNotificationChecker notificationChecker;
    private final NotificationCache cache;

    public LocalPluginLicenseNotificationJob(PluginLicenseNotificationChecker notificationChecker, NotificationCache cache, UpmScheduler scheduler) {
        super(scheduler);
        this.notificationChecker = Objects.requireNonNull(notificationChecker, "notificationChecker");
        this.cache = Objects.requireNonNull(cache, "cache");
    }

    @Override
    public Option<Duration> getInterval() {
        return Option.some(Duration.standardDays((long)1L));
    }

    @Override
    public void executeInternal(UpmScheduler.RunMode runMode) throws Exception {
        this.notificationChecker.updateLocalPluginLicenseNotifications();
        this.cache.resetNotificationTypeDismissal(NotificationType.NEARLY_EXPIRED_EVALUATION_PLUGIN_LICENSE);
        this.cache.resetNotificationTypeDismissal(NotificationType.MAINTENANCE_NEARLY_EXPIRED_PLUGIN_LICENSE);
    }
}

