/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.types.Viewed
 *  com.atlassian.confluence.user.actions.AbstractUserProfileAction
 */
package com.atlassian.confluence.plugins.tasklist.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.plugins.tasklist.action.MyTasksAction;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;

public class ViewMyTasksEvent
extends ConfluenceEvent
implements Viewed {
    public ViewMyTasksEvent(MyTasksAction src) {
        super((Object)src);
    }

    @EventName
    public String calculateEventName() {
        AbstractUserProfileAction action = (AbstractUserProfileAction)this.getSource();
        if (action.isMyProfile()) {
            return "confluence.user-profile.my.tasks.view";
        }
        return "confluence.user-profile.other.tasks.view";
    }
}

