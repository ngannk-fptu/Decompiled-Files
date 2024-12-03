/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.queue;

import com.atlassian.mail.MailThreader;
import com.atlassian.mail.queue.MailQueueItem;
import java.util.Date;

public abstract class AbstractMailQueueItem
implements MailQueueItem {
    String subject;
    Date dateQueued = new Date();
    private int timesSent = 0;
    protected MailThreader mailThreader;

    public AbstractMailQueueItem() {
    }

    public AbstractMailQueueItem(String subject) {
        this();
        this.subject = subject;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public Date getDateQueued() {
        return this.dateQueued;
    }

    @Override
    public int getSendCount() {
        return this.timesSent;
    }

    @Override
    public boolean hasError() {
        return this.timesSent > 0;
    }

    protected void incrementSendCount() {
        ++this.timesSent;
    }

    public void setMailThreader(MailThreader mailThreader) {
        this.mailThreader = mailThreader;
    }

    @Override
    public final int compareTo(MailQueueItem o) {
        int priorityComparator = new Integer(this.timesSent).compareTo(o.getSendCount());
        return priorityComparator == 0 ? this.dateQueued.compareTo(o.getDateQueued()) : priorityComparator;
    }
}

