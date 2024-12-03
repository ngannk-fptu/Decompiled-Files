/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.mail.server.SMTPMailServer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSupportMailQueueItem
implements Serializable,
MailQueueItem {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSupportMailQueueItem.class);
    private final Date dateQueued = new Date();
    private final AtomicInteger sendCount = new AtomicInteger();
    private boolean hasError = false;

    protected AbstractSupportMailQueueItem() {
    }

    protected final void send(Email email) throws MailException {
        if (!MailFactory.getSettings().isSendingDisabled()) {
            this.sendCount.incrementAndGet();
            SMTPMailServer smtpMailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
            if (smtpMailServer == null) {
                throw new MailException("No mail server found, unable to send mail.");
            }
            try {
                smtpMailServer.send(email);
                this.hasError = false;
                LOG.warn("Mail '{}' was successfully sent using the mail server '{}'.", (Object)email.getSubject(), (Object)smtpMailServer.getHostname());
            }
            catch (MailException e) {
                LOG.error("Error sending message '{}', see stack trace below for details.", (Object)email.getSubject());
                this.hasError = true;
                throw e;
            }
        }
    }

    public Date getDateQueued() {
        return this.dateQueued;
    }

    public int getSendCount() {
        return this.sendCount.get();
    }

    public boolean hasError() {
        return this.hasError;
    }

    public int compareTo(MailQueueItem o) {
        return this.dateQueued.compareTo(o.getDateQueued());
    }
}

