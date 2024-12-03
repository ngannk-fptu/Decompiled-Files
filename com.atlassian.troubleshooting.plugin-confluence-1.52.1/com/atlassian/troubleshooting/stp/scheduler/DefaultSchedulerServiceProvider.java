/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.scheduler.SchedulerService
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.troubleshooting.stp.scheduler.FallbackSchedulerService;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSchedulerServiceProvider
implements SchedulerServiceProvider,
DisposableBean {
    private final PluginScheduler pluginScheduler;
    private final ServiceTracker schedulerTracker;

    @Autowired
    public DefaultSchedulerServiceProvider(BundleContext bundleContext, PluginScheduler pluginScheduler) {
        this.pluginScheduler = pluginScheduler;
        this.schedulerTracker = new ServiceTracker(bundleContext, "com.atlassian.scheduler.SchedulerService", null);
        this.schedulerTracker.open();
    }

    public void destroy() {
        this.schedulerTracker.close();
    }

    @Override
    @Nonnull
    public SchedulerService getSchedulerService() {
        SchedulerService service = (SchedulerService)this.schedulerTracker.getService();
        return service == null ? new FallbackSchedulerService(this.pluginScheduler) : service;
    }
}

