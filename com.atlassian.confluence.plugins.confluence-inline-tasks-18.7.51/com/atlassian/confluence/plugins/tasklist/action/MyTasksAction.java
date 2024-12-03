/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.actions.AbstractUserProfileAction
 *  com.atlassian.confluence.user.actions.UserAware
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.plugins.tasklist.action;

import com.atlassian.confluence.plugins.tasklist.analytics.ViewMyTasksEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.event.api.EventPublisher;

public class MyTasksAction
extends AbstractUserProfileAction
implements UserAware {
    private EventPublisher eventPublisher;

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String execute() throws Exception {
        ConfluenceUser user = this.getUser();
        if (user == null) {
            return "error";
        }
        this.eventPublisher.publish((Object)new ViewMyTasksEvent(this));
        return "success";
    }
}

