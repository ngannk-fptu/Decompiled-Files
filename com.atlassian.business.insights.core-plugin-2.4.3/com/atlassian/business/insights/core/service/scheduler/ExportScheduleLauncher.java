/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.business.insights.core.plugin.CorePluginInfo;
import com.atlassian.business.insights.core.service.api.ExportScheduleService;
import com.atlassian.business.insights.core.service.api.ScheduleConfigService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ExportScheduleLauncher
implements LifecycleAware,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ExportScheduleLauncher.class);
    private final Set<LifecycleEvent> lifecycleEvents = EnumSet.noneOf(LifecycleEvent.class);
    private final AtomicBoolean isPluginStopped = new AtomicBoolean(false);
    private final ScheduleConfigService scheduleConfigService;
    private final EventPublisher eventPublisher;
    private final ExportScheduleService exportScheduleService;
    private final String platformId;
    private final String pluginKey;

    public ExportScheduleLauncher(ScheduleConfigService scheduleConfigService, ExportScheduleService exportScheduleService, EventPublisher eventPublisher, CorePluginInfo pluginInfo, ApplicationProperties applicationProperties) {
        this.scheduleConfigService = scheduleConfigService;
        this.exportScheduleService = exportScheduleService;
        this.eventPublisher = eventPublisher;
        this.pluginKey = pluginInfo.getPluginKey();
        this.platformId = applicationProperties.getPlatformId();
    }

    public void onStart() {
        this.onLifecycleEvent(LifecycleEvent.LIFECYCLE_AWARE_ON_START);
    }

    public synchronized void onStop() {
        if (!this.isPluginStopped.get()) {
            log.info("ExportScheduleLauncher is about to be destroyed. Unregistering event publisher and job runner.");
            this.eventPublisher.unregister((Object)this);
            this.exportScheduleService.unregisterJobRunner();
            this.isPluginStopped.set(true);
        }
    }

    public synchronized void destroy() throws Exception {
        if ("conf".equals(this.platformId)) {
            log.info("ExportScheduleLauncher is about to be destroyed, invoking onStop() for Confluence.");
            this.onStop();
        }
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (this.pluginKey.equals(event.getPlugin().getKey())) {
            this.onLifecycleEvent(LifecycleEvent.PLUGIN_ENABLED);
        }
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        if (this.pluginKey.equals(event.getPlugin().getKey())) {
            this.exportScheduleService.unregisterJobRunner();
        }
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
        this.onLifecycleEvent(LifecycleEvent.AFTER_PROPERTIES_SET);
    }

    public void launch() {
        this.exportScheduleService.registerJobRunner();
        this.scheduleConfigService.getExportSchedule().ifPresent(this.exportScheduleService::scheduleJob);
    }

    private void onLifecycleEvent(LifecycleEvent event) {
        if (this.isLifecycleReady(event)) {
            this.launch();
        }
    }

    private synchronized boolean isLifecycleReady(LifecycleEvent event) {
        return this.lifecycleEvents.add(event) && this.lifecycleEvents.size() == LifecycleEvent.values().length;
    }

    static enum LifecycleEvent {
        AFTER_PROPERTIES_SET,
        PLUGIN_ENABLED,
        LIFECYCLE_AWARE_ON_START;

    }
}

