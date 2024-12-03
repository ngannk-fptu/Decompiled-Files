/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.core.Application
 *  com.atlassian.healthcheck.core.HealthStatus
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.healthcheck;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.healthcheck.HealthStatusImpl;
import com.atlassian.confluence.extra.calendar3.healthcheck.TeamCalendarHealthCheckService;
import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class TeamCalendarHealthCheckServiceImpl
implements TeamCalendarHealthCheckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamCalendarHealthCheckServiceImpl.class);
    private final AtomicReference<String> prevExceptionDetailRef = new AtomicReference();
    private final CalendarManager calendarManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public TeamCalendarHealthCheckServiceImpl(@ComponentImport TransactionTemplate transactionTemplate, CalendarManager calendarManager) {
        this.transactionTemplate = transactionTemplate;
        this.calendarManager = calendarManager;
    }

    @Override
    public HealthStatus doHealthCheck() {
        return (HealthStatus)this.transactionTemplate.execute(() -> {
            HealthStatusImpl healthStatus = null;
            try {
                this.calendarManager.getSubCalendarsCount();
                this.prevExceptionDetailRef.set(null);
                healthStatus = new HealthStatusImpl("Team Calendars for Confluence", "Checks that Team Calendar plugin is healthy", true, "Team Calendar is healthy", Application.Plugin);
            }
            catch (Exception ex) {
                String exceptionDetail = String.format("%s: %s.", ex.getClass().getName(), ex.getMessage());
                String prevExceptionDetail = this.prevExceptionDetailRef.getAndSet(exceptionDetail);
                if (prevExceptionDetail == null || !prevExceptionDetail.equals(exceptionDetail)) {
                    LOGGER.error("Health check failure", (Throwable)ex);
                }
                healthStatus = new HealthStatusImpl("Team Calendars for Confluence", "Checks that Team Calendar plugin is healthy", false, exceptionDetail, Application.Plugin);
            }
            return healthStatus;
        });
    }
}

