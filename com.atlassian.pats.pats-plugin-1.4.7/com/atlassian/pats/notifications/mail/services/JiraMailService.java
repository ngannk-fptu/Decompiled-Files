/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.queue.MailQueue
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.notifications.mail.services;

import com.atlassian.mail.queue.MailQueue;
import com.atlassian.pats.notifications.mail.TokenMail;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraMailService
extends ProductMailService {
    private static final Logger logger = LoggerFactory.getLogger(JiraMailService.class);
    private final MailQueue mailQueue;

    public JiraMailService(MailQueue mailQueue) {
        this.mailQueue = mailQueue;
    }

    @Override
    public void sendMail(@Nonnull TokenMail mail) {
        logger.trace("Sending email: [{}]", (Object)mail);
        this.mailQueue.addItem(this.getQueueItem(mail));
    }
}

