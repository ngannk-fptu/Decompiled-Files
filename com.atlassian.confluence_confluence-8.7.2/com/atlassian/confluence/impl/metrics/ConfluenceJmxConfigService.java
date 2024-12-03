/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.composite.CompositeMeterRegistry
 *  io.micrometer.jmx.JmxMeterRegistry
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeExecution;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.monitoring.JmxConfigChangedEvent;
import com.atlassian.confluence.impl.metrics.ConfluenceJmxConfig;
import com.atlassian.confluence.impl.metrics.MicrometerFactoryBean;
import com.atlassian.confluence.impl.profiling.CollectNodeJmxMonitoringConfigs;
import com.atlassian.confluence.impl.profiling.NodeJmxMonitoringConfig;
import com.atlassian.confluence.jmx.JmxConfigService;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.UnknownFeatureException;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxMeterRegistry;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceJmxConfigService
implements JmxConfigService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceJmxConfigService.class);
    @VisibleForTesting
    static final BandanaContext JMX_CONFIG_CONTEXT = new ConfluenceBandanaContext(ConfluenceJmxConfigService.class.getName());
    @VisibleForTesting
    static final String JMX_CONFIG_KEY = "jmx.enabled.state";
    private final BandanaManager bandanaManager;
    private final CompositeMeterRegistry compositeMeterRegistry;
    private final ConfluenceJmxConfig confluenceJmxConfig;
    private final EventPublisher eventPublisher;
    private final DarkFeaturesManager darkFeaturesManager;
    private final ClusterManager clusterManager;
    private final IpdMainRegistry ipdMainRegistry;

    public ConfluenceJmxConfigService(EventPublisher eventPublisher, ConfluenceJmxConfig confluenceJmxConfig, CompositeMeterRegistry compositeMeterRegistry, BandanaManager bandanaManager, DarkFeaturesManager darkFeaturesManager, ClusterManager clusterManager, IpdMainRegistry ipdMainRegistry) {
        this.eventPublisher = eventPublisher;
        this.confluenceJmxConfig = confluenceJmxConfig;
        this.compositeMeterRegistry = compositeMeterRegistry;
        this.bandanaManager = bandanaManager;
        this.darkFeaturesManager = darkFeaturesManager;
        this.clusterManager = clusterManager;
        this.ipdMainRegistry = ipdMainRegistry;
    }

    @Override
    public boolean isJmxEnabledOnCluster() {
        Object jmxPersistedEnabledState = this.bandanaManager.getValue(JMX_CONFIG_CONTEXT, JMX_CONFIG_KEY);
        if (jmxPersistedEnabledState == null) {
            return this.confluenceJmxConfig.isJmxEnabled();
        }
        return Boolean.parseBoolean(String.valueOf(jmxPersistedEnabledState));
    }

    public boolean isAppMonitoringEnabled() {
        return !this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("com.atlassian.profiling.ita.metrics.deny");
    }

    public boolean isIpdMonitoringEnabled() {
        return !this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("confluence.in.product.diagnostics.deny");
    }

    public void setIpdMonitoringEnabled(boolean enableIpdMonitoring) {
        try {
            if (enableIpdMonitoring) {
                if (!this.isJmxEnabledOnCluster()) {
                    this.setJmxMonitoringEnabled(true);
                }
                this.darkFeaturesManager.disableSiteFeature("confluence.in.product.diagnostics.deny");
            } else {
                this.darkFeaturesManager.enableSiteFeature("confluence.in.product.diagnostics.deny");
                this.ipdMainRegistry.unregisterAllDisabledMetrics();
            }
        }
        catch (UnknownFeatureException e) {
            log.error("Could not find feature flag: {}", (Object)"confluence.in.product.diagnostics.deny", (Object)e);
        }
    }

    public void setAppMonitoringEnabled(boolean enableAppMonitoring) {
        try {
            if (enableAppMonitoring) {
                if (!this.isJmxEnabledOnCluster()) {
                    this.setJmxMonitoringEnabled(true);
                }
                this.darkFeaturesManager.disableSiteFeature("com.atlassian.profiling.ita.metrics.deny");
            } else {
                this.darkFeaturesManager.enableSiteFeature("com.atlassian.profiling.ita.metrics.deny");
            }
        }
        catch (UnknownFeatureException e) {
            log.error("Could not find feature flag: {}", (Object)"com.atlassian.profiling.ita.metrics.deny", (Object)e);
        }
    }

    public void setJmxMonitoringEnabled(boolean isJmxEnabled) {
        log.info("Updating JMX enabled state to {}", (Object)isJmxEnabled);
        this.bandanaManager.setValue(JMX_CONFIG_CONTEXT, JMX_CONFIG_KEY, (Object)isJmxEnabled);
        if (!isJmxEnabled) {
            this.setAppMonitoringEnabled(false);
            this.setIpdMonitoringEnabled(false);
        }
        JmxConfigChangedEvent jmxConfigChangedEvent = new JmxConfigChangedEvent(this, isJmxEnabled);
        this.eventPublisher.publish((Object)jmxConfigChangedEvent);
        this.handleConfigChangedEvent(jmxConfigChangedEvent);
    }

    public List<ClusterNodeExecution<NodeJmxMonitoringConfig>> getNodesJmxMonitoringConfigs() {
        return this.clusterManager.submitToAllNodes(new CollectNodeJmxMonitoringConfigs(), "cluster-manager-executor");
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent ignored) {
        if (this.isJmxDisabledPropertySet()) {
            return;
        }
        Object jmxConfigValue = this.bandanaManager.getValue(JMX_CONFIG_CONTEXT, JMX_CONFIG_KEY);
        if (jmxConfigValue != null) {
            boolean isJmxEnabled = Boolean.parseBoolean(String.valueOf(jmxConfigValue));
            this.confluenceJmxConfig.setIsJmxEnabled(isJmxEnabled);
            this.updateJmxRegistry(isJmxEnabled);
        }
    }

    @EventListener
    public void onClusteredJmxConfigChangedEvent(ClusterEventWrapper clusterEvent) {
        if (!(clusterEvent.getEvent() instanceof JmxConfigChangedEvent)) {
            return;
        }
        this.handleConfigChangedEvent((JmxConfigChangedEvent)clusterEvent.getEvent());
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
    }

    private Optional<MeterRegistry> getJmxMeterRegistry() {
        return this.compositeMeterRegistry.getRegistries().stream().filter(JmxMeterRegistry.class::isInstance).findFirst();
    }

    private void handleConfigChangedEvent(JmxConfigChangedEvent jmxConfigChangedEvent) {
        if (this.isJmxDisabledPropertySet()) {
            log.info("The jmx config won't be updated on this node, since it is configured via system properties.");
            return;
        }
        log.info("The jmx config service received an event that the config has changed to {}", (Object)jmxConfigChangedEvent.getJmxEnabled());
        this.confluenceJmxConfig.setIsJmxEnabled(jmxConfigChangedEvent.getJmxEnabled());
        this.updateJmxRegistry(jmxConfigChangedEvent.getJmxEnabled());
    }

    private boolean isJmxDisabledPropertySet() {
        return System.getProperty("confluence.jmx.disabled") != null;
    }

    private void updateJmxRegistry(boolean newJmxEnabledState) {
        if (newJmxEnabledState) {
            this.addJmxRegistry();
        } else {
            this.removeJmxRegistry();
        }
    }

    private void addJmxRegistry() {
        Optional<MeterRegistry> jmxMeterRegistry = this.getJmxMeterRegistry();
        log.info("Attempting to add JMX registry. The registry state is {}.", (Object)jmxMeterRegistry.isPresent());
        if (!jmxMeterRegistry.isPresent()) {
            this.compositeMeterRegistry.add((MeterRegistry)MicrometerFactoryBean.createJmxRegistry(this.confluenceJmxConfig));
        }
    }

    private void removeJmxRegistry() {
        Optional<MeterRegistry> jmxMeterRegistry = this.getJmxMeterRegistry();
        log.info("Attempting to remove JMX registry. The registry state is {}.", (Object)jmxMeterRegistry.isPresent());
        jmxMeterRegistry.ifPresent(MeterRegistry::close);
        jmxMeterRegistry.ifPresent(arg_0 -> ((CompositeMeterRegistry)this.compositeMeterRegistry).remove(arg_0));
    }
}

