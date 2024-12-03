/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.periodic.analytics.jobs;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.confluence.plugins.periodic.event.ConfluenceDailyStatisticsAnalyticsEventSupplier;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="confluenceDailyStatisticsPublisherJob")
public class ConfluenceDailyStatisticsPublisherJob
implements JobRunner {
    private final BundleContext bundleContext;
    private final EventPublisher eventPublisher;
    private final ConfluenceDailyStatisticsAnalyticsEventSupplier eventSupplier;
    private final ExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(ConfluenceDailyStatisticsPublisherJob.class);
    private final int timeout = Integer.getInteger("confluence.daily.statistics.task.timeout", 600000);

    @Autowired
    ConfluenceDailyStatisticsPublisherJob(@ComponentImport EventPublisher eventPublisher, BundleContext bundleContext, ConfluenceDailyStatisticsAnalyticsEventSupplier eventSupplier) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.bundleContext = Objects.requireNonNull(bundleContext);
        this.eventSupplier = Objects.requireNonNull(eventSupplier);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean shouldCollectAnalytics() {
        boolean shouldCollectAnalytics = false;
        ServiceReference serviceReference = this.bundleContext.getServiceReference("com.atlassian.analytics.api.services.AnalyticsConfigService");
        if (serviceReference != null) {
            try {
                AnalyticsConfigService analyticsConfigService = (AnalyticsConfigService)this.bundleContext.getService(serviceReference);
                shouldCollectAnalytics = analyticsConfigService == null ? false : analyticsConfigService.canCollectAnalytics();
            }
            finally {
                this.bundleContext.ungetService(serviceReference);
            }
        }
        return shouldCollectAnalytics;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if (!this.shouldCollectAnalytics()) {
            return JobRunnerResponse.aborted((String)"Can't collect analytics.");
        }
        Future future = this.executorService.submit(this.eventSupplier);
        try {
            PeriodicEvent periodicEvent = (PeriodicEvent)future.get(this.timeout, TimeUnit.MILLISECONDS);
            this.eventPublisher.publish((Object)periodicEvent);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            future.cancel(true);
            if (e instanceof TimeoutException) {
                return JobRunnerResponse.failed((String)String.format("%s took longer than %d ms to supply the event.", this.eventSupplier.getClass().getName(), this.timeout));
            }
            this.logger.debug("Unable to collect statistics", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }
}

