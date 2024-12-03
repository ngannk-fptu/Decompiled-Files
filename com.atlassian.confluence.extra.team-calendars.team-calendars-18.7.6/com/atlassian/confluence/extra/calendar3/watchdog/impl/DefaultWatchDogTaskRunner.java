/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceState;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTaskRunner;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class DefaultWatchDogTaskRunner
implements WatchDogTaskRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWatchDogTaskRunner.class);
    private AtomicReference<WatchDogServiceState> currentState = new AtomicReference<WatchDogServiceState>(WatchDogServiceState.NOT_RUNNING);

    @Override
    public AtomicReference<WatchDogServiceState> getState() {
        return this.currentState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void runTasks(Collection<WatchDogTask> tasks, WatchDogStatusReporter reporter) {
        long startTime = System.currentTimeMillis();
        try {
            for (WatchDogTask task : tasks) {
                if (!task.shouldRun()) continue;
                long taskStartTime = System.currentTimeMillis();
                try {
                    LOGGER.info("Running task {}", task.getClass());
                    task.run(reporter);
                }
                catch (Exception e) {
                    LOGGER.error("An exception occurred when running the task {}", (Object)task, (Object)e);
                }
                String msg = String.format("Execution time for task %s is %d ms", task, System.currentTimeMillis() - taskStartTime);
                if (reporter != null) {
                    reporter.report(msg);
                }
                LOGGER.info(msg);
            }
            this.currentState.set(WatchDogServiceState.NOT_RUNNING);
            if (reporter == null) return;
        }
        catch (Throwable throwable) {
            this.currentState.set(WatchDogServiceState.NOT_RUNNING);
            if (reporter == null) throw throwable;
            String msg = String.format("Execution time for tasks is %d ms", System.currentTimeMillis() - startTime);
            reporter.report(msg);
            throw throwable;
        }
        String msg = String.format("Execution time for tasks is %d ms", System.currentTimeMillis() - startTime);
        reporter.report(msg);
    }
}

