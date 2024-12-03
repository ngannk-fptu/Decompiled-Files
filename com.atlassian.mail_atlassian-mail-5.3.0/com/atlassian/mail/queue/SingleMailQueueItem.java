/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail.queue;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.AbstractMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import org.apache.log4j.Logger;

public class SingleMailQueueItem
extends AbstractMailQueueItem {
    private static final Logger LOG = Logger.getLogger(SingleMailQueueItem.class);
    private final Email email;

    public SingleMailQueueItem(Email email) {
        super(email.getSubject());
        this.email = email;
    }

    @Override
    public void send() throws MailException {
        this.incrementSendCount();
        SMTPMailServer smtpMailServer = MailFactory.getServerManager().getDefaultSMTPMailServer();
        if (smtpMailServer == null) {
            LOG.debug((Object)"Not sending message as the default SMTP Mail Server is not defined.");
            return;
        }
        if (!MailFactory.getSettings().isSendingDisabled()) {
            String customMessageId = null;
            if (this.mailThreader != null) {
                this.mailThreader.threadEmail(this.email);
                customMessageId = this.mailThreader.getCustomMessageId(this.email);
            }
            smtpMailServer.sendWithMessageId(this.email, customMessageId);
            if (this.mailThreader != null) {
                this.mailThreader.storeSentEmail(this.email);
            }
        } else {
            LOG.debug((Object)"Not sending message as sending is turned off.");
        }
    }

    public Email getEmail() {
        return this.email;
    }

    public String toString() {
        return this.email != null ? this.email.toString() : "null";
    }
}

