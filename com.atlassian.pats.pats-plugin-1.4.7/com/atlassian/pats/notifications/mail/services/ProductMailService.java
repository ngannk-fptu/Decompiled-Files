/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.mail.queue.SingleMailQueueItem
 *  javax.annotation.Nonnull
 */
package com.atlassian.pats.notifications.mail.services;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.pats.notifications.mail.TokenMail;
import javax.annotation.Nonnull;

public abstract class ProductMailService {
    public abstract void sendMail(@Nonnull TokenMail var1);

    @VisibleForTesting
    Email toEmail(@Nonnull TokenMail mail) {
        Email email = new Email(mail.getTo());
        email.setSubject(mail.getSubject());
        email.setBody(mail.getBody());
        email.setMimeType(mail.getMimeType());
        email.setEncoding(mail.getEncoding());
        return email;
    }

    public boolean isConfigured() {
        return MailFactory.getServerManager().isDefaultSMTPMailServerDefined();
    }

    public boolean isDisabled() {
        return MailFactory.getSettings().isSendingDisabled();
    }

    public MailQueueItem getQueueItem(@Nonnull TokenMail mail) {
        return new SingleMailQueueItem(this.toEmail(mail));
    }
}

