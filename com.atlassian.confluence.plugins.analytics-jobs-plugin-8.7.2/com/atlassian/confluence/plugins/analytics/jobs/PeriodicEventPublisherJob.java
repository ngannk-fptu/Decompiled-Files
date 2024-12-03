/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.analytics.jobs;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.analytics.jobs.PeriodicEventSupplierModuleDescriptor;
import com.atlassian.confluence.plugins.analytics.jobs.ServiceTrackingAnalyticsConfigService;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="periodicEventPublisherJob")
public class PeriodicEventPublisherJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(PeriodicEventPublisherJob.class);
    private final Duration supplierTimeout = Duration.ofMillis(Integer.getInteger("confluence.periodic.task.timeout", 5000).intValue());
    private final EventPublisher eventPublisher;
    private final PluginModuleTracker<PeriodicEventSupplier, PeriodicEventSupplierModuleDescriptor> eventFactoryTracker;
    private final Supplier<ExecutorService> executorSupplier;
    private final AnalyticsConfigService analyticsConfigService;

    @Autowired
    PeriodicEventPublisherJob(@ComponentImport EventPublisher eventPublisher, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager, BundleContext bundleContext) {
        this(eventPublisher, new ServiceTrackingAnalyticsConfigService(bundleContext), Executors::newSingleThreadExecutor, (PluginModuleTracker<PeriodicEventSupplier, PeriodicEventSupplierModuleDescriptor>)DefaultPluginModuleTracker.create((PluginAccessor)pluginAccessor, (PluginEventManager)pluginEventManager, PeriodicEventSupplierModuleDescriptor.class));
    }

    @VisibleForTesting
    PeriodicEventPublisherJob(EventPublisher eventPublisher, AnalyticsConfigService analyticsConfigService, Supplier<ExecutorService> executorSupplier, PluginModuleTracker<PeriodicEventSupplier, PeriodicEventSupplierModuleDescriptor> eventFactoryTracker) {
        this.eventPublisher = eventPublisher;
        this.analyticsConfigService = analyticsConfigService;
        this.eventFactoryTracker = eventFactoryTracker;
        this.executorSupplier = executorSupplier;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        return this.runJobInternal();
    }

    JobRunnerResponse runJobInternal() {
        if (this.analyticsConfigService.canCollectAnalytics()) {
            this.collectAndPublishEvents();
            return JobRunnerResponse.success();
        }
        return JobRunnerResponse.aborted((String)"Can't collect analytics.");
    }

    private void collectAndPublishEvents() {
        this.eventFactoryTracker.getModules().forEach(eventSupplier -> this.generateEvent((PeriodicEventSupplier)eventSupplier).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0)));
    }

    private Optional<PeriodicEvent> generateEvent(PeriodicEventSupplier eventSupplier) {
        log.debug("Submitting task to fetch event from {}", (Object)eventSupplier.getClass().getName());
        Future<PeriodicEvent> future = this.executorSupplier.get().submit(eventSupplier);
        log.debug("Waiting for task to supply event from {}", (Object)eventSupplier.getClass().getName());
        try {
            PeriodicEvent event = future.get(this.supplierTimeout.toMillis(), TimeUnit.MILLISECONDS);
            log.debug("Succesfully retrieved event from {}", (Object)eventSupplier.getClass().getName());
            return Optional.of(event);
        }
        catch (TimeoutException ex) {
            log.warn("{} was taking too long to supply the event. Cancelling.", (Object)eventSupplier.getClass().getName());
            future.cancel(true);
            return Optional.empty();
        }
        catch (ExecutionException e) {
            log.error("{} threw an exception while supplying the event.", (Object)eventSupplier.getClass().getName(), (Object)e);
            return Optional.empty();
        }
        catch (InterruptedException e) {
            log.error("{} was interrupted whle supplying the event. Cancelling.", (Object)eventSupplier.getClass().getName());
            return Optional.empty();
        }
    }
}

