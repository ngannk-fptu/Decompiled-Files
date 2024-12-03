/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.diagnostics.AlertRequest$Builder
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.LocalDocumentConversionSandbox;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxConversionFeature;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxErrorType;
import com.atlassian.diagnostics.AlertRequest;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SandboxMonitor {
    private static final Logger log = LoggerFactory.getLogger(SandboxMonitor.class);
    public static final String MONITOR_ID = "SANDBOX";
    private ComponentMonitor monitor;
    private final EventPublisher eventPublisher;
    private final DarkFeatureManager darkFeatureManager;
    private final MonitoringService monitoringService;
    private static final String DARK_FEATURE_NAME = "diagnostics.risky-monitors-enabled";
    private final AtomicBoolean riskyMonitorsEnabled = new AtomicBoolean();

    @Autowired
    public SandboxMonitor(@ComponentImport MonitoringService monitoringService, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeatureManager darkFeatureManager) {
        this.eventPublisher = eventPublisher;
        this.darkFeatureManager = darkFeatureManager;
        this.monitoringService = monitoringService;
    }

    @PostConstruct
    public void init() {
        this.monitor = this.monitoringService.createMonitor(MONITOR_ID, "diagnostics.sandbox.name");
        SandboxMonitor.defineIssue(this.monitor, SandboxErrorType.CRASHED.getIssueId(), Severity.INFO, null);
        SandboxMonitor.defineIssue(this.monitor, SandboxErrorType.KILLED.getIssueId(), Severity.INFO, null);
        this.eventPublisher.register((Object)this);
        log.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
        this.monitoringService.destroyMonitor(MONITOR_ID);
        log.debug("{} monitor has been destroyed", (Object)MONITOR_ID);
    }

    void alert(SandboxErrorType eventType, Object input, Duration duration) {
        if (input instanceof SandboxConversionRequest) {
            SandboxConversionRequest conversionRequest = (SandboxConversionRequest)input;
            if (this.isEnabled()) {
                this.monitor.getIssue(eventType.getIssueId()).ifPresent(issue -> this.monitor.alert(new AlertRequest.Builder(issue).timestamp(Instant.now()).details(() -> ImmutableMap.builder().put((Object)"username", (Object)conversionRequest.getUsername()).put((Object)"fileName", (Object)conversionRequest.getFilename()).put((Object)"fileFormat", (Object)conversionRequest.getFileFormat()).put((Object)"conversionType", (Object)conversionRequest.getConversionType()).put((Object)"fileSize", (Object)LocalDocumentConversionSandbox.getFileSize(conversionRequest.getInputFile())).put((Object)"actualTimeInSecs", (Object)duration.getSeconds()).put((Object)"timeLimitInSecs", (Object)SandboxConversionFeature.REQUEST_TIME_LIMIT_SECS).put((Object)"memoryLimitInMegabytes", (Object)SandboxConversionFeature.MEMORY_LIMIT_MEGABYTES).build()).build()));
            }
        }
    }

    private boolean isEnabled() {
        return this.riskyMonitorsEnabled.get() && this.monitor != null && this.monitor.isEnabled();
    }

    private static void defineIssue(ComponentMonitor monitor, int id, Severity severity, Class<?> detailsClass) {
        String i18nPrefix = "diagnostics.sandbox.issue." + StringUtils.leftPad((String)Integer.toString(id), (int)4, (char)'0') + ".";
        monitor.defineIssue(id).summaryI18nKey(i18nPrefix + "summary").descriptionI18nKey(i18nPrefix + "description").severity(severity).build();
    }

    @EventListener
    public void onFeatureEnabled(SiteDarkFeatureEnabledEvent event) {
        if (DARK_FEATURE_NAME.equals(event.getFeatureKey())) {
            this.riskyMonitorsEnabled.set(true);
            log.debug("Sandbox monitoring was enabled");
        }
    }

    @EventListener
    public void onFeatureDisabled(SiteDarkFeatureDisabledEvent event) {
        if (DARK_FEATURE_NAME.equals(event.getFeatureKey())) {
            this.riskyMonitorsEnabled.set(false);
            log.debug("Sandbox monitoring was disabled");
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrappedEvent) {
        Event event = wrappedEvent.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.onFeatureEnabled((SiteDarkFeatureEnabledEvent)event);
        } else if (event instanceof SiteDarkFeatureDisabledEvent) {
            this.onFeatureDisabled((SiteDarkFeatureDisabledEvent)event);
        }
    }

    @EventListener
    public void onTenantArrived(TenantArrivedEvent event) {
        this.riskyMonitorsEnabled.set(this.darkFeatureManager.getFeaturesEnabledForAllUsers().isFeatureEnabled(DARK_FEATURE_NAME));
        log.debug("Sandbox monitoring initial state: {}", (Object)(this.riskyMonitorsEnabled.get() ? "enabled" : "disabled"));
    }
}

