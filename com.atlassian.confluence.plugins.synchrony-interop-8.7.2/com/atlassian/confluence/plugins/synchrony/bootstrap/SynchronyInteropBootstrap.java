/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.event.events.plugin.PluginDisableEvent
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.plugin.PluginDisableEvent;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.api.events.SynchronyRestartedEvent;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={LifecycleAware.class})
public class SynchronyInteropBootstrap
implements LifecycleAware,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SynchronyInteropBootstrap.class);
    private final SynchronyProcessManager processManager;
    private EventPublisher eventPublisher;
    private final BootstrapManager bootstrapManager;

    @Autowired
    public SynchronyInteropBootstrap(SynchronyProcessManager processManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport BootstrapManager bootstrapManager) {
        this.processManager = processManager;
        this.eventPublisher = eventPublisher;
        this.bootstrapManager = bootstrapManager;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
        this.processManager.stop();
    }

    @EventListener
    public void onGlobalSettingsChangeEvent(GlobalSettingsChangedEvent globalSettingsChangedEvent) {
        String newBaseUrl;
        String oldBaseUrl = globalSettingsChangedEvent.getOldSettings().getBaseUrl();
        if (!oldBaseUrl.equalsIgnoreCase(newBaseUrl = globalSettingsChangedEvent.getNewSettings().getBaseUrl())) {
            this.processManager.restart().done(result -> this.eventPublisher.publish((Object)new SynchronyRestartedEvent(new Object(), (boolean)result))).fail(error -> this.eventPublisher.publish((Object)new SynchronyRestartedEvent(new Object(), false)));
        }
    }

    @EventListener
    public void onGlobalSettingsChangeEvent(ClusterEventWrapper wrapper) {
        if (wrapper.getEvent() instanceof GlobalSettingsChangedEvent) {
            this.onGlobalSettingsChangeEvent((GlobalSettingsChangedEvent)wrapper.getEvent());
        }
    }

    public void onStart() {
        if (this.bootstrapManager.isSetupComplete()) {
            try {
                this.processManager.startup().get(30L, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                log.warn("An exception occurred while waiting for Synchrony to start: {}", (Object)e.getMessage());
                log.debug("Details: ", (Throwable)e);
            }
        }
    }

    public void onStop() {
        this.processManager.stop();
    }

    @EventListener
    public void onPluginStopped(PluginDisableEvent pluginDisableEvent) {
        if ("com.atlassian.confluence.plugins.synchrony-interop".equals(pluginDisableEvent.getPluginKey())) {
            this.processManager.stop();
        }
    }

    @EventListener
    public void onPluginStopped(ClusterEventWrapper wrapper) {
        if (wrapper.getEvent() instanceof PluginDisableEvent) {
            this.onPluginStopped((PluginDisableEvent)wrapper.getEvent());
        }
    }
}

