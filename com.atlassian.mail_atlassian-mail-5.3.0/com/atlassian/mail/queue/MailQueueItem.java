/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail.queue;

import com.atlassian.mail.MailException;
import java.util.Date;

public interface MailQueueItem
extends Comparable<MailQueueItem> {
    public void send() throws MailException;

    public String getSubject();

    public Date getDateQueued();

    public int getSendCount();

    public boolean hasError();
}

