/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.queue.MailQueueItem
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence;

import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractMailUtility;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractSupportMailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.MailQueueItemFactory;
import com.atlassian.troubleshooting.stp.salext.mail.MailServerManagerProvider;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceMailUtility
extends AbstractMailUtility {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceMailUtility.class);
    private final MultiQueueTaskManager taskManager;

    @Autowired
    public ConfluenceMailUtility(@Nonnull MultiQueueTaskManager taskManager, @Nonnull MailQueueItemFactory mailQueueItemFactory, @Nonnull MailServerManagerProvider factoryProvider) {
        super(mailQueueItemFactory, factoryProvider);
        this.taskManager = Objects.requireNonNull(taskManager);
    }

    @Override
    public void sendMail(Email email) {
        AbstractSupportMailQueueItem item = this.getMailQueueItemFactory().newSimpleSupportMailQueueItem(email);
        this.taskManager.addTask("mail", (Task)new ConfluenceMailQueueItem(item));
        LOG.debug("Added message '{}' to the Confluence mail queue...", (Object)item.getSubject());
    }

    private static class ConfluenceMailQueueItem
    implements MailQueueItem,
    Task {
        private final AbstractSupportMailQueueItem delegate;

        private ConfluenceMailQueueItem(AbstractSupportMailQueueItem delegate) {
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
            return this.delegate.compareTo(o);
        }
    }
}

