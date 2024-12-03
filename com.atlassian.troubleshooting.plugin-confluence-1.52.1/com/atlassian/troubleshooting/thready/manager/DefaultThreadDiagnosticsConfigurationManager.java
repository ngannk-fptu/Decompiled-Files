/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.thready.manager;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.ClusterMessagingService;
import com.atlassian.troubleshooting.api.ListenerRegistration;
import com.atlassian.troubleshooting.cluster.JsonSerialiser;
import com.atlassian.troubleshooting.stp.ApplicationVersionInfo;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.thready.manager.ConfigurationPersistenceService;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultThreadDiagnosticsConfigurationManager
implements ThreadDiagnosticsConfigurationManager,
LifecycleAware {
    public static final String CHANNEL_NAME = "thread_diag_config";
    @VisibleForTesting
    protected static final List<ApplicationType> APPS_DISABLED_DEFAULT = Lists.newArrayList((Object[])new ApplicationType[]{ApplicationType.BITBUCKET, ApplicationType.STASH});
    private final ClusterMessagingService clusterMessagingService;
    private final JsonSerialiser jsonSerialiser;
    private final ConfigurationPersistenceService configurationPersistenceService;
    private final EventPublisher eventPublisher;
    private final ApplicationVersionInfo applicationVersionInfo;
    private ThreadDiagnosticsConfigurationManager.Configuration configuration;
    private ListenerRegistration listenerRegistration;

    @Autowired
    public DefaultThreadDiagnosticsConfigurationManager(ClusterMessagingService clusterMessagingService, JsonSerialiser jsonSerialiser, ConfigurationPersistenceService configurationPersistenceService, EventPublisher eventPublisher, ApplicationVersionInfo applicationVersionInfo) {
        this.clusterMessagingService = Objects.requireNonNull(clusterMessagingService);
        this.jsonSerialiser = Objects.requireNonNull(jsonSerialiser);
        this.configurationPersistenceService = Objects.requireNonNull(configurationPersistenceService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.applicationVersionInfo = Objects.requireNonNull(applicationVersionInfo);
    }

    @Override
    public ThreadDiagnosticsConfigurationManager.Configuration getConfiguration() {
        return this.configuration;
    }

    public void onStart() {
        this.configuration = this.configurationPersistenceService.findConfiguration().orElseGet(() -> {
            boolean enabled = !APPS_DISABLED_DEFAULT.contains((Object)this.applicationVersionInfo.getApplicationType());
            return this.configurationPersistenceService.storeConfiguration(new ThreadDiagnosticsConfigurationManager.Configuration(enabled));
        });
        this.listenerRegistration = this.clusterMessagingService.registerListener(CHANNEL_NAME, message -> {
            this.configuration = this.jsonSerialiser.fromJson((String)message, ThreadDiagnosticsConfigurationManager.Configuration.class);
        });
    }

    public void onStop() {
        this.listenerRegistration.unregister();
    }

    @Override
    public void setConfiguration(ThreadDiagnosticsConfigurationManager.Configuration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
        this.configurationPersistenceService.storeConfiguration(configuration);
        this.clusterMessagingService.sendMessage(CHANNEL_NAME, this.jsonSerialiser.toJson(configuration));
        this.eventPublisher.publish((Object)new ThreadDiagnosticsConfigurationEvent(configuration));
    }

    @Override
    public boolean isThreadNameAttributesEnabled() {
        return this.getConfiguration() != null && this.getConfiguration().isEnabled();
    }

    @EventName(value="troubleshooting.threaddiagnostics.config.change")
    public static class ThreadDiagnosticsConfigurationEvent {
        private final boolean enabled;

        public ThreadDiagnosticsConfigurationEvent(ThreadDiagnosticsConfigurationManager.Configuration configuration) {
            this.enabled = configuration.isEnabled();
        }

        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

