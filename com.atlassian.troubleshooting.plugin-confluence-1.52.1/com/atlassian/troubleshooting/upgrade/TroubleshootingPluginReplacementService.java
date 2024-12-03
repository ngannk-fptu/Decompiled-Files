/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.scheduler.SchedulerService
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.upgrade;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.Message;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.troubleshooting.upgrade.UpgradeMessage;
import com.atlassian.troubleshooting.upgrade.UpgradeResultEvent;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TroubleshootingPluginReplacementService
implements LifecycleAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(TroubleshootingPluginReplacementService.class);
    private static final Set<String> OLD_PLUGIN_KEYS = Collections.unmodifiableSet(Stream.of("com.atlassian.support.healthcheck.support-healthcheck-plugin", "com.atlassian.support.stp", "com.atlassian.confluence.plugins.confluence-healthcheck-plugin", "com.atlassian.jira.plugins.jira-healthcheck-plugin", "com.atlassian.bamboo.healthcheck.bamboo-healthcheck").collect(Collectors.toSet()));
    private static final String OLD_JOB_NAME = "com.atlassian.support.healthcheck.scheduler.HealthCheckSchedulerImpl:job";
    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;
    private final SchedulerService pluginScheduler;
    private final ApplicationProperties appProps;
    private final EventPublisher eventPublisher;

    @Autowired
    public TroubleshootingPluginReplacementService(@Nonnull SchedulerService scheduler, @Nonnull PluginAccessor pluginAccessor, @Nonnull PluginController pluginController, @Nonnull ApplicationProperties appProps, @Nonnull EventPublisher eventPublisher) {
        this.pluginScheduler = Objects.requireNonNull(scheduler);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.pluginController = Objects.requireNonNull(pluginController);
        this.appProps = Objects.requireNonNull(appProps);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @VisibleForTesting
    static Set<String> getOldPluginKeys() {
        return OLD_PLUGIN_KEYS;
    }

    @VisibleForTesting
    static String getOldJobName() {
        return OLD_JOB_NAME;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Collection<Message> doUpgrade() {
        ArrayList<Message> messages = new ArrayList<Message>();
        AtomicBoolean upgradeDone = new AtomicBoolean(false);
        try {
            try {
                LOGGER.info("Unschedule old jobs...");
                this.pluginScheduler.getRegisteredJobRunnerKeys().stream().flatMap(key -> this.pluginScheduler.getJobsByJobRunnerKey(key).stream()).filter(job -> job.getJobId().toString().endsWith(TroubleshootingPluginReplacementService.getOldJobName())).forEach(job -> {
                    this.pluginScheduler.unscheduleJob(job.getJobId());
                    upgradeDone.set(true);
                });
            }
            catch (Exception e) {
                LOGGER.warn("Failed to unschedule old job", (Throwable)e);
                messages.add(UpgradeMessage.upgradeMsg("troubleshooting.upgrade.unschedule.error.msg", new Serializable[]{e.getMessage()}));
            }
            LOGGER.info("Uninstalling old plugins...");
            this.pluginAccessor.getPlugins(plugin -> TroubleshootingPluginReplacementService.getOldPluginKeys().contains(plugin.getKey())).forEach(plugin -> {
                try {
                    this.pluginController.uninstall(plugin);
                    upgradeDone.set(true);
                }
                catch (Throwable e) {
                    LOGGER.error("Failed to uninstall plugin '{}'", (Object)plugin.getKey(), (Object)e);
                    messages.add(UpgradeMessage.upgradeMsg("troubleshooting.upgrade.uninstall.error.msg", new Serializable[]{plugin.getKey(), e.getMessage()}));
                }
            });
        }
        finally {
            this.sendAnalytics(messages, upgradeDone.get());
        }
        return messages;
    }

    private void sendAnalytics(Collection<Message> messages, boolean upgradeDone) {
        if (upgradeDone) {
            this.eventPublisher.publish((Object)new UpgradeResultEvent(UpgradeResultEvent.Type.FINISHED, null));
        }
        messages.forEach(m -> this.eventPublisher.publish((Object)new UpgradeResultEvent(UpgradeResultEvent.Type.ERROR, m.getKey() + " : " + m.getArguments()[0])));
    }

    public void onStart() {
        if (!this.appProps.getDisplayName().toLowerCase().contains("bitbucket") && !this.appProps.getDisplayName().toLowerCase().contains("stash")) {
            this.doUpgrade();
        }
    }

    public void onStop() {
        this.eventPublisher.unregister((Object)this);
    }
}

