/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.impl.NotificationQueueDispatchService;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;

final class ThreadPoolDispatchService
implements DispatchService,
DisposableBean {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forClass(ThreadPoolDispatchService.class);
    private static final String THREAD_PREFIX = NotificationQueueDispatchService.class.getName();
    private static final String PROPERTY_MAX_SHUTDOWN_DELAY_SEC = "notifications.max.shutdown.delay.sec";
    private static final int MAX_THREADS = Integer.getInteger("notifications.api.commithook.dispatch.threads", 5);
    private final int maxShutdownDelay = Integer.getInteger("notifications.max.shutdown.delay.sec", 20);
    private final ExecutorService executorService;
    private final DispatchService delegate;

    public ThreadPoolDispatchService(DispatchService delegate, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this(delegate, threadLocalDelegateExecutorFactory.createExecutorService(Executors.newFixedThreadPool(MAX_THREADS, ThreadFactories.namedThreadFactory((String)THREAD_PREFIX))));
    }

    public ThreadPoolDispatchService(DispatchService delegate, ExecutorService executorService) {
        this.executorService = executorService;
        this.delegate = delegate;
    }

    @Override
    public void dispatch(Notification notification) {
        this.executorService.submit(() -> this.delegate.dispatch(notification));
    }

    @Override
    public void dispatchWithAdditionalRecipients(Notification notification, Iterable<RoleRecipient> additionalRecipients) {
        this.executorService.submit(() -> this.delegate.dispatchWithAdditionalRecipients(notification, additionalRecipients));
    }

    @Override
    public void dispatchForExclusiveRecipients(Notification notification, Iterable<RoleRecipient> exclusiveRecipients) {
        this.executorService.submit(() -> this.delegate.dispatchForExclusiveRecipients(notification, exclusiveRecipients));
    }

    public void destroy() {
        int numberOfUnprocessedExecutions = 0;
        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(this.maxShutdownDelay, TimeUnit.SECONDS)) {
                numberOfUnprocessedExecutions = this.executorService.shutdownNow().size();
            }
        }
        catch (InterruptedException e) {
            numberOfUnprocessedExecutions = this.executorService.shutdownNow().size();
        }
        if (numberOfUnprocessedExecutions > 0) {
            log.warnOrDebug("There may be some emails still waiting to be sent on the queue, but the plugin is being shut down. %d queued notifications aborted", numberOfUnprocessedExecutions);
        }
    }
}

