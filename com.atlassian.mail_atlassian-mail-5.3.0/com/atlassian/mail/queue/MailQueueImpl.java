/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mail.queue;

import com.atlassian.mail.MailException;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.mail.queue.MailQueueItem;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailQueueImpl
implements MailQueue {
    private static final Logger log = LoggerFactory.getLogger(MailQueueImpl.class);
    private static final int MAX_SEND_ATTEMPTS = 10;
    private final Queue<MailQueueItem> items = new PriorityBlockingQueue<MailQueueItem>();
    private final Queue<MailQueueItem> errorItems = new PriorityBlockingQueue<MailQueueItem>();
    private volatile boolean sending;
    private volatile MailQueueItem itemBeingSent;
    private volatile Timestamp sendingStarted;
    private volatile Timestamp lastSendingAttempt;
    private final Lock sharedLock;
    private final Lock exclusiveLock;

    public MailQueueImpl() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.sharedLock = lock.readLock();
        this.exclusiveLock = lock.writeLock();
    }

    @Override
    public void sendBuffer() {
        this.sharedLock.lock();
        try {
            this.sendBufferUnderLock();
        }
        finally {
            this.sharedLock.unlock();
        }
    }

    @Override
    public void sendBufferBlocking() {
        this.exclusiveLock.lock();
        try {
            this.sendBufferUnderLock();
        }
        finally {
            this.exclusiveLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendBufferUnderLock() {
        if (this.sending) {
            log.warn("Already sending {} mails:", (Object)this.items.size());
            for (MailQueueItem item : this.items) {
                log.warn("Queued to send: {}, {}", (Object)item, item.getClass());
            }
            return;
        }
        this.sendingStarted();
        ArrayList<MailQueueItem> failed = new ArrayList<MailQueueItem>();
        try {
            while (!this.items.isEmpty()) {
                MailQueueItem item;
                String origThreadName = Thread.currentThread().getName();
                this.itemBeingSent = item = this.items.poll();
                this.lastSendingAttempt = new Timestamp(System.currentTimeMillis());
                log.debug("Sending: {}", (Object)item);
                try {
                    Thread.currentThread().setName("Sending mailitem " + item);
                    item.send();
                }
                catch (MailException e) {
                    if (item.getSendCount() > 10) {
                        this.errorItems.add(item);
                    } else {
                        failed.add(item);
                    }
                    log.error("Error occurred in sending e-mail: " + item, (Throwable)e);
                }
                finally {
                    Thread.currentThread().setName(origThreadName);
                }
            }
            this.items.addAll(failed);
        }
        finally {
            this.sendingStopped();
        }
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public int errorSize() {
        return this.errorItems.size();
    }

    @Override
    public void addItem(MailQueueItem item) {
        log.debug("Queued: {}", (Object)item);
        this.items.add(item);
    }

    @Override
    public void addErrorItem(MailQueueItem item) {
        log.debug("Queued error: {}", (Object)item);
        this.errorItems.add(item);
    }

    @Override
    public Queue<MailQueueItem> getQueue() {
        return this.items;
    }

    @Override
    public Queue<MailQueueItem> getErrorQueue() {
        return this.errorItems;
    }

    @Override
    public boolean isSending() {
        return this.sending;
    }

    @Override
    public Timestamp getSendingStarted() {
        return this.sendingStarted;
    }

    @Override
    public Timestamp getLastSendingAttempt() {
        return this.lastSendingAttempt;
    }

    @Override
    public MailQueueItem getItemBeingSent() {
        return this.itemBeingSent;
    }

    @Override
    public void unstickQueue() {
        log.error("Mail on queue was considered stuck: {}", (Object)this.itemBeingSent);
        this.sendingStopped();
    }

    @Override
    public void emptyErrorQueue() {
        this.errorItems.clear();
    }

    @Override
    public void resendErrorQueue() {
        this.items.addAll(this.errorItems);
        this.emptyErrorQueue();
    }

    public void sendingStarted() {
        this.sending = true;
        this.sendingStarted = new Timestamp(System.currentTimeMillis());
    }

    public void sendingStopped() {
        this.sending = false;
        this.sendingStarted = null;
        this.lastSendingAttempt = null;
    }
}

