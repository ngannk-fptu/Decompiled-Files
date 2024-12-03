/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.queue.MailQueueItem
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.notifications.mail.services;

import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.mail.MailException;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.pats.notifications.mail.TokenMail;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import java.util.Date;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceMailService
extends ProductMailService {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceMailService.class);
    public static final String MAIL_QUEUE_NAME = "mail";
    private final MultiQueueTaskManager multiQueueTaskManager;

    public ConfluenceMailService(MultiQueueTaskManager multiQueueTaskManager) {
        this.multiQueueTaskManager = multiQueueTaskManager;
    }

    @Override
    public void sendMail(@Nonnull TokenMail mail) {
        logger.trace("Sending email: [{}]", (Object)mail);
        this.multiQueueTaskManager.addTask(MAIL_QUEUE_NAME, (Task)new ConfluenceMailQueueItem(this.getQueueItem(mail)));
    }

    private static class ConfluenceMailQueueItem
    implements MailQueueItem,
    Task {
        private final MailQueueItem delegate;

        private ConfluenceMailQueueItem(MailQueueItem delegate) {
            this.delegate = delegate;
        }

        public void execute() throws Exception {
            this.delegate.send();
        }

        public void send() throws MailException {
            this.delegate.send();
        }

        public String getSubject() {
            return this.delegate.getSubject();
        }

        public Date getDateQueued() {
            return this.delegate.getDateQueued();
        }

        public int getSendCount() {
            return this.delegate.getSendCount();
        }

        public boolean hasError() {
            return this.delegate.hasError();
        }

        public int compareTo(MailQueueItem o) {
            return this.delegate.compareTo((Object)o);
        }
    }
}

