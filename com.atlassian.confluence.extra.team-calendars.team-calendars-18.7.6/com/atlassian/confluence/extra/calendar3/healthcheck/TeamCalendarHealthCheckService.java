/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.core.HealthStatus
 */
package com.atlassian.confluence.extra.calendar3.healthcheck;

import com.atlassian.healthcheck.core.HealthStatus;

public interface TeamCalendarHealthCheckService {
    public static final String HEALTH_CHECK_DESCRIPTION = "Checks that Team Calendar plugin is healthy";
    public static final String HEALTH_CHECK_NAME = "Team Calendars for Confluence";

    public HealthStatus doHealthCheck();
}

