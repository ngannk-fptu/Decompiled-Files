/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.task.TaskQueueWithErrorQueue
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.mail;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.admin.MailErrorQueueDeletedEvent;
import com.atlassian.confluence.event.events.admin.MailErrorQueueResentEvent;
import com.atlassian.confluence.event.events.admin.MailQueueFlushedEvent;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.core.task.TaskQueueWithErrorQueue;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class ViewMailQueueAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ViewMailQueueAction.class);
    private String page = "";
    private TaskQueueWithErrorQueue queue;
    private EventPublisher eventPublisher;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public TaskQueueWithErrorQueue getMailQueue() {
        return this.queue;
    }

    public String doFlush() {
        log.debug("Flushing mail queue ...");
        if (!this.getMailQueue().isFlushing()) {
            log.debug("Sending queue...");
            this.getMailQueue().flush();
            this.eventPublisher.publish((Object)new MailQueueFlushedEvent(this));
        } else {
            log.warn("Queue was sending - skipped mail flush this time.");
        }
        return "success";
    }

    public String doResend() {
        TaskQueueWithErrorQueue mailQueue = this.getMailQueue();
        Collection errorItems = mailQueue.getErrorQueue().getTasks();
        errorItems.forEach(arg_0 -> ((TaskQueueWithErrorQueue)mailQueue).addTask(arg_0));
        mailQueue.getErrorQueue().clear();
        this.getMailQueue().flush();
        this.eventPublisher.publish((Object)new MailErrorQueueResentEvent(this));
        return "success";
    }

    public String doDeleteErrorQueue() {
        this.getMailQueue().getErrorQueue().clear();
        this.eventPublisher.publish((Object)new MailErrorQueueDeletedEvent(this));
        return "success";
    }

    public String doDeleteQueue() {
        this.getMailQueue().clear();
        return "success";
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setMailTaskQueue(TaskQueueWithErrorQueue queue) {
        this.queue = queue;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

