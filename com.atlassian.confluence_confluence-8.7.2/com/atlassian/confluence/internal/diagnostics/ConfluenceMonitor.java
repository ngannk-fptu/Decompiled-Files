/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.diagnostics.ComponentMonitor
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.diagnostics.ComponentMonitor;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
abstract class ConfluenceMonitor
extends InitializingMonitor {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceMonitor.class);
    private volatile MonitoringService monitoringService;
    private volatile Thread monitorThread;

    ConfluenceMonitor() {
    }

    protected void startMonitorThread(Runnable monitoringRunnable, String threadName) {
        Thread thread = new Thread(monitoringRunnable, threadName);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> log.error(threadName + " monitor thread crashed", e));
        thread.start();
        this.monitorThread = thread;
    }

    public void init(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    protected abstract String getMonitorId();

    @PreDestroy
    public void preDestroy() {
        if (this.monitorThread != null) {
            this.monitorThread.interrupt();
            this.monitorThread = null;
        }
        if (this.monitoringService != null) {
            this.monitoringService.destroyMonitor(this.getMonitorId());
            this.monitoringService = null;
        }
        log.debug("{} monitor has been destroyed", (Object)this.getMonitorId());
    }

    @VisibleForTesting
    void setMonitor(ComponentMonitor monitor) {
        this.monitor = monitor;
    }
}

