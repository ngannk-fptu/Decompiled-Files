/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.internal.AlertPublisher
 *  com.atlassian.diagnostics.internal.DefaultMonitoringService
 *  com.atlassian.diagnostics.internal.PluginHelper
 *  com.atlassian.diagnostics.internal.dao.AlertEntityDao
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.internal.AlertPublisher;
import com.atlassian.diagnostics.internal.DefaultMonitoringService;
import com.atlassian.diagnostics.internal.PluginHelper;
import com.atlassian.diagnostics.internal.dao.AlertEntityDao;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceMonitoringService
extends DefaultMonitoringService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceMonitoringService.class);
    private final EventPublisher eventPublisher;

    public ConfluenceMonitoringService(DiagnosticsConfiguration configuration, AlertEntityDao dao, I18nResolver i18nResolver, JsonMapper jsonMapper, PermissionEnforcer permissionEnforcer, PluginHelper pluginHelper, AlertPublisher publisher, SchedulerService schedulerService, TransactionTemplate transactionTemplate, EventPublisher eventPublisher) {
        super(configuration, dao, i18nResolver, jsonMapper, permissionEnforcer, pluginHelper, publisher, schedulerService, transactionTemplate);
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
        super.onStop();
        log.debug("Confluence diagnostics: alert truncating job was unregistered");
    }

    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent event) {
        super.onStart();
        log.debug("Confluence diagnostics: alert truncating job was registered");
    }

    public void onStart() {
    }

    public void onStop() {
    }
}

