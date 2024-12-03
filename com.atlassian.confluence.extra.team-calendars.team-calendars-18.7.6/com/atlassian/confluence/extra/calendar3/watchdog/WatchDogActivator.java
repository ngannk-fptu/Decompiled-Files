/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="singleton")
public class WatchDogActivator {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchDogActivator.class);
    private static final String TC_PLUGIN_KEY = "com.atlassian.confluence.extra.team-calendars";
    private final AsynchronousTaskExecutor asynchronousTaskExecutor;
    private final WatchDogService watchDogService;
    private final EventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public WatchDogActivator(AsynchronousTaskExecutor asynchronousTaskExecutor, @ComponentImport TransactionTemplate transactionTemplate, WatchDogService watchDogService, @ComponentImport EventPublisher eventPublisher) {
        this.asynchronousTaskExecutor = asynchronousTaskExecutor;
        this.watchDogService = watchDogService;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
    }

    public void startWatchDog() {
        LOGGER.info("Starting Watch Dog Service ");
        this.asynchronousTaskExecutor.submit(() -> {
            this.transactionTemplate.execute(this.watchDogService::startService);
            LOGGER.info("Watch Dog Service has finished checking");
            return null;
        });
        LOGGER.info("Starting Watch Dog Service ===> completed");
    }
}

