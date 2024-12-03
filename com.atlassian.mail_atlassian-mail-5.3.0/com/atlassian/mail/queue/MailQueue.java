/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.queue;

import com.atlassian.mail.queue.MailQueueItem;
import java.sql.Timestamp;
import java.util.Queue;

public interface MailQueue {
    public void sendBuffer();

    public void sendBufferBlocking();

    public int size();

    public int errorSize();

    public void addItem(MailQueueItem var1);

    public void addErrorItem(MailQueueItem var1);

    public Queue<MailQueueItem> getQueue();

    public Queue<MailQueueItem> getErrorQueue();

    public boolean isSending();

    public Timestamp getSendingStarted();

    default public Timestamp getLastSendingAttempt() {
        throw new UnsupportedOperationException();
    }

    public void emptyErrorQueue();

    public void resendErrorQueue();

    public MailQueueItem getItemBeingSent();

    public void unstickQueue();
}

