/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsBuilder
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Function
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckUserSettingsService;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheckUserSettings;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherService;
import com.atlassian.troubleshooting.healthcheck.util.OptionalWrapper;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthCheckUserSettingsServiceImpl
implements HealthCheckUserSettingsService {
    @VisibleForTesting
    static final String SEVERITY_FOR_NOTIFICATION_KEY = "support.healthcheck.notification.severity";
    private final UserSettingsService userSettingsService;
    private final HealthCheckWatcherService watcherService;
    private final MailUtility mailUtility;

    @Autowired
    public HealthCheckUserSettingsServiceImpl(UserSettingsService userSettingsService, HealthCheckWatcherService watcherService, MailUtility mailUtility) {
        this.userSettingsService = userSettingsService;
        this.watcherService = watcherService;
        this.mailUtility = mailUtility;
    }

    @Override
    public HealthCheckUserSettings getUserSettings(UserKey userKey) {
        SupportHealthStatus.Severity severity = this.getSeverityForNotification(userKey);
        return HealthCheckUserSettings.builder().severityThreshold(severity).watching(this.watcherService.isWatching(userKey)).canWatch(this.canWatch()).build();
    }

    @Override
    public boolean canWatch() {
        return this.mailUtility.isMailServerConfigured();
    }

    private SupportHealthStatus.Severity getSeverityForNotification(UserKey userKey) {
        UserSettings settings = this.userSettingsService.getUserSettings(userKey);
        Optional<String> severityString = OptionalWrapper.fugueToJavaOptional(settings, "getString", String.class, SEVERITY_FOR_NOTIFICATION_KEY);
        return severityString.map(SupportHealthStatus.Severity::fromString).orElse(SupportHealthStatus.Severity.UNDEFINED);
    }

    @Override
    public void setSeverityForNotification(UserKey userKey, final SupportHealthStatus.Severity severity) {
        this.userSettingsService.updateUserSettings(userKey, (Function)new Function<UserSettingsBuilder, UserSettings>(){

            public UserSettings apply(UserSettingsBuilder input) {
                return input.put(HealthCheckUserSettingsServiceImpl.SEVERITY_FOR_NOTIFICATION_KEY, severity.stringValue()).build();
            }
        });
    }
}

