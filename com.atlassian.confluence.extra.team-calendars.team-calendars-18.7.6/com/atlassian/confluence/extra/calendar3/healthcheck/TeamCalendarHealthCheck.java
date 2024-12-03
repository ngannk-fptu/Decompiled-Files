/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.core.Application
 *  com.atlassian.healthcheck.core.HealthCheck
 *  com.atlassian.healthcheck.core.HealthStatus
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.healthcheck;

import com.atlassian.confluence.extra.calendar3.healthcheck.HealthStatusImpl;
import com.atlassian.confluence.extra.calendar3.healthcheck.TeamCalendarHealthCheckService;
import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.healthcheck.core.HealthStatus;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamCalendarHealthCheck
implements HealthCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamCalendarHealthCheck.class);
    private static final AtomicReference<String> prevExceptionDetailRef = new AtomicReference();
    private TeamCalendarHealthCheckService healthCheckService;

    public TeamCalendarHealthCheck(TeamCalendarHealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    public HealthStatus check() {
        try {
            HealthStatus status = this.healthCheckService.doHealthCheck();
            prevExceptionDetailRef.set(null);
            return status;
        }
        catch (Exception ex) {
            String exceptionDetail = String.format("%s: %s.", ex.getClass().getName(), ex.getMessage());
            String prevExceptionDetail = prevExceptionDetailRef.getAndSet(exceptionDetail);
            if (prevExceptionDetail == null || !prevExceptionDetail.equals(exceptionDetail)) {
                LOGGER.error("Health check failure", (Throwable)ex);
            }
            return new HealthStatusImpl("Team Calendars for Confluence", "Checks that Team Calendar plugin is healthy", false, exceptionDetail, Application.Plugin);
        }
    }
}

