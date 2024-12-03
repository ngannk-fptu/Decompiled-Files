/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.TaskQueueWithErrorQueue
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.event.events.admin.MailQueueFlushedEvent;
import com.atlassian.confluence.mail.MailQueueManager;
import com.atlassian.core.task.TaskQueueWithErrorQueue;
import com.atlassian.event.api.EventPublisher;

public class DefaultMailQueueManager
implements MailQueueManager {
    private static final long QUEUE_WAIT_TIMEOUT = 120000L;
    private final TaskQueueWithErrorQueue queue;
    private final EventPublisher eventPublisher;

    public DefaultMailQueueManager(TaskQueueWithErrorQueue queue, EventPublisher eventPublisher) {
        this.queue = queue;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void flushQueue() {
        long wait = 0L;
        while (this.queue.isFlushing()) {
            if (wait > 120000L) {
                throw new RuntimeException("Mail queue was busy for more than two minutes. Could not flush.");
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for mail queue to flush");
            }
        }
        this.queue.flush();
        this.eventPublisher.publish((Object)new MailQueueFlushedEvent(this));
    }
}

