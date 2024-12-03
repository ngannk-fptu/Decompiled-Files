/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.troubleshooting.healthcheck.api;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheckUserSettings;

public interface HealthCheckUserSettingsService {
    public boolean canWatch();

    public HealthCheckUserSettings getUserSettings(UserKey var1);

    public void setSeverityForNotification(UserKey var1, SupportHealthStatus.Severity var2);
}

