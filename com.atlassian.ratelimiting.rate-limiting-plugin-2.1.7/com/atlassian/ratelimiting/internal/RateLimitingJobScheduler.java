/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisablingEvent
 *  com.atlassian.plugin.event.events.PluginEvent
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.ratelimiting.internal;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisablingEvent;
import com.atlassian.plugin.event.events.PluginEvent;
import com.atlassian.ratelimiting.events.RateLimitingSettingsReloadedEvent;
import com.atlassian.ratelimiting.scheduling.ScheduledJobSource;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RateLimitingJobScheduler
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(RateLimitingJobScheduler.class);
    private final SchedulerService schedulerService;
    private final EventPublisher eventPublisher;
    private final List<ScheduledJobSource> rateLimitingJobs;
    private final String pluginKey;
    private final AtomicBoolean schedulingPerformed = new AtomicBoolean(false);

    public RateLimitingJobScheduler(SchedulerService schedulerService, EventPublisher eventPublisher, List<ScheduledJobSource> rateLimitingJobs, String pluginKey) {
        this.schedulerService = schedulerService;
        this.rateLimitingJobs = rateLimitingJobs;
        this.eventPublisher = eventPublisher;
        this.pluginKey = pluginKey;
    }

    @EventListener
    public void onSettingsReloaded(RateLimitingSettingsReloadedEvent settingsReloaded) {
        if (this.schedulingPerformed.compareAndSet(false, true)) {
            this.rateLimitingJobs.forEach(job -> {
                try {
                    log.debug("Scheduling jobs for {}", (Object)job.getJobId());
                    job.schedule(this.schedulerService);
                }
                catch (SchedulerServiceException e) {
                    log.error("Failed to schedule jobs for {}", (Object)job.getJobId().toString(), (Object)e);
                }
            });
        }
    }

    @EventListener
    public void onPluginDisabled(PluginDisablingEvent pluginDisabledEvent) {
        if (this.isRateLimitingPlugin((PluginEvent)pluginDisabledEvent)) {
            this.rateLimitingJobs.forEach(job -> {
                try {
                    log.debug("Unscheduling jobs for {}", (Object)job.getJobId());
                    job.unschedule(this.schedulerService);
                }
                catch (SchedulerServiceException e) {
                    log.error("Failed to unschedule jobs for {}", (Object)job.getJobId().toString(), (Object)e);
                }
            });
        }
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    private boolean isRateLimitingPlugin(PluginEvent pluginEnabledEvent) {
        return pluginEnabledEvent.getPlugin().getKey().equals(this.pluginKey);
    }
}

