/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringControl;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Internal
public class DefaultConfluenceMonitoringController
implements InitializingBean,
DisposableBean {
    public static final String DARK_FEATURE_SITE_KEY_FOR_DISABLE_MONITORING = "confluence-monitoring.disable";
    public static final String DARK_FEATURE_SITE_KEY_FOR_ENABLE_CPU_TIMING = "confluence-monitoring.cpu.enable";
    public static final String DARK_FEATURE_SITE_KEY_FOR_ENABLE_HIBERNATE_MONITORING = "confluence-monitoring.hibernate.enable";
    private static final ImmutableSet<String> MY_FEATURE_KEYS = ImmutableSet.builder().add((Object)"confluence-monitoring.disable").add((Object)"confluence-monitoring.cpu.enable").add((Object)"confluence-monitoring.hibernate.enable").build();
    private final ConfluenceMonitoringControl control;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final DarkFeaturesManager darkFeaturesMgr;

    public DefaultConfluenceMonitoringController(ConfluenceMonitoringControl control, EventListenerRegistrar eventListenerRegistrar, DarkFeaturesManager darkFeaturesMgr) {
        this.control = (ConfluenceMonitoringControl)Preconditions.checkNotNull((Object)control);
        this.darkFeaturesMgr = (DarkFeaturesManager)Preconditions.checkNotNull((Object)darkFeaturesMgr);
        this.eventListenerRegistrar = (EventListenerRegistrar)Preconditions.checkNotNull((Object)eventListenerRegistrar);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventListenerRegistrar.register((Object)this);
        this.control.disableMonitoring();
        this.control.disableCpuTiming();
        this.control.disableHibernateMonitoring();
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.updateMonitoringEnablement();
    }

    @EventListener
    public void onSiteDarkFeatureEnabledEvent(SiteDarkFeatureEnabledEvent event) {
        if (MY_FEATURE_KEYS.contains((Object)event.getFeatureKey())) {
            this.updateMonitoringEnablement();
        }
    }

    @EventListener
    public void onSiteDarkFeatureDisabledEvent(SiteDarkFeatureDisabledEvent event) {
        if (MY_FEATURE_KEYS.contains((Object)event.getFeatureKey())) {
            this.updateMonitoringEnablement();
        }
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof SiteDarkFeatureEnabledEvent) {
            this.onSiteDarkFeatureEnabledEvent((SiteDarkFeatureEnabledEvent)event);
        } else if (event instanceof SiteDarkFeatureDisabledEvent) {
            this.onSiteDarkFeatureDisabledEvent((SiteDarkFeatureDisabledEvent)event);
        }
    }

    private void updateMonitoringEnablement() {
        if (this.isMonitoringEnabled()) {
            this.control.enableMonitoring();
        } else {
            this.control.disableMonitoring();
        }
        if (this.isCpuTimingEnabled()) {
            this.control.enableCpuTiming();
        } else {
            this.control.disableCpuTiming();
        }
        if (this.isHibernateMonitoringEnabled()) {
            this.control.enableHibernateMonitoring();
        } else {
            this.control.disableHibernateMonitoring();
        }
    }

    private boolean isMonitoringEnabled() {
        return !this.darkFeaturesMgr.getSiteDarkFeatures().isFeatureEnabled(DARK_FEATURE_SITE_KEY_FOR_DISABLE_MONITORING);
    }

    private boolean isCpuTimingEnabled() {
        return this.isMonitoringEnabled() && this.darkFeaturesMgr.getSiteDarkFeatures().isFeatureEnabled(DARK_FEATURE_SITE_KEY_FOR_ENABLE_CPU_TIMING);
    }

    private boolean isHibernateMonitoringEnabled() {
        return this.darkFeaturesMgr.getSiteDarkFeatures().isFeatureEnabled(DARK_FEATURE_SITE_KEY_FOR_ENABLE_HIBERNATE_MONITORING);
    }
}

