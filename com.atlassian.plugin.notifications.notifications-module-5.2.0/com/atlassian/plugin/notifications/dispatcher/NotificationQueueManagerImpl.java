/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang.builder.ToStringBuilder
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.GroupRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.queue.NotificationQueueManager;
import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.NotificationQueueMonitor;
import com.atlassian.plugin.notifications.dispatcher.SingleServerPreferences;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.UserRecipientPreferences;
import com.atlassian.plugin.notifications.dispatcher.task.AddressesNotificationTask;
import com.atlassian.plugin.notifications.dispatcher.task.GroupNotificationTask;
import com.atlassian.plugin.notifications.dispatcher.task.IndividualNotificationTask;
import com.atlassian.plugin.notifications.dispatcher.task.NotificationTaskProducer;
import com.atlassian.plugin.notifications.dispatcher.util.SystemPropertiesUtil;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

public class NotificationQueueManagerImpl
implements NotificationQueueManager,
DisposableBean,
NotificationQueueMonitor {
    private static final Logger log = Logger.getLogger(NotificationQueueManagerImpl.class);
    @VisibleForTesting
    static final String PROPERTY_NOTIFICATIONS_QUEUE_MAX_SIZE = "notifications.queue.max.size";
    private static final String PROPERTY_NOTIFICATIONS_MAX_RESEND_COUNT = "notifications.max.resend.count";
    private static final String PROPERTY_SENDER_THREAD_COUNT = "notifications.sender.thread.count";
    private static final String PROPERTY_NOTIFICATIONS_DELAY_SIZE_PER_FAILURE = "notifications.delay.per.failure";
    private static final String PROPERTY_MAX_SHUTDOWN_DELAY_SEC = "notifications.max.shutdown.delay.sec";
    private final TaskComponents components;
    private final ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory;
    private final ScheduledThreadPoolExecutor notificationSenders;
    private final int maxQueueSize;
    private final int maxResendCount;
    private final int delaySizePerFailure;
    private final int maxShutdownDelay;
    private final ConcurrentMap<String, NotificationTask> queue;

    public NotificationQueueManagerImpl(TaskComponents components, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.components = components;
        this.threadLocalDelegateExecutorFactory = threadLocalDelegateExecutorFactory;
        int senderThreadCount = SystemPropertiesUtil.parseSystemProperty(PROPERTY_SENDER_THREAD_COUNT, 3);
        this.maxResendCount = SystemPropertiesUtil.parseSystemProperty(PROPERTY_NOTIFICATIONS_MAX_RESEND_COUNT, 5);
        this.maxQueueSize = SystemPropertiesUtil.parseSystemProperty(PROPERTY_NOTIFICATIONS_QUEUE_MAX_SIZE, 1000);
        this.delaySizePerFailure = SystemPropertiesUtil.parseSystemProperty(PROPERTY_NOTIFICATIONS_DELAY_SIZE_PER_FAILURE, 120);
        this.maxShutdownDelay = SystemPropertiesUtil.parseSystemProperty(PROPERTY_MAX_SHUTDOWN_DELAY_SEC, 20);
        this.queue = new ConcurrentHashMap<String, NotificationTask>(this.maxQueueSize);
        this.notificationSenders = new ScheduledThreadPoolExecutor(senderThreadCount, ThreadFactories.namedThreadFactory((String)"NotificationSender", (ThreadFactories.Type)ThreadFactories.Type.DAEMON), (task, executor) -> {
            if (!(task instanceof NotificationTask)) {
                throw new RejectedExecutionException();
            }
            NotificationTask notificationTask = (NotificationTask)task;
            this.taskError(notificationTask);
        });
    }

    @Override
    public void processEvent(Object event) {
        if (!this.components.getServerConfigurationManager().getNotificationStatus().isEnabled()) {
            if (log.isInfoEnabled()) {
                log.info((Object)String.format("Notifications are currently disabled. Event '%s' will not be sent to any recipients.", ToStringBuilder.reflectionToString((Object)event)));
            }
            return;
        }
        int queueSize = this.queue.size();
        if (queueSize >= this.maxQueueSize) {
            this.components.getErrorRegistry().getLogger().error((Object)String.format("Notification Queue is full (%d/%d max). Try setting a higher maximum size '%s'.  Ignoring notification event '%s'.", queueSize, this.maxQueueSize, PROPERTY_NOTIFICATIONS_QUEUE_MAX_SIZE, ToStringBuilder.reflectionToString((Object)event)));
        } else {
            this.notificationSenders.submit(this.wrap(new NotificationTaskProducer(this.components, event)));
        }
    }

    private Runnable wrap(Runnable task) {
        return this.threadLocalDelegateExecutorFactory.createRunnable(task);
    }

    @Override
    public void submitIndividualNotification(Iterable<RoleRecipient> recipients, NotificationEvent event) {
        if (!this.components.getServerConfigurationManager().getNotificationStatus().isEnabled()) {
            if (log.isInfoEnabled()) {
                log.info((Object)String.format("Notifications are currently disabled. Event '%s' will not be sent to any recipients.", ToStringBuilder.reflectionToString((Object)event)));
            }
            return;
        }
        IndividualNotificationTask task = new IndividualNotificationTask(this.components, recipients, event, new UserRecipientPreferences(this.components.getUserServerManager(), this.components.getNotificationPreferencesManager()), this);
        task.setState(TaskStatus.State.QUEUED);
        this.notificationSenders.submit(this.wrap(task));
    }

    @Override
    public void submitIndividualNotificationViaAddress(Iterable<NotificationAddress> address, NotificationEvent event) {
        this.submitTask(event, () -> new AddressesNotificationTask(address, this.components, event, (NotificationQueueMonitor)this));
    }

    @Override
    public void submitIndividualNotificationViaServer(Iterable<RoleRecipient> recipients, NotificationEvent event, int serverId) {
        this.submitTask(event, () -> new IndividualNotificationTask(this.components, recipients, event, new SingleServerPreferences(this.components.getServerManager(), serverId), this));
    }

    @Override
    public void submitGroupNotification(GroupRecipient recipient, NotificationEvent event) {
        this.submitTask(event, () -> new GroupNotificationTask(this.components, recipient, event, (NotificationQueueMonitor)this));
    }

    private void submitTask(NotificationEvent event, Supplier<NotificationTask> taskSupplier) {
        if (!this.components.getServerConfigurationManager().getNotificationStatus().isEnabled()) {
            if (log.isInfoEnabled()) {
                log.info((Object)String.format("Notifications are currently disabled. Event '%s' will not be sent to any recipients.", ToStringBuilder.reflectionToString((Object)event)));
            }
            return;
        }
        NotificationTask task = taskSupplier.get();
        task.setState(TaskStatus.State.QUEUED);
        this.notificationSenders.submit(this.wrap(task));
    }

    @Override
    public void clear() {
        BlockingQueue<Runnable> taskQueue = this.notificationSenders.getQueue();
        for (Runnable runnable : taskQueue) {
            this.notificationSenders.remove(runnable);
        }
        this.queue.clear();
    }

    public void destroy() {
        List<Runnable> unproccessedTasks = null;
        this.notificationSenders.shutdown();
        try {
            if (!this.notificationSenders.awaitTermination(this.maxShutdownDelay, TimeUnit.SECONDS)) {
                unproccessedTasks = this.notificationSenders.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            unproccessedTasks = this.notificationSenders.shutdownNow();
        }
        if (unproccessedTasks != null) {
            log.warn((Object)("There are " + unproccessedTasks.size() + " notifications unsent, but the plugin is being shut down."));
        }
    }

    @Override
    public List<NotificationTask> getQueuedTasks() {
        return Lists.newArrayList(this.queue.values());
    }

    @Override
    public void taskAdded(NotificationTask task) {
        this.queue.putIfAbsent(task.getId(), task);
    }

    @Override
    public void taskCompleted(NotificationTask task) {
        this.queue.remove(task.getId());
        this.components.getErrorRegistry().removeTaskErrors(task.getId());
    }

    @Override
    public void taskError(NotificationTask task) {
        if (task.getSendCount() < this.maxResendCount) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Queueing for resend [" + task.getSendCount() + "]: " + ToStringBuilder.reflectionToString((Object)task.getEvent())));
            }
            long delayInSeconds = this.calculateDelayInSeconds(task);
            long nextAttemptTime = System.currentTimeMillis() + delayInSeconds * 1000L;
            this.notificationSenders.schedule(task, delayInSeconds, TimeUnit.SECONDS);
            task.setQueuedForRetry(nextAttemptTime);
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Giving up trying to resend: " + ToStringBuilder.reflectionToString((Object)task.getEvent())));
            }
            this.queue.remove(task.getId());
        }
    }

    private long calculateDelayInSeconds(NotificationTask task) {
        return task.getSendCount() * this.delaySizePerFailure;
    }
}

