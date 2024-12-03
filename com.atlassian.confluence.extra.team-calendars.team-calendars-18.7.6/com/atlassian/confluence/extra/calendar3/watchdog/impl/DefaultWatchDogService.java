/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogService;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceState;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogServiceStatus;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogStatusReporter;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTask;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogTaskRunner;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="singleton")
public class DefaultWatchDogService
implements WatchDogService,
WatchDogStatusReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWatchDogService.class);
    private WatchDogTaskRunner watchDogTaskRunner;
    private List<WatchDogTask> watchDogTasks;
    private String lastReportMessage;

    @Autowired
    public DefaultWatchDogService(WatchDogTaskRunner watchDogTaskRunner, List<WatchDogTask> watchDogTasks) {
        Objects.nonNull(watchDogTaskRunner);
        Objects.nonNull(watchDogTasks);
        this.watchDogTaskRunner = watchDogTaskRunner;
        this.watchDogTasks = watchDogTasks;
    }

    @Override
    public WatchDogServiceStatus startService() {
        AtomicReference<WatchDogServiceState> currentState = this.watchDogTaskRunner.getState();
        if (!currentState.compareAndSet(WatchDogServiceState.NOT_RUNNING, WatchDogServiceState.RUNNING)) {
            LOGGER.warn("Watch Dog service is already in running state. Please retry after it finish");
            return this.getStatus();
        }
        this.watchDogTaskRunner.runTasks(this.watchDogTasks, this);
        return this.getStatus();
    }

    @Override
    public WatchDogServiceStatus getStatus() {
        return new WatchDogServiceStatus(this.watchDogTaskRunner.getState().get(), this.lastReportMessage);
    }

    @Override
    public void report(String status) {
        this.lastReportMessage = status;
    }
}

